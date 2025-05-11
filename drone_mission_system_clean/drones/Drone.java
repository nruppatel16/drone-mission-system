package drones;

import utils.Location;

public abstract class Drone {

    protected String id;
    protected String model;
    protected Location currentLocation;
    protected double batteryLevel = 100.0;

    protected DroneType type; // ✅ enum field

    public enum DroneType {
        RESCUE, COMBAT, SURVEILLANCE
    }

    public Drone(String id, String model, Location location, DroneType type) {
        this.id = id;
        this.model = model;
        this.currentLocation = location;
        this.type = type;
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

    public double getBatteryLevel() {
        return batteryLevel;
    }

    public DroneType getType() {
        return type;
    }

    public void decreaseBattery(double amount) {
        batteryLevel = Math.max(0.0, batteryLevel - amount);
    }

    public void recharge() {
        batteryLevel = 100.0;
    }

    public void setCurrentLocation(Location loc) {
        this.currentLocation = loc;
    }

    public void setBatteryLevel(double level) {
        batteryLevel = Math.max(0.0, Math.min(100.0, level));
    }

    public abstract void performMission();
}
