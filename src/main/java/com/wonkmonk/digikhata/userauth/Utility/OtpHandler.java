package com.wonkmonk.digikhata.userauth.Utility;

import com.wonkmonk.digikhata.userauth.Exception.TokenExpiredException;
import com.wonkmonk.digikhata.userauth.Exception.TokenNotFoundException;
import com.wonkmonk.digikhata.userauth.models.OtpKey;
import com.wonkmonk.digikhata.userauth.repository.OtpRepository;
import com.wonkmonk.digikhata.userauth.services.CustomOtpService;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.wonkmonk.digikhata.userauth.constants.OtpConstants.OTP_LENGTH;
import static com.wonkmonk.digikhata.userauth.constants.OtpConstants.OTP_VALID_DURATION;

@Component
public class OtpHandler {

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private CustomOtpService customOtpService;


    public String generateOneTimePassword(){
        return RandomString.make(OTP_LENGTH);
    }

    public boolean verifyOneTimePassword(OtpKey otpKey) throws TokenNotFoundException, TokenExpiredException {
        OtpKey otpObj = customOtpService.getOtpFromDb(otpKey.getUsername());
        long currentTimeInMillis = System.currentTimeMillis();
        long otpRequestedTimeInMillis = otpObj.getCreatedTime().getTime();
        // OTP expires
        if(otpObj.getOtpToken().equals(otpKey.getOtpToken())){
            if(otpRequestedTimeInMillis + OTP_VALID_DURATION >= currentTimeInMillis){
                throw new TokenExpiredException("Stale OTP");
            }
            return true;
        }
        return false;

    }



}
