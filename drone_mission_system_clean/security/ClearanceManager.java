package security;

import drones.Drone;
import utils.Location;

import java.sql.*;

public class ClearanceManager {
    private static final String DB_URL = "jdbc:sqlite:clearance_logs.db";

    public ClearanceManager() {
        initializeDatabase();
    }

    private void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS ClearanceLogs (" +
                         "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                         "droneId TEXT NOT NULL," +
                         "droneType TEXT NOT NULL," +
                         "location TEXT NOT NULL," +
                         "cleared BOOLEAN NOT NULL," +
                         "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP" +
                         ");";
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("❌ Error initializing database: " + e.getMessage());
        }
    }

    public boolean hasClearance(Drone drone, Location target) {
        boolean cleared;

        if (drone.getType() == Drone.DroneType.COMBAT || drone.getType() == Drone.DroneType.SURVEILLANCE) {
            cleared = true;
        } else {
            cleared = false;
        }

        logClearanceDecision(drone, target, cleared);
        return cleared;
    }

    private void logClearanceDecision(Drone drone, Location loc, boolean cleared) {
        String locationStr = "(" + loc.row + "," + loc.col + ")";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO ClearanceLogs (droneId, droneType, location, cleared) VALUES (?, ?, ?, ?)")) {

            pstmt.setString(1, drone.getId());
            pstmt.setString(2, drone.getType().name());
            pstmt.setString(3, locationStr);
            pstmt.setBoolean(4, cleared);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("❌ Error logging clearance decision: " + e.getMessage());
        }
    }
}
