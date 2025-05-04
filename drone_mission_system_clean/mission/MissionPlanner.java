package mission;

import drones.*;
import utils.Location;

import java.util.*;

public class MissionPlanner {

    private List<Drone> dronePool;
    private Set<String> assignedDrones;

    public MissionPlanner(List<Drone> dronePool) {
        this.dronePool = dronePool;
        this.assignedDrones = new HashSet<>();
    }

    public Mission assignMissionAutomatically(String id, MissionType type, Location target) {
        Drone bestDrone = null;
        int minDistance = Integer.MAX_VALUE;

        // 🔍 Look for nearest available manual drone
        for (Drone drone : dronePool) {
            if (assignedDrones.contains(drone.getId()))
                continue;

            int dist = Math.abs(drone.getCurrentLocation().row - target.row)
                    + Math.abs(drone.getCurrentLocation().col - target.col);

            if (dist < minDistance) {
                bestDrone = drone;
                minDistance = dist;
            }
        }

        // 🚨 Auto-deploy drone if none available
        if (bestDrone == null) {
            String droneId = "AUTO-" + System.currentTimeMillis();
            bestDrone = new SurveillanceDrone(droneId, "AutoGen", new Location(0, 0));
            dronePool.add(bestDrone);
            System.out.println("⚠️ No idle drones found — auto-deployed: " + droneId);
        }

        assignedDrones.add(bestDrone.getId());
        Mission mission = new Mission(id, type, bestDrone, target);
        MissionLogger.logMission(mission, bestDrone.getId().startsWith("AUTO"));
        return mission;
    }

    public void resetAssignments() {
        assignedDrones.clear();
    }
}
