package mission;

import drones.*;
import simulation.GridMap;
import simulation.PathFinder;
import utils.Location;

import java.util.*;

public class MissionPlanner {

    private List<Drone> dronePool;
    private Set<String> assignedDrones;
    private Map<String, Integer> droneMissionCount;

    public MissionPlanner(List<Drone> dronePool) {
        this.dronePool = dronePool;
        this.assignedDrones = new HashSet<>();
        this.droneMissionCount = new HashMap<>();
    }

    public Mission assignMissionAutomatically(String id, MissionType type, Location target) {
        Drone bestDrone = null;
        int minDistance = Integer.MAX_VALUE;

        for (Drone drone : dronePool) {
            String droneId = drone.getId();
            boolean alreadyAssigned = assignedDrones.contains(droneId);

            boolean typeMatches = (type == MissionType.COMBAT && drone instanceof CombatDrone) ||
                                  (type == MissionType.SURVEILLANCE && drone instanceof SurveillanceDrone) ||
                                  (type == MissionType.RESCUE && drone instanceof RescueDrone);

            int missionCount = droneMissionCount.getOrDefault(droneId, 0);

            if (alreadyAssigned && (!typeMatches || missionCount >= 3)) {
                continue;
            }

            if (drone.getBatteryLevel() < 20) {
                continue; // ❌ skip drones with low battery
            }

            int dist = Math.abs(drone.getCurrentLocation().row - target.row)
                     + Math.abs(drone.getCurrentLocation().col - target.col);

            if (dist < minDistance) {
                bestDrone = drone;
                minDistance = dist;
            }
        }

        // Auto-deploy if none found
        if (bestDrone == null) {
            String droneId = "AUTO-" + System.currentTimeMillis();
            bestDrone = new SurveillanceDrone(droneId, "AutoGen", new Location(1, 7)); // Spawn at charging station
            dronePool.add(bestDrone);
            System.out.println("⚠️ No suitable drone found — auto-deployed: " + droneId);
        }

        assignedDrones.add(bestDrone.getId());

        // ⏱️ ETA + Path Calculation
        GridMap grid = new GridMap(10, 10);
        Location start = bestDrone.getCurrentLocation();
        List<Location> path = PathFinder.findPath(grid, start, target);
        int eta = (path != null) ? path.size() : -1;

        System.out.println("✅ Mission " + id + " assigned to Drone " + bestDrone.getId());
        System.out.println("📍 Path Length: " + ((eta != -1) ? eta : "N/A"));
        System.out.println("⏱️ ETA: " + ((eta != -1) ? eta + " units" : "Path not found"));

        if (droneMissionCount.containsKey(bestDrone.getId())) {
            System.out.println("🔁 Reusing drone " + bestDrone.getId() + " (same mission type)");
        }

        // 🔋 Drain battery
        bestDrone.drainBattery(eta);
        System.out.println("🔋 Battery after assignment: " + bestDrone.getBatteryLevel() + "%");

        droneMissionCount.put(bestDrone.getId(),
                droneMissionCount.getOrDefault(bestDrone.getId(), 0) + 1);

        Mission mission = new Mission(id, type, bestDrone, target);
        MissionLogger.logMission(mission, bestDrone.getId().startsWith("AUTO"));

        return mission;
    }

    public void resetAssignments() {
        assignedDrones.clear();
        droneMissionCount.clear();
    }

    // 🔁 Let Simulator access all drones
    public List<Drone> getDronePool() {
        return dronePool;
    }
}
