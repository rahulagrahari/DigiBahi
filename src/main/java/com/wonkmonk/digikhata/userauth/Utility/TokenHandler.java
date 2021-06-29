package com.wonkmonk.digikhata.userauth.Utility;

import com.wonkmonk.digikhata.userauth.constants.ConstantsInterface;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;

public class TokenHandler {
    private final ConstantsInterface constants;

    public TokenHandler(ConstantsInterface constants) {
        this.constants = constants;
    }

    public String generateJwtToken(String subject){
        String token = Jwts.builder().setSubject(subject)
                .setExpiration(new Date(System.currentTimeMillis() + this.constants.getExpirationTime()))
                .signWith(SignatureAlgorithm.HS512, this.constants.getSECRET().getBytes()).compact();

        return token;
    }

    public String parseJwtToken(String token) {

        String subject = Jwts.parser()
                .setSigningKey(this.constants.getSECRET().getBytes())
                .parseClaimsJws(token.replace(this.constants.getTokenPrefix(), ""))
                .getBody()
                .getSubject();

        return subject;
    }

    public Date parseJwtTokenForTime(String token) {

        Date expirationTime = Jwts.parser()
                .setSigningKey(this.constants.getSECRET().getBytes())
                .parseClaimsJws(token.replace(this.constants.getTokenPrefix(), ""))
                .getBody()
                .getExpiration();

        return expirationTime;
    }

    public boolean verifyTokenForExpiration(String token){
        Date expirationTime = parseJwtTokenForTime(token);
        return System.currentTimeMillis() < expirationTime.getTime();
    }
}
