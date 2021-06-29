package com.wonkmonk.digikhata.userauth.services;

import com.wonkmonk.digikhata.userauth.Utility.TokenHandler;
import com.wonkmonk.digikhata.userauth.constants.SecurityConstants;
import com.wonkmonk.digikhata.userauth.models.JwtToken;
import com.wonkmonk.digikhata.userauth.repository.JwtTokenRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomJwtService {

    @Autowired
    private JwtTokenRepo jwtTokenRepo;
    @Autowired
    SecurityConstants securityConstants;

    public String createAndSaveToken(String subject){
        try{
        TokenHandler tokenHandler = new TokenHandler(securityConstants);
        String token = tokenHandler.generateJwtToken(subject);
        this.saveTokenInDb(token);
        return token;
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    public boolean isTokenPresentInDb(String token){
        String tok = token.substring(7);
        Optional<JwtToken> jwtToken = jwtTokenRepo.findById(tok);
        return jwtToken.isPresent();
    }

    public boolean isUserPresentInDb(String username){
        Optional<JwtToken> jwtToken = jwtTokenRepo.findJwtTokenByUsername(username);
        return jwtToken.isPresent();
    }

    public String getTokenFromDb(String username){
        Optional<JwtToken> jwtToken = jwtTokenRepo.findJwtTokenByUsername(username);
        return  jwtToken.get().getToken();
    }


    public void saveTokenInDb(String token){
        TokenHandler tokenHandler = new TokenHandler(securityConstants);
        String user = tokenHandler.parseJwtToken(token);

        jwtTokenRepo.save(new JwtToken(token, user));
    }

    public void deleteTokenInDb(String username) throws Exception {

            jwtTokenRepo.delete(jwtTokenRepo.findJwtTokenByUsername(username).
                    orElseThrow(()->new RuntimeException("not found")));

    }

}
