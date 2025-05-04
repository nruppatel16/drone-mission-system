package mission;
import simulation.GridMap;
import simulation.PathFinder;
import utils.Location;
import java.util.List;

import drones.Drone;
import utils.Location;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;



public class MissionLogger {

    private static final String LOG_FILE = "logs/mission_log.txt";

    public static void logMission(Mission mission, boolean autoDeployed) {
        try (FileWriter writer = new FileWriter(LOG_FILE, true)) {
            String timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            Drone drone = mission.getAssignedDrone();
            Location target = mission.getTargetLocation();

            String log = String.format("[%s] MISSION: %s | Type: %s | Drone: %s | Auto-Deployed: %b | Target: (%d, %d)",
                    timestamp,
                    mission.getMissionId(),
                    mission.getType(),
                    mission.getMissionId(),
                    autoDeployed,
                    target.row,
                    target.col
            );

            writer.write(log + "\n");
        } catch (IOException e) {
            System.out.println("⚠️ Failed to write mission log: " + e.getMessage());
        }
    }
}
