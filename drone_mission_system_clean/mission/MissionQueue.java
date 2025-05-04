package mission;

import java.util.LinkedList;
import java.util.Queue;

public class MissionQueue {
    private Queue<Mission> missionQueue;

    public Mission getNextMission() {
        return missionQueue.poll(); // Gets and removes the next mission
    }

    public MissionQueue() {
        missionQueue = new LinkedList<>();
    }

    public void addMission(Mission mission) {
        if (mission != null) {
            missionQueue.offer(mission);
            System.out.println("📝 Mission " + mission.getMissionId() + " added to queue.");
        }
    }

    public void processAllMissions() {
        while (!missionQueue.isEmpty()) {
            Mission current = missionQueue.poll();
            current.startMission();
        }
        System.out.println("✅ All missions processed.");
    }

    public boolean isEmpty() {
        return missionQueue.isEmpty();
    }

    public int getSize() {
        return missionQueue.size();
    }
}
