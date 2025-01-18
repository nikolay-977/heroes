package programs;

import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.Edge;
import com.battle.heroes.army.programs.EdgeDistance;
import com.battle.heroes.army.programs.UnitTargetPathFinder;

import java.util.*;

public class UnitTargetPathFinderImpl implements UnitTargetPathFinder {
    private static final int WIDTH = 27;
    private static final int HEIGHT = 21;
    private static final int[][] DIRECTIONS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

    // Основной цикл обработки узлов в очереди PriorityQueue
    // Алгоритм обрабатывает все клетки N. На каждой итерации основного цикла:
    // Удаление минимального элемента из очереди занимает O(logN)времени.
    // Затем соседние клетки (≤ 4) для текущей клетки проверяются и обновляются (вставляются в очередь).
    // Добавление в очередь также занимает O(logN) для каждой соседней клетки. Таким образом, каждая вершина v
    // может быть добавлена в очередь или обновлена максимум 1 раз, что в сумме требует O(NlogN) для работы очереди.
    // Обход соседей: При максимальных 4 соседях для каждой из N ячеек, проверка соседей требует O(4N) = O(N)
    // Итого по времени:Алгоритм по времени работает за: O(Nlog) где N = WIDTH×HEIGHT
    // Пространственная сложность:
    // Алгоритм использует несколько вспомогательных структур данных:
    // Массивы расстояний, посещений, и предыдущих рёбер:
    // Каждый из них имеет размер O(N).
    // Очередь PriorityQueue: В худшем случае может содержать O(N) элементов (например, если все клетки добавляются в очередь).
    // Множество занятых ячеек (occupiedCells): В худшем случае содержит до O(N) элементов (если все клетки заняты).
    // Хранение результата пути: Лист рёбер, содержащий путь, ограничен длиной O(N) в худшем случае.
    // Итого по пространству: O(N)
    // Итоговая сложность: O(Nlog) где N = WIDTH×HEIGHT
    @Override
    public List<Edge> getTargetPath(Unit attackUnit, Unit targetUnit, List<Unit> existingUnitList) {
        int[][] distance = getDistanceArray();
        boolean[][] visited = new boolean[WIDTH][HEIGHT];
        Edge[][] previous = new Edge[WIDTH][HEIGHT];
        Set<Point> occupiedCells = getOccupiedCells(existingUnitList, attackUnit, targetUnit);

        PriorityQueue<EdgeDistance> queue = new PriorityQueue<>(Comparator.comparingInt(EdgeDistance::getDistance));
        setStartPoint(attackUnit, distance, queue);

        while (!queue.isEmpty()) {
            EdgeDistance current = queue.poll();
            if (visited[current.getX()][current.getY()]) continue;
            visited[current.getX()][current.getY()] = true;

            if (isTargetReached(current, targetUnit)) {
                break;
            }

            exploreNeighbors(current, occupiedCells, distance, previous, queue, visited);
        }

        return getPathToTarget(previous, attackUnit, targetUnit);
    }

    // Массив расстояний заполнен максимальными значениями
    private int[][] getDistanceArray() {
        int[][] distance = new int[WIDTH][HEIGHT];
        for (int[] row : distance) {
            Arrays.fill(row, Integer.MAX_VALUE);
        }
        return distance;
    }

    // Получение занятых клеток в виде уникальных точек
    private Set<Point> getOccupiedCells(List<Unit> existingUnitList, Unit attackUnit, Unit targetUnit) {
        Set<Point> occupiedCells = new HashSet<>();
        for (Unit unit : existingUnitList) {
            if (unit.isAlive() && unit != attackUnit && unit != targetUnit) {
                occupiedCells.add(new Point(unit.getxCoordinate(), unit.getyCoordinate()));
            }
        }
        return occupiedCells;
    }

    // Устанавливаем начальную точку в очередь
    private void setStartPoint(Unit attackUnit, int[][] distance, PriorityQueue<EdgeDistance> queue) {
        int startX = attackUnit.getxCoordinate();
        int startY = attackUnit.getyCoordinate();
        distance[startX][startY] = 0;
        queue.add(new EdgeDistance(startX, startY, 0));
    }

    // Проверка достижения цели
    private boolean isTargetReached(EdgeDistance current, Unit targetUnit) {
        return current.getX() == targetUnit.getxCoordinate() && current.getY() == targetUnit.getyCoordinate();
    }

    // Исследуем соседние клетки
    private void exploreNeighbors(EdgeDistance current, Set<Point> occupiedCells, int[][] distance, Edge[][] previous,
                                  PriorityQueue<EdgeDistance> queue, boolean[][] visited) {
        for (int[] dir : DIRECTIONS) {
            int neighborX = current.getX() + dir[0];
            int neighborY = current.getY() + dir[1];

            if (isValid(neighborX, neighborY, occupiedCells, visited)) {
                int newDistance = distance[current.getX()][current.getY()] + 1;

                if (newDistance < distance[neighborX][neighborY]) {
                    distance[neighborX][neighborY] = newDistance;
                    previous[neighborX][neighborY] = new Edge(current.getX(), current.getY());
                    queue.add(new EdgeDistance(neighborX, neighborY, newDistance));
                }
            }
        }
    }

    // Проверка, является ли клетка допустимой
    private boolean isValid(int x, int y, Set<Point> occupiedCells, boolean[][] visited) {
        return x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT && !occupiedCells.contains(new Point(x, y)) && !visited[x][y];
    }

    // Формируем путь от цели к началу
    private List<Edge> getPathToTarget(Edge[][] previous, Unit attackUnit, Unit targetUnit) {
        List<Edge> path = new ArrayList<>();
        int pathX = targetUnit.getxCoordinate();
        int pathY = targetUnit.getyCoordinate();

        while (pathX != attackUnit.getxCoordinate() || pathY != attackUnit.getyCoordinate()) {
            path.add(new Edge(pathX, pathY));
            Edge prev = previous[pathX][pathY];
            if (prev == null) return Collections.emptyList();
            pathX = prev.getX();
            pathY = prev.getY();
        }
        path.add(new Edge(attackUnit.getxCoordinate(), attackUnit.getyCoordinate()));
        Collections.reverse(path);
        return path;
    }

    // Класс для точки
    private class Point {
        private final int x;
        private final int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Point point = (Point) o;
            return x == point.x && y == point.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }
}