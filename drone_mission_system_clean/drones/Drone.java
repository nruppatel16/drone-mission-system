// File: src/drones/Drone.java
package drones;

import utils.Location;

public abstract class Drone implements IDrone {
    protected String id;
    protected String model;
    protected Location currentLocation;
    protected boolean isActive;

    public Drone(String id, String model, Location startLocation) {
        this.id = id;
        this.model = model;
        this.currentLocation = startLocation;
        this.isActive = false;
    }

    public String getId() {
        return id;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void takeOff() {
        isActive = true;
        System.out.println(model + " " + id + " taking off.");
    }

    public void land() {
        isActive = false;
        System.out.println(model + " " + id + " landing.");
    }

    public void moveTo(int x, int y) {
        currentLocation.setX(x);
        currentLocation.setY(y);
        System.out.println(model + " " + id + " moved to (" + x + ", " + y + ")");
    }

    public String getStatus() {
        return isActive ? "Active" : "Inactive";
    }

    // Abstract method that must be implemented by subclasses
    public abstract void performMission();

    public void setCurrentLocation(Location loc) {
        this.currentLocation = loc;
    }

}
