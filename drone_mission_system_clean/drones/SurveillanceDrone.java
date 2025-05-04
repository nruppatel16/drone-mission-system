package drones;

import utils.Location;

public class SurveillanceDrone extends Drone {
    public SurveillanceDrone(String id, String model, Location startLocation) {
        super(id, model, startLocation);
    }

    @Override
    public void performMission() {
        System.out.println(model + " " + id + " is performing a surveillance mission at " + currentLocation);
    }
}
