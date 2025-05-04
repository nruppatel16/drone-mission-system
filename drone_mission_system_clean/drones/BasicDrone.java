package drones;

import utils.Location;

public class BasicDrone extends Drone {

    public BasicDrone() {
        super("BD-001", "Basic", new Location(0, 0));
    }

    public BasicDrone(String id, String model, Location location) {
        super(id, model, location);
    }

    @Override
    public void performMission() {
        System.out.println("✅ BasicDrone performing default mission.");
    }
}
