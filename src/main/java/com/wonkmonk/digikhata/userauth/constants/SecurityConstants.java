package com.wonkmonk.digikhata.userauth.constants;

import org.springframework.stereotype.Component;

@Component
public class SecurityConstants implements ConstantsInterface {
    private final String SECRET = "SomeSecretKeyToGenerate";
    private final long EXPIRATION_TIME = 864_000_000; // 10 days
    private final String TOKEN_PREFIX = "Bearer ";
    private final String HEADER_STRING = "Authorization";
    private final String SIGN_UP_URL = "/users/sign-up";
    private final String EMAIL_VERIFICATION_URL = "http://localhost:8080/retailer/verify";


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

    public String getHeaderString(){
        return HEADER_STRING;
    }
    public String getSignUpUrl(){
        return SIGN_UP_URL;
    }
    public String getEmailVerificationUrl(){
        return EMAIL_VERIFICATION_URL;
    }

}
