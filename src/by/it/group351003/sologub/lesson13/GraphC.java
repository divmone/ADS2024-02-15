package by.it.group351003.sologub.lesson13;
import java.util.*;

// топологическая сортировка
/*Алгоритм Кана:
Вычислить степень входа для каждой вершины.
Добавьте все вершины с нулевой степенью входа в очередь.
Извлекайте вершины из очереди, добавляйте их в результат и уменьшайте степень входа для соседей.
Если степень входа соседа становится равной нулю, добавьте его в очередь.
Повторяйте, пока не обработаете все вершины.*/
public class GraphC {
    private Map<String, List<String>> graph = new HashMap<>();
    private Map<String, List<String>> transposeGraph = new HashMap<>();
    private Set<String> visited = new HashSet<>();
    private Stack<String> stack = new Stack<>();

    public static void main(String[] args) {
        GraphC graphC = new GraphC();
        graphC.readGraph();
        graphC.findSCC();
    }

    private void readGraph() {
        Scanner scanner = new Scanner(System.in);

        // Считываем строку орграфа
        String input = scanner.nextLine();
        String[] edges = input.split(", ");

        // Строим граф и транспонированный граф
        for (String edge : edges) {
            String[] vertices = edge.split("->");
            String from = vertices[0];
            String to = vertices[1];

            graph.putIfAbsent(from, new ArrayList<>());
            graph.get(from).add(to);

            transposeGraph.putIfAbsent(to, new ArrayList<>());
            transposeGraph.get(to).add(from);
        }
    }

    private void findSCC() {
        // 1. Заполняем стек в порядке завершения обхода
        for (String vertex : graph.keySet()) {
            if (!visited.contains(vertex)) {
                fillOrder(vertex);
            }
        }

        // 2. Транспонируем граф и обрабатываем вершины в порядке стека
        visited.clear();
        List<List<String>> stronglyConnectedComponents = new ArrayList<>();

        while (!stack.isEmpty()) {
            String vertex = stack.pop();
            if (!visited.contains(vertex)) {
                List<String> component = new ArrayList<>();
                dfsTranspose(vertex, component);
                Collections.sort(component); // Сортируем компонент лексикографически
                stronglyConnectedComponents.add(component);
            }
        }

        // 3. Выводим компоненты
        for (List<String> component : stronglyConnectedComponents) {
            System.out.println(String.join("", component));
        }
    }

    private void fillOrder(String vertex) {
        visited.add(vertex);
        for (String neighbor : graph.getOrDefault(vertex, new ArrayList<>())) {
            if (!visited.contains(neighbor)) {
                fillOrder(neighbor);
            }
        }
        stack.push(vertex);
    }

    private void dfsTranspose(String vertex, List<String> component) {
        visited.add(vertex);
        component.add(vertex);
        for (String neighbor : transposeGraph.getOrDefault(vertex, new ArrayList<>())) {
            if (!visited.contains(neighbor)) {
                dfsTranspose(neighbor, component);
            }
        }
    }
}

