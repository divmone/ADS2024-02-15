package by.it.group351003.sologub.lesson15;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;
import java.util.stream.Collectors;

public class SourceScannerB {
    public static void main(String[] args) {
        String src = System.getProperty("user.dir") + File.separator + "src" + File.separator;

        // Список для хранения информации о файлах
        List<FileInfo> fileInfos = new ArrayList<>();

        try {
            // Рекурсивно найти все файлы .java
            Files.walk(Paths.get(src))
                    .filter(path -> Files.isRegularFile(path) && path.toString().endsWith(".java"))
                    .forEach(path -> {
                        try {
                            // Прочитать содержимое файла
                            String content = Files.readString(path, StandardCharsets.UTF_8);

                            // Пропустить файлы, содержащие @Test или org.junit.Test
                            if (content.contains("@Test") || content.contains("org.junit.Test")) {
                                return;
                            }

                            // Удалить строку package и импорты
                            String modifiedContent = removePackageAndImports(content);

                            // Удалить комментарии
                            modifiedContent = removeComments(modifiedContent);

                            // Удалить символы с кодом <33 в начале и конце текста
                            modifiedContent = trimSpecialCharacters(modifiedContent);

                            // Удалить пустые строки
                            modifiedContent = removeEmptyLines(modifiedContent);

                            // Рассчитать размер текста в байтах
                            byte[] contentBytes = modifiedContent.getBytes(StandardCharsets.UTF_8);
                            int size = contentBytes.length;

                            // Релятивизация пути относительно src
                            Path srcPath = Paths.get(src).toAbsolutePath();
                            String relativePath = srcPath.relativize(path.toAbsolutePath()).toString();

                            // Добавить информацию о файле
                            fileInfos.add(new FileInfo(size, relativePath));

                        } catch (IOException e) {
                            // Ошибка чтения файла
                            System.err.println("Ошибка чтения файла (пропускаем): " + path);
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Сортировка: по размеру, затем лексикографически по пути
        fileInfos.sort(Comparator
                .comparingInt(FileInfo::getSize)
                .thenComparing(FileInfo::getPath));

        // Вывод размера и пути для каждого файла
        fileInfos.forEach(fileInfo ->
                System.out.println(fileInfo.getSize() + " " + fileInfo.getPath()));
    }

    // Метод для удаления строки package и всех импортов
    private static String removePackageAndImports(String content) {
        StringBuilder result = new StringBuilder();
        String[] lines = content.split("\n");

        for (String line : lines) {
            String trimmed = line.trim();
            // Пропустить строки package и import
            if (!trimmed.startsWith("package") && !trimmed.startsWith("import")) {
                result.append(line).append("\n");
            }
        }
        return result.toString();
    }

    // Метод для удаления комментариев (однострочные и многострочные)
    private static String removeComments(String content) {
        // Регулярные выражения для удаления однострочных и многострочных комментариев
        String regexSingleLine = "//.*";
        String regexMultiLine = "/\\*.*?\\*/";

        // Убираем комментарии, используя регулярные выражения
        content = content.replaceAll(regexSingleLine, ""); // Однострочные комментарии
        content = content.replaceAll(regexMultiLine, "");  // Многострочные комментарии

        return content;
    }

    // Метод для удаления символов с кодом <33 в начале и конце текста
    private static String trimSpecialCharacters(String content) {
        int start = 0;
        int end = content.length();

        // Удалить символы с кодом <33 с начала
        while (start < end && content.charAt(start) < 33) {
            start++;
        }

        // Удалить символы с кодом <33 с конца
        while (end > start && content.charAt(end - 1) < 33) {
            end--;
        }

        return content.substring(start, end);
    }

    // Метод для удаления пустых строк
    private static String removeEmptyLines(String content) {
        // Разделить по строкам и вернуть только ненулевые строки
        return Arrays.stream(content.split("\n"))
                .filter(line -> !line.trim().isEmpty())
                .collect(Collectors.joining("\n"));
    }

    // Класс для хранения информации о файле
    static class FileInfo {
        private final int size;
        private final String path;

        public FileInfo(int size, String path) {
            this.size = size;
            this.path = path;
        }

        public int getSize() {
            return size;
        }

        public String getPath() {
            return path;
        }
    }
}

