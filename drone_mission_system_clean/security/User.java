package security;

public class User {
    private String username;
    private String password;
    private Role role;

    public User(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public boolean checkPassword(String input) {
        return this.password.equals(input);
    }

    public Role getRole() {
        return role;
    }
}
