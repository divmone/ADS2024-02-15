package by.it.group351003.sologub.lesson13;

import java.util.*;
/*Алгоритм поиска циклов в графе:
глубинный поиск(DFS) с отслеживанием состояния. три состояния для вершин:
0 — вершина не посещена.
1 — вершина находится в процессе посещения.
2 — вершина была обработана (соседи посещены).
Цикл обнаруживается, если мы встречаем вершину, которая находится в процессе посещения (состояние 1).
Это означает, что мы снова пришли к вершине, с которой начали обход, образуя цикл.*/
public class GraphB {

    public static void main(String[] args) {
        // Чтение входной строки
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();

        // Создаём граф
        Map<Integer, List<Integer>> graph = new HashMap<>();
        String[] edges = input.split(", ");

        for (String edge : edges) {
            String[] parts = edge.split(" -> ");
            int from = Integer.parseInt(parts[0]);
            int to = Integer.parseInt(parts[1]);

            graph.computeIfAbsent(from, k -> new ArrayList<>()).add(to);
        }

        // Проверка на цикл
        if (hasCycle(graph)) {
            System.out.println("yes");
        } else {
            System.out.println("no");
        }
    }

    // Метод для проверки наличия цикла в графе
    public static boolean hasCycle(Map<Integer, List<Integer>> graph) {
        Map<Integer, Integer> visited = new HashMap<>();

        // Проходим по всем вершинам
        for (Integer vertex : graph.keySet()) {
            if (!visited.containsKey(vertex)) {
                if (dfs(graph, vertex, visited)) {
                    return true;
                }
            }
        }
        return false;
    }

    // Метод поиска в глубину с отслеживанием цикла
    private static boolean dfs(Map<Integer, List<Integer>> graph, int node, Map<Integer, Integer> visited) {
        // Если вершина уже в процессе посещения, значит есть цикл
        if (visited.getOrDefault(node, 0) == 1) {
            return true;
        }

        // Помечаем вершину как посещаемую
        visited.put(node, 1);

        // Рекурсивно посещаем всех соседей
        if (graph.containsKey(node)) {
            for (Integer neighbor : graph.get(node)) {
                if (dfs(graph, neighbor, visited)) {
                    return true;
                }
            }
        }

        // Помечаем вершину как полностью посещённую
        visited.put(node, 2);
        return false;
    }
}
