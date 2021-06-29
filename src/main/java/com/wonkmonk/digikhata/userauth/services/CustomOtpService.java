package com.wonkmonk.digikhata.userauth.services;

import com.wonkmonk.digikhata.userauth.Exception.TokenNotFoundException;
import com.wonkmonk.digikhata.userauth.models.OtpKey;
import com.wonkmonk.digikhata.userauth.repository.OtpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CustomOtpService {
    @Autowired
    private OtpRepository otpRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public void save(OtpKey otpKey){
        otpKey.setOtpToken(bCryptPasswordEncoder.encode(otpKey.getOtpToken()));
        try {
            otpRepository.save(otpKey);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }

    }

    private boolean isKeyAlreadyAvailable(String username) {
        OtpKey otpObj = otpRepository.findOtpKeyByUsername(username);
        return otpObj != null;
    }

    public OtpKey getOtpFromDb(String username) throws TokenNotFoundException {

        if (!isKeyAlreadyAvailable(username)){
            throw new TokenNotFoundException("Otp not in the db");
        }
        return otpRepository.findOtpKeyByUsername(username);
    }

    public void deleteOtp(String username) throws TokenNotFoundException {
        if (!isKeyAlreadyAvailable(username)){
            throw new TokenNotFoundException("Otp not in the db");
        }
            otpRepository.deleteByUsername(username);
        }


}
