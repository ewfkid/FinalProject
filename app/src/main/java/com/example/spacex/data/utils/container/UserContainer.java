package com.example.spacex.data.utils.container;

public class UserContainer {

    private final String name;

    private final String username;

    private final String photoUrl;

    private final String phone;

    private final String email;

    public UserContainer(
            String name,
            String username,
            String photoUrl,
            String phone,
            String email
    ) {
        this.name = name;
        this.username = username;
        this.photoUrl = photoUrl;
        this.phone = phone;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }
}
