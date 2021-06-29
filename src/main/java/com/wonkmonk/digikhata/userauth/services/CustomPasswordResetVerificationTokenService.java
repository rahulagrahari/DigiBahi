package com.wonkmonk.digikhata.userauth.services;

import com.wonkmonk.digikhata.userauth.Exception.TokenNotFoundException;
import com.wonkmonk.digikhata.userauth.Utility.TokenHandler;
import com.wonkmonk.digikhata.userauth.constants.PasswordResetVerificationTokenConstants;
import com.wonkmonk.digikhata.userauth.models.PasswordResetVerificationToken;
import com.wonkmonk.digikhata.userauth.repository.PasswordResetVerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class CustomPasswordResetVerificationTokenService {
    @Autowired
    PasswordResetVerificationTokenRepository passwordResetVerificationTokenRepository;
    @Autowired
    PasswordResetVerificationTokenConstants passwordResetVerificationTokenConstants;

    public String createAndSaveToken(String username){

        try {
            TokenHandler tokenHandler = new TokenHandler(passwordResetVerificationTokenConstants);
            String token = tokenHandler.generateJwtToken(username);
            this.saveTokenInDb(token, username);
            return token;
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            throw new RuntimeException("unable to save password reset verification token");
        }

    }

    public void saveTokenInDb(String token, String username){

        passwordResetVerificationTokenRepository.save(new PasswordResetVerificationToken(token, username, new Date()));
    }

    public boolean isPasswordResetVerificationTokenPresentInDb(String token){
        Optional<PasswordResetVerificationToken> jwtToken = passwordResetVerificationTokenRepository.
                findPasswordResetVerificationTokenByToken(token);
        return jwtToken.isPresent();
    }

    public PasswordResetVerificationToken findPasswordResetVerificationTokenByUsername(String username) throws TokenNotFoundException {
        PasswordResetVerificationToken passwordResetVerificationToken = passwordResetVerificationTokenRepository
                .findPasswordResetVerificationTokenByUsername(username);
        if (passwordResetVerificationToken == null) {
            throw new TokenNotFoundException("Token not available");
        }
        return passwordResetVerificationToken;
    }

    public boolean isUserPresentInDb(String username){
        PasswordResetVerificationToken passwordResetVerificationToken = passwordResetVerificationTokenRepository
                .findPasswordResetVerificationTokenByUsername(username);
        return passwordResetVerificationToken != null;
    }

    public void deleteTokenFromDbByUsername(String username){
        PasswordResetVerificationToken pwdToken = passwordResetVerificationTokenRepository.findPasswordResetVerificationTokenByUsername(username);
        if(pwdToken != null){
            passwordResetVerificationTokenRepository.delete(pwdToken);
        }
    }
    public void deleteTokenFromDbByToken(String token){
        try{
            passwordResetVerificationTokenRepository.deletePasswordResetVerificationTokenByToken(token);
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

}
