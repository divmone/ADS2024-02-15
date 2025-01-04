package by.it.group351003.sologub.lesson15;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;
import java.util.stream.Collectors;

public class SourceScannerC {
    public static void main(String[] args) {
        String src = System.getProperty("user.dir") + File.separator + "src" + File.separator;

        // Список для хранения информации о файлах
        List<FileInfo> fileInfos = new ArrayList<>();
        // Карта для хранения текстов и их путей
        Map<String, List<String>> textToPaths = new HashMap<>();

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

                            // Заменить все символы с кодом <33 на пробел
                            modifiedContent = replaceLowCodeChars(modifiedContent);

                            // Выполнить trim()
                            modifiedContent = modifiedContent.trim();

                            // Добавить текст в карту
                            textToPaths.computeIfAbsent(modifiedContent, k -> new ArrayList<>()).add(path.toString());

                        } catch (IOException e) {
                            System.err.println("Ошибка чтения файла (пропускаем): " + path);
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Перебираем все пары файлов и вычисляем расстояние Левенштейна
        Set<Set<String>> processed = new HashSet<>();

        for (Map.Entry<String, List<String>> entry : textToPaths.entrySet()) {
            List<String> paths = entry.getValue();
            if (paths.size() > 1) {
                // Сортировать пути файлов лексикографически
                paths.sort(String::compareTo);

                // Для всех файлов, содержащих одинаковый текст, ищем копии
                for (int i = 0; i < paths.size(); i++) {
                    for (int j = i + 1; j < paths.size(); j++) {
                        Set<String> pair = new HashSet<>();
                        pair.add(paths.get(i));
                        pair.add(paths.get(j));

                        // Если пара еще не была обработана, вычисляем расстояние Левенштейна
                        if (!processed.contains(pair)) {
                            int distance = levenshteinDistance(entry.getKey(), entry.getKey());
                            if (distance < 10) {
                                // Если тексты похожи, выводим их пути
                                System.out.println(paths.get(i));
                                System.out.println(paths.get(j));
                                processed.add(pair);
                            }
                        }
                    }
                }
            }
        }
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

    // Метод для замены всех символов с кодом <33 на пробел
    private static String replaceLowCodeChars(String content) {
        StringBuilder result = new StringBuilder();

        for (char c : content.toCharArray()) {
            if (c < 33) {
                result.append(' '); // заменяем на пробел
            } else {
                result.append(c);
            }
        }

        return result.toString();
    }

    // Метод для вычисления расстояния Левенштейна
    private static int levenshteinDistance(String s1, String s2) {
            if (s1.equals(s2)) return 0;
            int len1 = s1.length();
            int len2 = s2.length();

            if (len1 == 0) return len2;
            if (len2 == 0) return len1;

            int[] prev = new int[len2 + 1];
            int[] curr = new int[len2 + 1];

            // Инициализация первой строки
            for (int i = 0; i <= len2; i++) {
                prev[i] = i;
            }

            for (int i = 1; i <= len1; i++) {
                curr[0] = i;

                for (int j = 1; j <= len2; j++) {
                    int cost = s1.charAt(i - 1) == s2.charAt(j - 1) ? 0 : 1;
                    curr[j] = Math.min(Math.min(curr[j - 1] + 1, prev[j] + 1), prev[j - 1] + cost);
                }

                // Обмен текущих и предыдущих строк
                int[] temp = prev;
                prev = curr;
                curr = temp;
            }

            return prev[len2];
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

