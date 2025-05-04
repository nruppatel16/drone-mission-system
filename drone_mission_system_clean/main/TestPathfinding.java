package main;

import simulation.GridMap;
import simulation.PathFinder;
import utils.Location;

import java.util.List;

public class TestPathfinding {
    public static void main(String[] args) throws InterruptedException {
        GridMap grid = new GridMap(5, 7);

        // Add obstacles
        grid.placeObstacle(0, 3);
        grid.placeObstacle(1, 3);
        grid.placeObstacle(2, 3);

        Location start = new Location(0, 0);
        Location end = new Location(2, 4);

        List<Location> path = PathFinder.findPath(grid, start, end); // This now uses A*

        if (path.isEmpty()) {
            System.out.println("❌ No path found.");
            return;
        }

        System.out.println("✅ Path found! Steps: " + path.size());
        for (Location step : path) {
            Thread.sleep(300);
            System.out.println("Drone at (" + step.row + ", " + step.col + ")");
            grid.printMap(step, end, path);
        }
    }
}
