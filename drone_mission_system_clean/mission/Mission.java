package mission;

import drones.Drone;
import utils.Location;

public class Mission {
    private String missionId;
    private MissionType type;
    private Drone assignedDrone;
    private Location targetLocation;
    private boolean completed;

    public Mission(String missionId, MissionType type, Drone assignedDrone, Location targetLocation) {
        this.missionId = missionId;
        this.type = type;
        this.assignedDrone = assignedDrone;
        this.targetLocation = targetLocation;
        this.completed = false;
    }

    public void startMission() {
        System.out.println("Starting mission: " + missionId + " [" + type + "]");
        assignedDrone.takeOff();
        assignedDrone.moveTo(targetLocation.getX(), targetLocation.getY());
        assignedDrone.performMission();
        assignedDrone.land();
        completed = true;
        System.out.println("Mission completed: " + missionId);
    }

    public boolean isCompleted() {
        return completed;
    }

    public String getMissionId() {
        return missionId;
    }

    public MissionType getType() {
        return type;
    }

    public Drone getAssignedDrone() {
        return assignedDrone;
    }

    public Location getTargetLocation() {
        return targetLocation;
    }
}
