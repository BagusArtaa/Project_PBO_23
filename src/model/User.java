package model;

public class User {
    private int id;
    private String username;
    private String createdAt;

    public User(int id, String username, String createdAt) {
        this.id = id;
        this.username = username;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }
    public String getUsername() {
        return username;
    }
    public String getCreatedAt() {
        return createdAt;
    }
}
