package com.wonkmonk.digikhata.userauth.models;

public class PsuedoApplicationUser {
    private String username;
    private String firstname;
    private String lastname;
    private String password;
    private long retailerid;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public long getRetailerid() {
        return retailerid;
    }

    public void setRetailerid(long retailerid) {
        this.retailerid = retailerid;
    }
}
