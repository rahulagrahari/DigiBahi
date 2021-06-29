package com.wonkmonk.digikhata.userauth.constants;


public class OtpConstants implements ConstantsInterface {
    public static final long OTP_VALID_DURATION = 5 * 60 * 1000; // 5 minutes
    public static final int OTP_LENGTH = 6;

    @Override
    public String getSECRET() {
        return null;
    }

    @Override
    public long getExpirationTime() {
        return 0;
    }

    @Override
    public String getTokenPrefix() {
        return null;
    }
}
