package simulation;

import java.util.List;
import java.util.Random;
import utils.Location;

public class GridMap {
    private final int rows;
    private final int cols;
    private final int[][] grid;
    private final Location base = new Location(0, 0);
    private final Location charger = new Location(1, 5);

    public static final int EMPTY = 0;
    public static final int OBSTACLE = 1;
    public static final int CHARGER = 2;

    public GridMap(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.grid = new int[rows][cols];
        initializeGrid();
        placeDefaults();
    }

    private void initializeGrid() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j] = EMPTY;
            }
        }
    }

    private void placeDefaults() {
        placeRechargeStation(charger.row, charger.col);
        placeObstaclesDynamically();
    }

    public void placeRechargeStation(int row, int col) {
        if (isValidCell(row, col)) {
            grid[row][col] = CHARGER;
        }
    }

    public void placeObstacle(int row, int col) {
        if (isValidCell(row, col) && !isSpecialCell(row, col)) {
            grid[row][col] = OBSTACLE;
        }
    }

    private boolean isSpecialCell(int row, int col) {
        return (row == base.row && col == base.col) || (row == charger.row && col == charger.col);
    }

    private void placeObstaclesDynamically() {
        int totalCells = rows * cols;
        int obstacleCount = (rows / 10) * 4;

        Random rand = new Random();
        int placed = 0;

        while (placed < obstacleCount) {
            int r = rand.nextInt(rows);
            int c = rand.nextInt(cols);
            if (grid[r][c] == EMPTY && !isSpecialCell(r, c)) {
                grid[r][c] = OBSTACLE;
                placed++;
            }
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

                if (i == base.row && j == base.col) {
                    System.out.print("B ");
                } else if (drone != null && drone.row == i && drone.col == j) {
                    if (grid[i][j] == CHARGER) {
                        System.out.print("D ");
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

    public void printWithMovement(Location start, Location end, List<Location> path) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Location current = new Location(i, j);

                if (i == base.row && j == base.col) {
                    System.out.print("B ");
                } else if (start.row == i && start.col == j) {
                    if (grid[i][j] == CHARGER) {
                        System.out.print("D ");
                    } else {
                        System.out.print("D ");
                    }
                } else if (end.row == i && end.col == j) {
                    System.out.print("T ");
                } else if (path != null && path.contains(current)) {
                    System.out.print("* ");
                } else if (grid[i][j] == OBSTACLE) {
                    System.out.print("X ");
                } else if (grid[i][j] == CHARGER) {
                    System.out.print("C ");
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
        // No-op: kept for compatibility with Simulator
    }

    public void placeTarget(Location loc) {
        // No-op: kept for compatibility with Simulator
    }

}
