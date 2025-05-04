package drones;

import utils.Location;

public abstract class Drone {

    protected String id;
    protected String model;
    protected Location currentLocation;
    protected int batteryLevel = 100; // new field (starts full)

    public Drone(String id, String model, Location location) {
        this.id = id;
        this.model = model;
        this.currentLocation = location;
    }

    public String getId() {
        return id;
    }

    public String getModel() {
        return model;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public int getBatteryLevel() {
        return batteryLevel;
    }

    public void drainBattery(int amount) {
        batteryLevel -= amount;
        if (batteryLevel < 0) batteryLevel = 0;
    }

    public void recharge() {
        batteryLevel = 100;
    }

    public void setCurrentLocation(Location loc) {
        this.currentLocation = loc;
    }

    public abstract void performMission();

    public void decreaseBattery(int amount) {
        this.batteryLevel = Math.max(0, batteryLevel - amount);
    }

}
