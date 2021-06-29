package com.wonkmonk.digikhata.userauth.constants;

import org.springframework.stereotype.Component;

@Component
public class EmailVerificationTokenConstants implements ConstantsInterface{
    public final String SECRET = "SomeSecretKeyToGenerate";
    public final long EXPIRATION_TIME = 1000 * 60 * 60 * 24; // 1 day expiration time
    public final String TOKEN_PREFIX = "";
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
}
