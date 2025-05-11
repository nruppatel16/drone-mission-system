package simulation;

import utils.Location;
import java.util.*;

public class PathFinder {

    // ✅ Global cache for repeated paths
    private static final Map<String, List<Location>> pathCache = new HashMap<>();

    public static List<Location> findPath(GridMap grid, Location start, Location end) {
        String key = start.row + "," + start.col + "->" + end.row + "," + end.col;

        if (pathCache.containsKey(key)) {
            return pathCache.get(key);
        }

        List<Location> path = findPathAStar(grid, start, end);
        pathCache.put(key, path);
        return path;
    }

    private static List<Location> findPathAStar(GridMap grid, Location start, Location end) {
        int rows = grid.getRows();
        int cols = grid.getCols();
        boolean[][] visited = new boolean[rows][cols];
        Map<Location, Location> cameFrom = new HashMap<>();
        Map<Location, Integer> gScore = new HashMap<>();

        PriorityQueue<Location> openSet = new PriorityQueue<>(
                Comparator.comparingInt(loc -> gScore.get(loc) + getChebyshevDistance(loc, end)));

        gScore.put(start, 0);
        openSet.offer(start);

        int[][] directions = {
            {0, 1}, {1, 0}, {0, -1}, {-1, 0},     // ↑ ↓ ← →
            {-1, -1}, {-1, 1}, {1, -1}, {1, 1}    // Diagonals ↖ ↗ ↙ ↘
        };

        while (!openSet.isEmpty()) {
            Location current = openSet.poll();

            if (current.equals(end)) {
                return reconstructPath(cameFrom, start, end);
            }

            if (visited[current.row][current.col]) continue;
            visited[current.row][current.col] = true;

            for (int[] dir : directions) {
                int newRow = current.row + dir[0];
                int newCol = current.col + dir[1];

                // Check bounds and obstacles
                if (!grid.isWalkable(newRow, newCol)) continue;

                // Prevent "corner cutting" for diagonal movement
                if (Math.abs(dir[0]) == 1 && Math.abs(dir[1]) == 1) {
                    int sideRow = current.row + dir[0];
                    int sideCol = current.col;
                    int otherSideRow = current.row;
                    int otherSideCol = current.col + dir[1];

                    if (!grid.isWalkable(sideRow, sideCol) || !grid.isWalkable(otherSideRow, otherSideCol)) {
                        continue; // can't go diagonally around a corner
                    }
                }

                Location neighbor = new Location(newRow, newCol);
                int tentativeG = gScore.get(current) + 1;

                if (!gScore.containsKey(neighbor) || tentativeG < gScore.get(neighbor)) {
                    gScore.put(neighbor, tentativeG);
                    cameFrom.put(neighbor, current);
                    openSet.offer(neighbor);
                }
            }
        }

        return new ArrayList<>(); // No path found
    }

    private static int getChebyshevDistance(Location a, Location b) {
        return Math.max(Math.abs(a.row - b.row), Math.abs(a.col - b.col));
    }

    private static List<Location> reconstructPath(Map<Location, Location> cameFrom, Location start, Location end) {
        List<Location> path = new ArrayList<>();
        Location current = end;
        while (!current.equals(start)) {
            path.add(current);
            current = cameFrom.get(current);
        }
        path.add(start);
        Collections.reverse(path);
        return path;
    }
}
