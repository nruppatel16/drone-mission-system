package mission;

import simulation.GridMap;
import simulation.PathFinder;
import utils.Location;
import drones.Drone;

import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MissionLogger {

    private static final String LOG_FILE = "logs/mission_log.txt";
    private static final List<MissionLog> logs = new ArrayList<>();

    public static void logMission(Mission mission, boolean autoDeployed) {
        try {
            // Ensure logs directory exists
            File logDir = new File("logs");
            if (!logDir.exists()) {
                logDir.mkdirs();
            }

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
            }
        } catch (IOException e) {
            System.out.println("⚠️ Failed to write mission log: " + e.getMessage());
        }
    }

    // ✅ Add this method
    public static void logMissionWithStats(Mission mission, MissionLog log) {
        logs.add(log);
        logMission(mission, true);  // Write basic log to file too
    }

    // ✅ Add this method
    public static void printAllLogs() {
        System.out.println("\n--- MISSION LOGS ---");
        for (MissionLog log : logs) {
            System.out.println(log);
        }
    }

    // ✅ Add this method
    public static void printSummary() {
        int total = logs.size();
        int success = 0;
        int failed = 0;
        int battery = 0;
        int recharges = 0;

        for (MissionLog log : logs) {
            if (log.success) success++;
            else failed++;
            battery += log.batteryUsed;
            if (log.recharged) recharges++;
        }

        System.out.println("\n--- SUMMARY ---");
        System.out.printf("Missions: %d | Success: %d | Failed: %d\n", total, success, failed);
        System.out.printf("Total Battery Used: %d%% | Recharges: %d\n", battery, recharges);
    }
}
