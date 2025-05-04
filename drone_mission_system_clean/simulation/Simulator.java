package simulation;

import mission.Mission;
import mission.MissionQueue;
import mission.MissionLogger;
import utils.Location;

import java.util.List;

public class Simulator {

    private MissionQueue missionQueue;

    public Simulator(MissionQueue queue) {
        this.missionQueue = queue;
    }

    public void runSimulation() {
        System.out.println("🧠 Starting simulation...");

        int step = 1;
        while (!missionQueue.isEmpty()) {
            System.out.println("\n⏱️ Step " + step + ":");
            Mission current = missionQueue.getNextMission();
            if (current != null) {
                GridMap grid = new GridMap(10, 10);
                grid.clear();

                Location start = current.getAssignedDrone().getCurrentLocation();
                Location end = current.getTargetLocation();

                // Optional obstacles for testing
                grid.placeObstacle(3, 3);
                grid.placeObstacle(4, 3);
                grid.placeObstacle(5, 3);

                List<Location> path = PathFinder.findPath(grid, start, end);

                if (path.isEmpty()) {
                    System.out.println("❌ No path found for this mission.");
                    continue;
                }

                System.out.println("Path length: " + path.size()); // ✅ Added here

                // Animate the drone along the path
                for (Location stepLoc : path) {
                    grid.placeDrone(stepLoc);
                    grid.placeTarget(end);
                    grid.printMap(stepLoc, end, path);

                    try {
                        Thread.sleep(300); // simulate delay
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                System.out.println("✅ Mission path completed. Steps taken: " + path.size());

                current.startMission();
                MissionLogger.logMission(current,false);
            }
            step++;
        }

        System.out.println("\n🛑 Simulation complete. All missions executed.");
    }
}
