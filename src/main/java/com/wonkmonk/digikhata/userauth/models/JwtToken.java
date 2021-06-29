package com.wonkmonk.digikhata.userauth.models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
public class JwtToken {

    @Id
    private String token;

    private String username;


    public JwtToken(String token, String username) {

        this.token = token;
        this.username = username;
    }

    public JwtToken() {
        super();
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


}
