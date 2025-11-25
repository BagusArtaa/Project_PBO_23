package model;

public class User {
    private int id;
    private String username;
    private String passwordHash;
    private String createdAt;

    public User(int id, String username, String passwordHash, String createdAt) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
