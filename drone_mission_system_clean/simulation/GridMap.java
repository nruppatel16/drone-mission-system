package simulation;

import java.util.List;
import utils.Location;

public class GridMap {
    private final int rows;
    private final int cols;
    private final int[][] grid;
    private String[][] visualMap;

    public static final int EMPTY = 0;
    public static final int OBSTACLE = 1;
    public static final int CHARGER = 2;

    public GridMap(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.grid = new int[rows][cols];
        this.visualMap = new String[rows][cols]; // ✅ Added initialization
        initializeGrid();
        placeDefaults();
    }

    private void initializeGrid() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j] = EMPTY;
                visualMap[i][j] = ".";
            }
        }
    }

    private void placeDefaults() {
        placeRechargeStation(1, 5);
        placeObstacle(2, 7);
        placeObstacle(3, 4);
        placeObstacle(5, 6);
    }

    public void placeObstacle(int row, int col) {
        if (isValidCell(row, col)) {
            grid[row][col] = OBSTACLE;
        }
    }

    public void placeRechargeStation(int row, int col) {
        if (isValidCell(row, col)) {
            grid[row][col] = CHARGER;
        }
    }

    public boolean isWalkable(int row, int col) {
        return isValidCell(row, col) && grid[row][col] != OBSTACLE;
    }

    private boolean isValidCell(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }

    public void printMap(Location drone, Location target, List<Location> path) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Location current = new Location(i, j);
                if (drone != null && drone.row == i && drone.col == j) {
                    if (grid[i][j] == CHARGER) {
                        System.out.print("C-D ");
                    } else {
                        System.out.print("D ");
                    }
                } else if (target != null && target.row == i && target.col == j) {
                    System.out.print("T ");
                } else if (grid[i][j] == OBSTACLE) {
                    System.out.print("X ");
                } else if (grid[i][j] == CHARGER) {
                    System.out.print("C ");
                } else if (path != null && path.contains(current)) {
                    System.out.print("• ");
                } else {
                    System.out.print(". ");
                }
            }
            System.out.println();
        }
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public void clear() {
        initializeGrid();
        placeDefaults();
    }

    public void placeDrone(Location loc) {
        if (isValidCell(loc.row, loc.col)) {
            grid[loc.row][loc.col] = EMPTY;
        }
    }

    public void placeTarget(Location loc) {
        // No real placement — visual only
    }

    public void printWithMovement(Location start, Location end, List<Location> path) {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Location current = new Location(row, col);
                if (start.row == row && start.col == col) {
                    if (grid[row][col] == CHARGER) {
                        System.out.print("C-D ");
                    } else {
                        System.out.print("D ");
                    }
                } else if (end.row == row && end.col == col) {
                    System.out.print("T ");
                } else if (path != null && path.contains(current)) {
                    System.out.print("* ");
                } else if (grid[row][col] == OBSTACLE) {
                    System.out.print("X ");
                } else if (grid[row][col] == CHARGER) {
                    System.out.print("C ");
                } else {
                    System.out.print(". ");
                }
            }
            System.out.println();
        }
    }

    public void setCell(int row, int col, String val) {
        if (isValidCell(row, col)) {
            visualMap[row][col] = val;
        }
    }
}
