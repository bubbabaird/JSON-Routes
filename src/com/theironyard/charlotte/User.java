package com.theironyard.charlotte;

/**
 * Created by BUBBABAIRD on 5/19/17.
 */
public class User {
    Integer id;
    String username;
    String address;
    String email;

    public void setId(Integer id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getId() {
        return id;

    }

    public String getUsername() {
        return username;
    }

    public String getAddress() {
        return address;
    }

    public User() {
        // needed for the json serializer/parser.
    }

    public User(Integer id, String username, String address, String email) {
        this.id = id;
        this.username = username;
        this.address = address;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

}
