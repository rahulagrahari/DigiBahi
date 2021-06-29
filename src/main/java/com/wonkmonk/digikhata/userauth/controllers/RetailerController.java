package com.wonkmonk.digikhata.userauth.controllers;

import com.wonkmonk.digikhata.userauth.Exception.TokenExpiredException;
import com.wonkmonk.digikhata.userauth.Exception.TokenNotFoundException;
import com.wonkmonk.digikhata.userauth.ResponsePayload.AuthenticationResponse;
import com.wonkmonk.digikhata.userauth.Utility.EmailHandler;
import com.wonkmonk.digikhata.userauth.constants.SecurityConstants;
import com.wonkmonk.digikhata.userauth.models.Address;
import com.wonkmonk.digikhata.userauth.models.EMail;
import com.wonkmonk.digikhata.userauth.models.EmailVerificationToken;
import com.wonkmonk.digikhata.userauth.models.Retailer;
import com.wonkmonk.digikhata.userauth.services.CustomEmailVerificationTokenService;
import com.wonkmonk.digikhata.userauth.services.CustomRetailerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;

@Controller
@RequestMapping("/retailer")
public class RetailerController {

    @Autowired
    CustomRetailerService customRetailerService;
    @Autowired
    CustomEmailVerificationTokenService customEmailVerificationTokenService;
    @Autowired
    EmailHandler emailHandler;
    @Autowired
    SecurityConstants securityConstants;


    @PostMapping("/signup")
    public ResponseEntity<AuthenticationResponse> signUp(@RequestBody Retailer retailer){
        HttpHeaders headers = new HttpHeaders();
        try {
            customRetailerService.save(retailer);
            customRetailerService.sendEmailVerificationMail(retailer);
            customRetailerService.createUserForNewRetailer(retailer);
            String token = customRetailerService.loginNewRetailerUser(retailer);
            headers.add(securityConstants.getHeaderString(), securityConstants.getTokenPrefix() + token);
            return new ResponseEntity<AuthenticationResponse>(new AuthenticationResponse(
                    "Retailer created successful"), headers, HttpStatus.CREATED);
        }
        catch (UnsupportedEncodingException | MessagingException e1){
            return new ResponseEntity<AuthenticationResponse>(new AuthenticationResponse(
                    "Retailer created successful"), headers, HttpStatus.CREATED);
        }
        catch (Exception e){
            String errorString = "retailer creation failed-->"+e.getMessage();
            return new ResponseEntity<AuthenticationResponse>(new AuthenticationResponse(
                    errorString), headers, HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/verify")
    public ResponseEntity<AuthenticationResponse> verifyEmail(@Param("code") String code) throws TokenExpiredException, TokenNotFoundException {
        HttpHeaders headers = new HttpHeaders();
        HttpStatus status = null;
        String msg = null;
        try {
            boolean isVerified = customEmailVerificationTokenService.verifyEmailVerificationToken(code);

            EmailVerificationToken emailVerificationToken =
                    customEmailVerificationTokenService.findEmailVerificationTokenByToken(code);
            customRetailerService.findRetailerById(emailVerificationToken.getRetailerId()).setEmailVerified(true);
            customEmailVerificationTokenService.deleteTokenFromDb(code);
            msg = "verified";
            status = HttpStatus.OK;

        }
        catch (TokenExpiredException | TokenNotFoundException e){
            System.out.println(e.getMessage());
            msg = "not verified";
            status = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<AuthenticationResponse>(new AuthenticationResponse(
                msg), headers, status);

    }
    @GetMapping("/sendmail")
    public ResponseEntity<AuthenticationResponse> sendMail() throws UnsupportedEncodingException, MessagingException {
        EMail eMail = new EMail("agraharr@tcd.ie", "digibahi@gmail.com", "hello", "I am rahul");
        emailHandler.sendEmail(eMail);
        return new ResponseEntity<AuthenticationResponse>(new AuthenticationResponse(
                "OK"), new HttpHeaders(), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping("/addaddress")
    public ResponseEntity<AuthenticationResponse> addAddress(@RequestBody Address address, @RequestHeader("Authorization") String token){
        try{
            customRetailerService.addAddress(address, token);
            return new ResponseEntity<AuthenticationResponse>(new AuthenticationResponse(
                    "Address Added"), new HttpHeaders(), HttpStatus.OK);
        }
        catch (RuntimeException e){
            return new ResponseEntity<AuthenticationResponse>(new AuthenticationResponse(
                    e.getMessage()), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
