package com.wonkmonk.digikhata.userauth.constants;

import org.springframework.stereotype.Component;

@Component
public class PasswordResetVerificationTokenConstants implements ConstantsInterface{
    public final String SECRET = "SomeSecretKeyToGenerate";
    public final long EXPIRATION_TIME = 1000 * 60 * 60; // 1 hour expiration time
    public final String TOKEN_PREFIX = "";
    private final String EMAIL_VERIFICATION_URL = "http://localhost:8080/user/verifylink";
    @Override
    public String getSECRET() {
        return SECRET;
    }

    @Override
    public long getExpirationTime() {
        return EXPIRATION_TIME;
    }

    @Override
    public String getTokenPrefix() {
        return TOKEN_PREFIX;
    }
    public String getEMAIL_VERIFICATION_URL(){ return EMAIL_VERIFICATION_URL; }
}
