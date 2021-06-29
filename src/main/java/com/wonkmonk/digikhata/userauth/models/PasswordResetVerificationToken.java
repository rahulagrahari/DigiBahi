package com.wonkmonk.digikhata.userauth.models;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="passwordresetverificationtoken")
public class PasswordResetVerificationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="pwdverification_id")
    private long pwdverificationId;
    private String token;
    private String username;
    private Date createdTime;

    public PasswordResetVerificationToken() {
    }

    public PasswordResetVerificationToken(String token, String username, Date createdTime) {
        this.token = token;
        this.username = username;
        this.createdTime = createdTime;
    }

    public long getPwdverificationId() {
        return pwdverificationId;
    }

    public void setPwdverificationId(long pwdverificationId) {
        this.pwdverificationId = pwdverificationId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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
