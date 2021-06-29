package com.wonkmonk.digikhata.userauth.models;

import org.hibernate.annotations.GeneratorType;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="emailverificationtable")
public class EmailVerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="verification_id")
    private long verificationId;
    private String token;
    private long retailerId;
    private Date createdTime;


    public EmailVerificationToken() {
    }

    public EmailVerificationToken(String token, long retailerId, Date createdTime) {
        this.token = token;
        this.retailerId = retailerId;
        this.createdTime = createdTime;
    }

    public long getVerificationId() {
        return verificationId;
    }

    public void setVerificationId(long verificationId) {
        this.verificationId = verificationId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getRetailerId() {
        return retailerId;
    }

    public void setRetailerId(long retailerId) {
        this.retailerId = retailerId;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }
}
