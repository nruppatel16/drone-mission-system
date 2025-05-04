// File: src/drones/IDrone.java
package drones;

public interface IDrone {
    void takeOff();

    void land();

    void moveTo(int x, int y);

    String getStatus();
}
