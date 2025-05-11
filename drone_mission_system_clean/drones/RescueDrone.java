package drones;

import utils.Location;

public class RescueDrone extends Drone {
    private boolean hasMedicalKit;

    public RescueDrone(String id, String model, Location startLocation, boolean hasMedicalKit) {
        super(id, model, startLocation, DroneType.RESCUE); // ✅ Updated to include DroneType
        this.hasMedicalKit = hasMedicalKit;
    }

    @Override
    public void performMission() {
        if (hasMedicalKit) {
            System.out.println(model + " " + id + " is delivering medical supplies at " + currentLocation);
            hasMedicalKit = false;
        } else {
            System.out.println(model + " " + id + " completed evacuation mission at " + currentLocation);
        }
    }

    public void restockMedicalKit() {
        hasMedicalKit = true;
        System.out.println(model + " " + id + " restocked medical kit.");
    }
}
