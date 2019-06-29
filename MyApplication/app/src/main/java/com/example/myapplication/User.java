package com.example.myapplication;

public class User
{
    public String username;
    public String pretty_name;
    public String image_url;
    private String token;

    public User(String username)
    {
        this.username = username;
        this.token = "";
    }


    // Getters
    public String getUsername() {
        return username;
    }

    public String getToken() {
        return token;
    }

    // Setters
    public void setToken(String token) {
        this.token = token;
    }

}
