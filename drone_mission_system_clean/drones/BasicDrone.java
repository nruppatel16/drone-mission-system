package drones;

import utils.Location;

public class BasicDrone extends Drone {

    public BasicDrone() {
        super("BD-001", "Basic", new Location(0, 0), DroneType.RESCUE); // default to RESCUE
    }

    public BasicDrone(String id, String model, Location location, DroneType type) {
        super(id, model, location, type);
    }

    @Override
    public void performMission() {
        System.out.println("✅ BasicDrone performing default mission.");
    }
}
