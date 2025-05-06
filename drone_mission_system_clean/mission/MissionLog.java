package mission;

import utils.Location;

public class MissionLog {
    public String missionId;
    public Location start;
    public Location target;
    public int steps;
    public int batteryUsed;
    public boolean recharged;
    public boolean success;

    public MissionLog(String missionId, Location start, Location target, int steps, int batteryUsed, boolean recharged, boolean success) {
        this.missionId = missionId;
        this.start = start;
        this.target = target;
        this.steps = steps;
        this.batteryUsed = batteryUsed;
        this.recharged = recharged;
        this.success = success;
    }

    @Override
    public String toString() {
        return String.format("Mission %s: (%d,%d) → (%d,%d) | Steps: %d | Battery: %d%% | Recharged: %s | %s",
                missionId,
                start.row, start.col,
                target.row, target.col,
                steps,
                batteryUsed,
                recharged ? "Yes" : "No",
                success ? "Success" : "Failed"
        );
    }
}
