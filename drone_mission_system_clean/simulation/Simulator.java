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

    private double batteryDrainPerStep;

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
        batteryDrainPerStep = 3.0 + 0.1 * ((gridRows * gridCols) / 100.0);
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

        GridMap grid = new GridMap(gridRows, gridCols);
        grid.clear();

        // 🔁 Prepare all drones
        for (Drone drone : missionGroups.keySet()) {
            drone.setBatteryLevel(80);
            drone.setCurrentLocation(new Location(0, 0));
        }

        Set<Location> globalPathTrace = new HashSet<>();
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
                double totalNeeded = (toTarget + toCharger) * batteryDrainPerStep;

                if (drone.getBatteryLevel() < totalNeeded) {
                    System.out.println("\n🔋 Battery low for next mission. Redirecting to charger first...");
                    goToLocation(drone, chargerLocation, step, "Recharging at station...", true);
                    drone.recharge();

                    System.out.println("🔋 Battery now full. Resuming mission.");
                    step++;

                    grid.setCharging(true);
                    grid.setDroneLocation(drone.getCurrentLocation());
                    grid.setDrones(new ArrayList<>(missionGroups.keySet()));
                    grid.printMap(null, null, new ArrayList<>(globalPathTrace)); // 🧠 Use traced paths
                    System.out.println("✅ Drone is now charging at the station.");
                }

                System.out.println("\n⏱️ Step " + step + ":");
                List<Location> path = PathFinder.findPath(grid, drone.getCurrentLocation(), end);
                globalPathTrace.addAll(path); // ✅ Accumulate paths

                drone.setCurrentLocation(end);
                drone.decreaseBattery(path.size() * batteryDrainPerStep);
                current.startMission();
                MissionLogger.logMission(current, true);

                grid.setDrones(new ArrayList<>(missionGroups.keySet()));
                grid.printMap(null, null, new ArrayList<>(globalPathTrace));
                System.out.println("📍 Path length: " + path.size());

                step++;
            }
        }

        // ✅ Final Grid
        System.out.println("\n📡 Final Grid with All Drones + Paths:");
        grid.setDrones(new ArrayList<>(missionGroups.keySet()));
        grid.printMap(null, null, new ArrayList<>(globalPathTrace));

        System.out.println("\n✅ Simulation complete. All missions executed.");
        rechargeAllDrones(missionGroups.keySet());
    }

    private void goToLocation(Drone drone, Location destination, int step, String msg, boolean isCharging) {
        GridMap grid = new GridMap(gridRows, gridCols);
        grid.clear();
        Location start = drone.getCurrentLocation();
        List<Location> path = PathFinder.findPath(grid, start, destination);

        System.out.println("\n⏱️ Step " + step + ":");
        grid.setDroneLocation(drone.getCurrentLocation());
        grid.setDrones(Collections.singletonList(drone));
        grid.setCharging(isCharging);
        grid.printMap(null, destination, path);
        System.out.println("📍 Path length: " + path.size());
        System.out.println(msg);

        drone.setCurrentLocation(destination);
        drone.decreaseBattery(path.size() * batteryDrainPerStep);
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
        return Math.max(Math.abs(a.row - b.row), Math.abs(a.col - b.col));
    }
}
