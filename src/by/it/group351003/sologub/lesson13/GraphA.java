package by.it.group351003.sologub.lesson13;
// топологическая сортировка
/*Алгоритм Кана:
Вычислить степень входа для каждой вершины.
Все вершины с 0-вой степенью входа в очередь.
Извлечь вершины из очереди, добавляем в результат и уменьшаем степень входа для соседей.
Если степень входа соседа становится равной 0,-> в очередь.
пока не обработаем все вершины.*/
import java.util.*;
import java.util.stream.Collectors;

public class GraphA {
    public static void main(String[] args) {
        // Чтение входной строки
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();

        // Парсинг строки в структуру орграфа
        Map<String, List<String>> graph = new HashMap<>();
        Set<String> allVertices = new HashSet<>();

        // Разбиение входной строки на элементы
        String[] edges = input.split(", ");
        for (String edge : edges) {
            String[] parts = edge.split(" -> ");
            String from = parts[0];
            String to = parts[1];

            // Строим граф
            graph.computeIfAbsent(from, k -> new ArrayList<>()).add(to);
            allVertices.add(from);
            allVertices.add(to);
        }

        // Структуры для топологической сортировки
        Map<String, Integer> inDegree = new HashMap<>();
        for (String vertex : allVertices) {
            inDegree.put(vertex, 0);
        }

        // Заполнение информации о входных степенях
        for (Map.Entry<String, List<String>> entry : graph.entrySet()) {
            for (String neighbor : entry.getValue()) {
                inDegree.put(neighbor, inDegree.getOrDefault(neighbor, 0) + 1);
            }
        }

        // Используем очередь с приоритетом для лексикографического порядка
        PriorityQueue<String> queue = new PriorityQueue<>();

        // Добавляем вершины с нулевым входным степенем в очередь
        for (Map.Entry<String, Integer> entry : inDegree.entrySet()) {
            if (entry.getValue() == 0) {
                queue.offer(entry.getKey());
            }
        }

        List<String> result = new ArrayList<>();

        // Алгоритм Кана
        while (!queue.isEmpty()) {
            String vertex = queue.poll();
            result.add(vertex);

            if (graph.containsKey(vertex)) {
                for (String neighbor : graph.get(vertex)) {
                    inDegree.put(neighbor, inDegree.get(neighbor) - 1);
                    if (inDegree.get(neighbor) == 0) {
                        queue.offer(neighbor);
                    }
                }
            }
        }

        // Вывод результата
        if (result.size() == allVertices.size()) {
            System.out.println(result.stream().collect(Collectors.joining(" ")));
        } else {
            System.out.println("Ошибка: граф содержит цикл.");
        }
    }
}

