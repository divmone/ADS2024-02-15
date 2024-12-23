package by.it.group351001.yakovenko.lesson15;

import java.io.File;
import java.io.IOException;
import java.nio.charset.MalformedInputException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SourceScannerA {

    protected static class MyStringComparator implements Comparator<String> {
        @Override
        public int compare(String s1, String s2) {
            int int_s1, int_s2;

            int_s1 = Integer.parseInt(s1.split(" ")[0]);
            int_s2 = Integer.parseInt(s2.split(" ")[0]);

            if (int_s1 == int_s2) {
                return s1.compareTo(s2);
            }
            return Integer.compare(int_s1, int_s2);
        }
    }

    protected static String trimWhitespace(String str) {
        int start = 0, end = str.length() - 1;
        while (start <= end && str.charAt(start) < 33) start++;
        while (end >= start && str.charAt(end) < 33) end--;
        return (start <= end) ? str.substring(start, end + 1) : "";
    }

    private static List<String> getInformation() throws IOException {
        List<String> size_directory = new ArrayList<>();

        Path src = Path.of(System.getProperty("user.dir")
                + File.separator + "src" + File.separator);

        ForkJoinPool customThreadPool = new ForkJoinPool(4); // Устанавливаем количество потоков

        try (Stream<Path> fileTrees = Files.walk(src)) {
            List<Path> paths = fileTrees.filter(p -> p.toString().endsWith(".java"))
                    .collect(Collectors.toList());

            customThreadPool.submit(() ->
                    paths.parallelStream().forEach(directory -> {
                        try {
                            String str = Files.readString(directory);
                            if (!str.contains("@Test") && !str.contains("org.junit.Test")) {
                                str = str.replaceAll("(?m)^package.*|^import.*", "");
                                str = trimWhitespace(str);
                                synchronized (size_directory) {
                                    size_directory.add(str.getBytes().length + " " + src.relativize(directory));
                                }
                            }
                        } catch (MalformedInputException e) {
                            // Игнорируем MalformedInputException
                        } catch (IOException e) {
                            System.err.println("Error reading file: " + directory);
                            e.printStackTrace();
                        }
                    })
            ).get(); // Используем get, чтобы дождаться завершения параллельной обработки

            Collections.sort(size_directory, new MyStringComparator());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            customThreadPool.shutdown();
        }

        return size_directory;
    }

    public static void main(String[] args) {
        try {
            List<String> results = getInformation();
            for (String info : results) {
                System.out.println(info);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
