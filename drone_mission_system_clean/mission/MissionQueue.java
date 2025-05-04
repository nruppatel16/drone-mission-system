package mission;

import java.util.LinkedList;
import java.util.Queue;
import java.util.List;
import java.util.ArrayList;


public class MissionQueue {
    private Queue<Mission> missionQueue;
    private List<Mission> missions = new ArrayList<>();


    public Mission getNextMission() {
        return missionQueue.poll(); // Gets and removes the next mission
    }

    public MissionQueue() {
        missionQueue = new LinkedList<>();
    }

    public void addMission(Mission mission) {
        if (mission != null) {
            missionQueue.offer(mission);
            missions.add(mission);
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

    public List<Mission> getAllMissions() {
        return new ArrayList<>(missions); // Assuming missions is your internal list
    }
}
