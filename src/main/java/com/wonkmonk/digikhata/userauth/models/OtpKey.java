package com.wonkmonk.digikhata.userauth.models;

import com.fasterxml.jackson.annotation.JsonAlias;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "otp")
public class OtpKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="otp_id")
    long id;
    @Column(name="otp_key")
    @JsonAlias("otp")
    private String otpToken;
    private String username;
    private Date createdTime;

    public OtpKey() {
    }

    public OtpKey(String otpToken, String username, Date createdTime) {
        this.otpToken = otpToken;
        this.username = username;
        this.createdTime = createdTime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOtpToken() {
        return otpToken;
    }

    public void setOtpToken(String otpToken) {
        this.otpToken = otpToken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }
}
