package com.example.timemachine.taskmanagement;

public class User {
    public String uid;
    public String Username;
    public String email;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email, String uid) {
        this.uid = uid;
        this.Username = username;
        this.email = email;
    }
}
