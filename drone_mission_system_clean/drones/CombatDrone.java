package drones;

import utils.Location;

public class CombatDrone extends Drone {
    private int missileCount;

    public CombatDrone(String id, String model, Location startLocation, int missileCount) {
        super(id, model, startLocation, DroneType.COMBAT); // ✅ Pass DroneType
        this.missileCount = missileCount;
    }

    @Override
    public void performMission() {
        if (missileCount > 0) {
            System.out.println(model + " " + id + " is engaging target at " + currentLocation + " with missiles.");
            missileCount--;
        } else {
            System.out.println(model + " " + id + " has no missiles left!");
        }
    }

    public int getMissileCount() {
        return missileCount;
    }

    public void reloadMissiles(int count) {
        missileCount += count;
        System.out.println(model + " " + id + " reloaded with " + count + " missiles.");
    }
}
