package simulation;

import drones.Drone;
import drones.DroneType;
import utils.Location;

import java.util.*;

public class GridMap {
    private final int rows;
    private final int cols;
    private final int[][] grid;
    private final Location base = new Location(0, 0);
    private final Location charger = new Location(1, 5);

    public static final int EMPTY = 0;
    public static final int OBSTACLE = 1;
    public static final int CHARGER = 2;
    public static final int ENEMY_BORDER = 3;

    private boolean isCharging = false;
    private Location currentDroneLocation = null;

    private Map<Location, Drone> droneSymbols = new HashMap<>();
    private Map<Location, Character> pathSymbols = new HashMap<>();
    private Map<Location, String> pathColors = new HashMap<>();

    public GridMap(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.grid = new int[rows][cols];
        initializeGrid();
        placeDefaults();
        placeEnemyTerritoryBorder();
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

    private void placeEnemyTerritoryBorder() {
        int rStart = rows / 10;
        int rEnd = rows / 2;
        int cStart = (int) (cols * 0.65);
        int cEnd = cols - 1;

        for (int c = cStart; c <= cEnd; c++) {
            grid[rStart][c] = ENEMY_BORDER;
        }
        for (int r = rStart + 1; r <= rEnd; r++) {
            grid[r][cStart] = ENEMY_BORDER;
        }
        for (int c = cStart + 1; c <= cEnd; c++) {
            grid[rEnd][c] = ENEMY_BORDER;
        }
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
        return isValidCell(row, col) && grid[row][col] != OBSTACLE && grid[row][col] != ENEMY_BORDER;
    }

    private boolean isValidCell(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }

    public void setCharging(boolean charging) {
        this.isCharging = charging;
    }

    public void setDroneLocation(Location droneLoc) {
        this.currentDroneLocation = droneLoc;
    }

    public void setDrones(List<Drone> drones) {
        droneSymbols.clear();
        for (Drone d : drones) {
            Location loc = d.getCurrentLocation();
            droneSymbols.put(loc, d);
        }
    }

    public void setPathSymbol(Location loc, char symbol, DroneType type) {
        if (!droneSymbols.containsKey(loc)) {
            pathSymbols.put(loc, symbol);
            pathColors.put(loc, getColorForType(type));
        }
    }

    private String getColorForType(DroneType type) {
        switch (type) {
            case RESCUE:
                return "\u001B[34m"; // Blue
            case COMBAT:
                return "\u001B[31m"; // Red
            case SURVEILLANCE:
                return "\u001B[32m"; // Green
            default:
                return "\u001B[0m"; // Default
        }
    }

    private String getColoredSymbol(Drone drone) {
        String color;
        char symbol;
        switch (drone.getType()) {
            case RESCUE:
                color = "\u001B[34m";
                symbol = 'R';
                break;
            case COMBAT:
                color = "\u001B[31m";
                symbol = 'C';
                break;
            case SURVEILLANCE:
                color = "\u001B[32m";
                symbol = 'S';
                break;
            default:
                color = "\u001B[0m";
                symbol = 'D';
        }

        if (drone.getCurrentLocation().equals(charger)) {
            return color + "C-" + symbol + "\u001B[0m";
        }

        return color + symbol + "\u001B[0m";
    }

    public void printMap(Location unused, Location target, List<Location> path) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Location current = new Location(i, j);

                if (i == base.row && j == base.col) {
                    System.out.print("B ");
                } else if (droneSymbols.containsKey(current)) {
                    System.out.print(getColoredSymbol(droneSymbols.get(current)) + " ");
                } else if (target != null && target.row == i && target.col == j) {
                    System.out.print("T ");
                } else if (grid[i][j] == OBSTACLE) {
                    System.out.print("X ");
                } else if (grid[i][j] == CHARGER) {
                    System.out.print("C ");
                } else if (grid[i][j] == ENEMY_BORDER) {
                    System.out.print("\u001B[31m#\u001B[0m ");
                } else if (pathSymbols.containsKey(current)) {
                    String color = pathColors.getOrDefault(current, "\u001B[0m");
                    System.out.print(color + pathSymbols.get(current) + "\u001B[0m ");
                } else {
                    System.out.print(". ");
                }
            }
            System.out.println();
        }

        System.out.println("\nLegend:");
        System.out.println("\u001B[34mR\u001B[0m = Rescue   \u001B[31mC\u001B[0m = Combat   \u001B[32mS\u001B[0m = Surveillance   B = Base   C = Charger   X = Obstacle   \u001B[31m#\u001B[0m = Enemy Border");
    }

    public void printWithMovement(Location start, Location end, List<Location> path) {
        printMap(start, end, path);
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
        pathSymbols.clear();
        pathColors.clear();
        placeEnemyTerritoryBorder();
    }

    public void placeDrone(Location loc) {}
    public void placeTarget(Location loc) {}
}
