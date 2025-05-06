package mission;

import drones.*;
import simulation.GridMap;
import simulation.PathFinder;
import utils.Location;
import mission.MissionLog;

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

            int dist = Math.abs(drone.getCurrentLocation().row - target.row)
                     + Math.abs(drone.getCurrentLocation().col - target.col);

            if (dist < minDistance) {
                bestDrone = drone;
                minDistance = dist;
            }
        }

        boolean isAutoDeployed = false;

        if (bestDrone == null) {
            String droneId = "AUTO-" + System.currentTimeMillis();
            bestDrone = new SurveillanceDrone(droneId, "AutoGen", new Location(1, 7));
            dronePool.add(bestDrone);
            System.out.println("⚠️ No suitable drone found — auto-deployed: " + droneId);
            isAutoDeployed = true;
        }

        assignedDrones.add(bestDrone.getId());

        GridMap grid = new GridMap(10, 10);
        Location start = bestDrone.getCurrentLocation();
        List<Location> pathToTarget = PathFinder.findPath(grid, start, target);
        Location charger = new Location(1, 7);
        List<Location> pathToCharger = PathFinder.findPath(grid, target, charger);

        int totalSteps = 0;
        if (pathToTarget != null) totalSteps += pathToTarget.size();
        if (pathToCharger != null) totalSteps += pathToCharger.size();

        int batteryNeeded = totalSteps * 5;

        if (bestDrone.getBatteryLevel() < batteryNeeded) {
            System.out.println("⚠️ Not enough battery to complete mission and reach charger. Rerouting to recharge first...");
            List<Location> pathToChargerNow = PathFinder.findPath(grid, start, charger);
            if (pathToChargerNow != null) {
                bestDrone.drainBattery(pathToChargerNow.size() * 5);
                bestDrone.setCurrentLocation(charger);
                System.out.println("🔋 Battery after reaching charger: " + bestDrone.getBatteryLevel() + "%");
                bestDrone.recharge();
                System.out.println("🔌 Recharged to 100% at charger before mission.");
            } else {
                System.out.println("❌ No path to charger. Mission cannot be assigned.");
                return null;
            }
        }

        int eta = (pathToTarget != null) ? pathToTarget.size() : -1;

        System.out.println("✅ Mission " + id + " assigned to Drone " + bestDrone.getId());
        System.out.println("📍 Path Length: " + ((eta != -1) ? eta : "N/A"));
        System.out.println("⏱️ ETA: " + ((eta != -1) ? eta + " units" : "Path not found"));

        if (droneMissionCount.containsKey(bestDrone.getId())) {
            System.out.println("🔁 Reusing drone " + bestDrone.getId() + " (same mission type)");
        }

        bestDrone.drainBattery(eta * 5);
        System.out.println("🔋 Battery after assignment: " + bestDrone.getBatteryLevel() + "%");

        droneMissionCount.put(bestDrone.getId(),
                droneMissionCount.getOrDefault(bestDrone.getId(), 0) + 1);

        Mission mission = new Mission(id, type, bestDrone, target);
        MissionLogger.logMission(mission, isAutoDeployed);

        MissionLog log = new MissionLog(
            id,
            start,
            target,
            (eta != -1 ? eta : 0),
            (eta != -1 ? eta * 5 : 0),
            bestDrone.getBatteryLevel() == 100,
            (eta != -1)
        );
        MissionLogger.logMissionWithStats(mission, log);

        return mission;
    }

    public void resetAssignments() {
        assignedDrones.clear();
        droneMissionCount.clear();
    }

    public List<Drone> getDronePool() {
        return dronePool;
    }
}

