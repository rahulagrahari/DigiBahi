package com.wonkmonk.digikhata.userauth.services;

import com.wonkmonk.digikhata.userauth.Exception.TokenExpiredException;
import com.wonkmonk.digikhata.userauth.Exception.TokenNotFoundException;
import com.wonkmonk.digikhata.userauth.Utility.TokenHandler;
import com.wonkmonk.digikhata.userauth.constants.EmailVerificationTokenConstants;
import com.wonkmonk.digikhata.userauth.models.EmailVerificationToken;
import com.wonkmonk.digikhata.userauth.repository.EmailVerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomEmailVerificationTokenService {


    @Autowired
    EmailVerificationTokenRepository emailVerificationTokenRepository;

    @Autowired
    EmailVerificationTokenConstants emailVerificationTokenConstants;


    public void save(EmailVerificationToken emailVerificationToken){
        try {
            emailVerificationTokenRepository.save(emailVerificationToken);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            throw new RuntimeException("unable to save email verification token");
        }
    }

    public EmailVerificationToken findEmailVerificationTokenByRetailerId(long retailerId) throws TokenNotFoundException {
        EmailVerificationToken emailVerificationToken = emailVerificationTokenRepository
                .findEmailVerificationTokenByRetailerId(retailerId);
        if(emailVerificationToken==null){
            throw new TokenNotFoundException("Token not available");
        }
        return emailVerificationToken;
    }

    public EmailVerificationToken findEmailVerificationTokenByToken(String token) throws TokenNotFoundException {
        Optional<EmailVerificationToken> jwtToken = emailVerificationTokenRepository.
                findEmailVerificationTokenByToken(token);

        return jwtToken.orElseThrow(() -> new TokenNotFoundException("token not in db"));
    }

    public boolean isEmailVerificationTokenPresentInDb(String token){
        Optional<EmailVerificationToken> jwtToken = emailVerificationTokenRepository.
                findEmailVerificationTokenByToken(token);
        return jwtToken.isPresent();
    }


    public void deleteTokenFromDb(String token){
        emailVerificationTokenRepository.deleteById(emailVerificationTokenRepository.
                findEmailVerificationTokenByToken(token).get().getVerificationId());    // do exception handling here
    }

    public boolean verifyEmailVerificationToken(String token) throws TokenNotFoundException, TokenExpiredException {

        TokenHandler tokenHandler = new TokenHandler(emailVerificationTokenConstants);
        String retailerId = tokenHandler.parseJwtToken(token);
        if(retailerId != null) {
            EmailVerificationToken emailVerificationToken =
                    this.findEmailVerificationTokenByRetailerId(Long.parseLong(retailerId));
            if (emailVerificationToken != null && this.isEmailVerificationTokenPresentInDb(token)) {
                long currentTimeInMillis = System.currentTimeMillis();
                long tokenRequestedTimeInMillis = emailVerificationToken.getCreatedTime().getTime();
                if (currentTimeInMillis-tokenRequestedTimeInMillis > emailVerificationTokenConstants.getExpirationTime())
                {throw new TokenExpiredException("expired token");}
                else{
                    return true;
                }
            }

        }
        throw new TokenNotFoundException("token not found in db");
    }
}
