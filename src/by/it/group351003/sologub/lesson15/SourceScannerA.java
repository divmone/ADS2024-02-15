package by.it.group351003.sologub.lesson15;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.nio.charset.MalformedInputException;
/*В каждом тексте файла необходимо:
1. Удалить строку package и все импорты за O(n).
2. Удалить все символы с кодом <33 в начале и конце текстов.

В полученном наборе текстов:
1. Рассчитать размер в байтах для полученных текстов
   и вывести в консоль
   размер и относительный от src путь к каждому из файлов (по одному в строке)
2. При выводе сортировать файлы по размеру,
   а если размер одинаковый,
   то лексикографически сортировать пути
*/

public class SourceScannerA {
    public static void main(String[] args) {
        String src = System.getProperty("user.dir") + File.separator + "src" + File.separator;

        // Создать список для хранения информации о файлах
        List<FileInfo> fileInfos = new ArrayList<>();

        try {
            // Рекурсивно найти все файлы с расширением .java
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

                            // Удалить символы с кодом <33 в начале и конце текста
                            modifiedContent = trimSpecialCharacters(modifiedContent);

                            // Рассчитать размер текста в байтах
                            byte[] contentBytes = modifiedContent.getBytes(StandardCharsets.UTF_8);
                            int size = contentBytes.length;

                            // Относительный путь к файлу
                            Path srcPath = Paths.get(src).toAbsolutePath();
                            String relativePath = srcPath.relativize(path.toAbsolutePath()).toString();

                            // Добавить информацию о файле
                            fileInfos.add(new FileInfo(size, relativePath));

                        } catch (MalformedInputException e) {
                            System.err.println("Ошибка чтения файла (пропускаем): " + path);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Сортировать файлы: сначала по размеру, затем лексикографически по пути
        fileInfos.sort(Comparator
                .comparingInt(FileInfo::getSize)
                .thenComparing(FileInfo::getPath));

        // Вывести размер и путь для каждого файла
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

    // Метод для удаления символов с кодом <33 в начале и конце текста
    private static String trimSpecialCharacters(String content) {
        int start = 0;
        int end = content.length();

        // Удалить символы <33 с начала
        while (start < end && content.charAt(start) < 33) {
            start++;
        }

        // Удалить символы <33 с конца
        while (end > start && content.charAt(end - 1) < 33) {
            end--;
        }

        return content.substring(start, end);
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

