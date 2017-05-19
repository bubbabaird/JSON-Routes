package com.theironyard.charlotte;

/**
 * Created by BUBBABAIRD on 5/19/17.
 */
public class User {
    Integer id;
    String username;
    String address;
    String email;

    public Integer getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getAddress() {
        return address;
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
