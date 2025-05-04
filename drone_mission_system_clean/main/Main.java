package main;

import drones.*;
import mission.*;
import simulation.Simulator;
import utils.Location;
import security.*;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        AuthManager auth = new AuthManager();

        // Login
        System.out.println("Welcome to Autonomous Drone Command System");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        User currentUser = auth.login(username, password);
        if (currentUser == null)
            return;

        Role role = currentUser.getRole();
        System.out.println("\nHello " + currentUser.getUsername() + "! You are logged in as " + role);

        if (role == Role.COMMANDER) {
            List<Drone> dronePool = new ArrayList<>();
            MissionQueue queue = new MissionQueue();
            MissionPlanner planner = new MissionPlanner(dronePool);

            while (true) {
                System.out.println("\n===== COMMANDER MENU =====");
                System.out.println("[1] Add Drone");
                System.out.println("[2] Assign Mission (Smart)");
                System.out.println("[3] Run Simulation");
                System.out.println("[4] View Logs");
                System.out.println("[5] Logout");
                System.out.print("Enter your choice: ");

                int choice = scanner.nextInt();
                scanner.nextLine();

                if (choice == 1) {
                    System.out.print("Drone Type (combat/surveillance/rescue): ");
                    String type = scanner.nextLine();
                    System.out.print("Drone ID: ");
                    String id = scanner.nextLine();
                    System.out.print("Model: ");
                    String model = scanner.nextLine();
                    Location loc = new Location(0, 0); // Default position

                    switch (type.toLowerCase()) {
                        case "combat":
                            System.out.print("Missile count: ");
                            int missiles = scanner.nextInt();
                            scanner.nextLine();
                            dronePool.add(new CombatDrone(id, model, loc, missiles));
                            break;
                        case "surveillance":
                            dronePool.add(new SurveillanceDrone(id, model, loc));
                            break;
                        case "rescue":
                            dronePool.add(new RescueDrone(id, model, loc, true));
                            break;
                        default:
                            System.out.println("Invalid drone type.");
                    }

                } else if (choice == 2) {
                    System.out.print("Mission ID: ");
                    String missionId = scanner.nextLine();
                    System.out.print("Type (combat/surveillance/rescue): ");
                    String mtype = scanner.nextLine();
                    System.out.print("Target X: ");
                    int x = scanner.nextInt();
                    System.out.print("Target Y: ");
                    int y = scanner.nextInt();
                    scanner.nextLine();
                    Location target = new Location(x, y);

                    MissionType missionType;
                    switch (mtype.toLowerCase()) {
                        case "combat":
                            missionType = MissionType.COMBAT;
                            break;
                        case "surveillance":
                            missionType = MissionType.SURVEILLANCE;
                            break;
                        case "rescue":
                            missionType = MissionType.RESCUE;
                            break;
                        default:
                            System.out.println("Invalid mission type.");
                            continue;
                    }

                    Mission mission = planner.assignMissionAutomatically(missionId, missionType, target);
                    queue.addMission(mission);

                    if (mission != null)
                        queue.addMission(mission);

                } else if (choice == 3) {
                    Simulator sim = new Simulator(queue,planner);
                    sim.runSimulation();
                    planner.resetAssignments(); // ✅ Allow drone reuse after sim

                } else if (choice == 4) {
                    System.out.println("Logs available at: logs/mission_log.txt");

                } else if (choice == 5) {
                    System.out.println("Logging out.");
                    break;

                } else {
                    System.out.println("Invalid choice.");
                }
            }

        } else if (role == Role.OPERATOR) {
            System.out.println("\n===== OPERATOR MODE =====");
            MissionQueue queue = new MissionQueue();
            queue.addMission(new Mission("M-101", MissionType.SURVEILLANCE,
                    new SurveillanceDrone("DR-S2", "HawkEye", new Location(3, 3)), new Location(7, 4)));
            queue.addMission(new Mission("M-102", MissionType.RESCUE,
                    new RescueDrone("DR-R2", "AngelWing", new Location(5, 1), true), new Location(6, 6)));
            Simulator sim = new Simulator(queue,null);
            sim.runSimulation();

        } else if (role == Role.GUEST) {
            System.out.println("\n===== GUEST MODE =====");
            System.out.println("You may only view mission logs.");
            System.out.println("Check logs at: logs/mission_log.txt");
        }

        scanner.close();
    }
}
