package simulation;

import drones.Drone;
import mission.Mission;
import mission.MissionLogger;
import mission.MissionQueue;
import mission.MissionPlanner;
import utils.Location;

import java.util.*;

public class Simulator {
    private MissionQueue missionQueue;
    private Scanner scanner;
    private final Location chargerLocation = new Location(1, 5);
    private static int gridRows = 10;
    private static int gridCols = 10;

    public static void setGridSize(int rows, int cols) {
        gridRows = rows;
        gridCols = cols;
    }

    public static int getGridRows() {
        return gridRows;
    }

    public static int getGridCols() {
        return gridCols;
    }

    public Simulator(MissionQueue queue, MissionPlanner planner) {
        this.missionQueue = queue;
        this.scanner = new Scanner(System.in);
    }

    public void runSimulation() {
        System.out.println("\n🧠 Starting simulation...");

        Map<Drone, List<Mission>> missionGroups = new HashMap<>();
        for (Mission mission : missionQueue.getAllMissions()) {
            Drone drone = mission.getAssignedDrone();
            missionGroups.computeIfAbsent(drone, k -> new ArrayList<>()).add(mission);
        }

        System.out.println("📦 Missions found: " + missionGroups.size());

        boolean offerFuelMode = missionGroups.values().stream().anyMatch(list -> list.size() > 1);
        int mode = 1;

        if (offerFuelMode) {
            System.out.println("\nYou have assigned multiple missions to at least one drone.");
            System.out.println("Choose mission execution mode:");
            System.out.println("[1] Direct Orders (in order assigned)");
            System.out.println("[2] Fuel Efficiency Mode (optimize path)");
            System.out.print("Your choice: ");
            mode = scanner.nextInt();
            scanner.nextLine();
        }

        int step = 1;
        for (Map.Entry<Drone, List<Mission>> entry : missionGroups.entrySet()) {
            Drone drone = entry.getKey();
            List<Mission> missions = entry.getValue();

            if (mode == 2 && missions.size() > 1) {
                missions = sortByFuelEfficiency(missions, drone.getCurrentLocation());
            }

            for (Mission current : missions) {
                Location start = drone.getCurrentLocation();
                Location end = current.getTargetLocation();

                int toTarget = getDistance(start, end);
                int toCharger = getDistance(end, chargerLocation);
                int totalNeeded = (toTarget + toCharger) * 5;

                if (drone.getBatteryLevel() < totalNeeded) {
                    System.out.println("\n🔋 Battery low for next mission. Redirecting to charger first...");
                    goToLocation(drone, chargerLocation, step, "Recharging at station...", true);
                    drone.recharge();

                    System.out.println("🔋 Battery now full. Resuming mission.");
                    step++;

                    // 🪫 Display 'C-D' at charger while charging
                    GridMap grid = new GridMap(gridRows, gridCols);
                    grid.clear();
                    System.out.println("\n🔌 Charging...");
                    grid.printMap(chargerLocation, null, null);
                    System.out.println("✅ Drone is now charging at the station.");
                }

                System.out.println("\n⏱️ Step " + step + ":");
                GridMap grid = new GridMap(gridRows, gridCols);
                grid.clear();
                grid.placeDrone(drone.getCurrentLocation());
                grid.placeTarget(end);
                List<Location> path = PathFinder.findPath(grid, drone.getCurrentLocation(), end);
                grid.printMap(drone.getCurrentLocation(), end, path);
                System.out.println("📍 Path length: " + path.size());
                grid.printWithMovement(drone.getCurrentLocation(), end, path);

                current.startMission();
                drone.decreaseBattery(path.size());
                drone.setCurrentLocation(end);
                MissionLogger.logMission(current, true);
                step++;
            }
        }

        System.out.println("\n✅ Simulation complete. All missions executed.");
        rechargeAllDrones(missionGroups.keySet());
    }

    private void goToLocation(Drone drone, Location destination, int step, String msg, boolean isCharging) {
        GridMap grid = new GridMap(gridRows, gridCols);
        grid.clear();
        Location start = drone.getCurrentLocation();
        List<Location> path = PathFinder.findPath(grid, start, destination);

        System.out.println("\n⏱️ Step " + step + ":");
        grid.printMap(start, destination, path);
        System.out.println("📍 Path length: " + path.size());
        grid.printWithMovement(start, destination, path);
        System.out.println(msg);

        drone.setCurrentLocation(destination);
        drone.decreaseBattery(path.size());
    }

    private List<Mission> sortByFuelEfficiency(List<Mission> missions, Location start) {
        List<Mission> sorted = new ArrayList<>();
        Set<Mission> remaining = new HashSet<>(missions);
        Location current = start;

        while (!remaining.isEmpty()) {
            Mission nearest = null;
            int minDist = Integer.MAX_VALUE;

            for (Mission m : remaining) {
                Location tgt = m.getTargetLocation();
                int dist = Math.abs(current.row - tgt.row) + Math.abs(current.col - tgt.col);
                if (dist < minDist) {
                    minDist = dist;
                    nearest = m;
                }
            }

            sorted.add(nearest);
            current = nearest.getTargetLocation();
            remaining.remove(nearest);
        }

        return sorted;
    }

    private void rechargeAllDrones(Set<Drone> drones) {
        System.out.println("\n🔁 Recharging all drones...");
        for (Drone d : drones) {
            d.recharge();
            System.out.println("🔋 " + d.getId() + " recharged to 100%");
        }
    }

    private int getDistance(Location a, Location b) {
        return Math.abs(a.row - b.row) + Math.abs(a.col - b.col);
    }
}
