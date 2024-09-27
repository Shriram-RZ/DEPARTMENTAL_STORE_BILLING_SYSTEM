public class User {
    private String username;
    private String role;

    // Constructor to initialize username and role
    public User(String username, String role) {
        this.username = username;
        this.role = role;
    }

    // Getter method for username
    public String getUsername() {
        return username;
    }

    // Getter method for role
    public String getRole() {
        return role;
    }
}
