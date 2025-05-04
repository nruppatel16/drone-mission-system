package security;

import java.util.*;

public class AuthManager {
    private Map<String, User> users;

    public AuthManager() {
        users = new HashMap<>();

        // Add some predefined users
        users.put("admin", new User("admin", "1234", Role.COMMANDER));
        users.put("op1", new User("op1", "pass", Role.OPERATOR));
        users.put("guest", new User("guest", "0000", Role.GUEST));
    }

    public User login(String username, String password) {
        User user = users.get(username);
        if (user != null && user.checkPassword(password)) {
            System.out.println("✅ Login successful. Role: " + user.getRole());
            return user;
        }
        System.out.println("❌ Login failed. Invalid username or password.");
        return null;
    }
}
