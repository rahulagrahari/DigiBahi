package com.wonkmonk.digikhata.userauth.controllers;

import com.wonkmonk.digikhata.userauth.Exception.TokenExpiredException;
import com.wonkmonk.digikhata.userauth.Exception.TokenNotFoundException;
import com.wonkmonk.digikhata.userauth.Exception.UserIsLockedException;
import com.wonkmonk.digikhata.userauth.ResponsePayload.AuthenticationResponse;
import com.wonkmonk.digikhata.userauth.Utility.OtpHandler;
import com.wonkmonk.digikhata.userauth.Utility.UserNameHandler;
import com.wonkmonk.digikhata.userauth.constants.SecurityConstants;
import com.wonkmonk.digikhata.userauth.models.*;
import com.wonkmonk.digikhata.userauth.repository.RoleRepository;
import com.wonkmonk.digikhata.userauth.services.CustomJwtService;
import com.wonkmonk.digikhata.userauth.services.CustomOtpService;
import com.wonkmonk.digikhata.userauth.services.CustomUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.Collections;
import java.util.HashSet;


@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    RoleRepository roleRepository;
    @Autowired
    private CustomUserService customUserService;
    @Autowired
    private OtpHandler otpHandler;
    @Autowired
    CustomOtpService customOtpService;
    @Autowired
    CustomJwtService customJwtService;
    @Autowired
    SecurityConstants securityConstants;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    UserNameHandler userNameHandler;
    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/signup")
    public ResponseEntity<AuthenticationResponse> signUp(@Valid @RequestBody PsuedoApplicationUser pseudoApplicationUser){
        Role userRole = roleRepository.findByName("ROLE_USER");
        ApplicationUser applicationUser = new ApplicationUser(
                pseudoApplicationUser.getUsername(),
                pseudoApplicationUser.getFirstname(),
                pseudoApplicationUser.getLastname(),
                null,
                pseudoApplicationUser.getPassword(),
                pseudoApplicationUser.getRetailerid(),
                true,
                true,
                false,
                new HashSet<Role>(Collections.singletonList(userRole))
                );

        HttpHeaders headers = new HttpHeaders();
        try {
            customUserService.saveNewUser(applicationUser);
            return new ResponseEntity<AuthenticationResponse>(new AuthenticationResponse(
                    "user created successful"), headers, HttpStatus.CREATED);
        }
        catch (Exception e){
            String errorString = "user creation failed-->"+e.getMessage();
            return new ResponseEntity<AuthenticationResponse>(new AuthenticationResponse(
                    errorString), headers, HttpStatus.CONFLICT);
        }

    }

    @PostMapping("/verify")
    public ResponseEntity<AuthenticationResponse> verifyUser(@RequestBody OtpKey otpKey) throws Exception {
        String msg = null;
        HttpHeaders headers = new HttpHeaders();
        try {
            boolean isVerified = otpHandler.verifyOneTimePassword(otpKey);
            if(isVerified){
                customOtpService.deleteOtp(otpKey.getUsername());
                if(customJwtService.isUserPresentInDb(otpKey.getUsername())){
                    customJwtService.deleteTokenInDb(otpKey.getUsername());
                }
                String token = customJwtService.createAndSaveToken(otpKey.getUsername());
                headers.add(securityConstants.getHeaderString(), securityConstants.getTokenPrefix() + token);
                msg = "Token: created";
                return new ResponseEntity<AuthenticationResponse>(new AuthenticationResponse(
                        msg), headers, HttpStatus.ACCEPTED);
            }
            else{
                msg = "token not verified";
            }
        } catch (Exception e1) {
            msg = e1.getMessage();

        }
        return new ResponseEntity<AuthenticationResponse>(new AuthenticationResponse(
                    msg), headers, HttpStatus.BAD_REQUEST);


    }

    @PostMapping("/addaddress")
    public ResponseEntity<AuthenticationResponse> addAddress(@RequestBody Address address, @RequestHeader("Authorization") String token) {
        try{
            customUserService.addAddress(address, token);
            return new ResponseEntity<AuthenticationResponse>(new AuthenticationResponse(
                    "Address Added"), new HttpHeaders(), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<AuthenticationResponse>(new AuthenticationResponse(
                    e.getMessage()), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody LoginRequest loginRequest) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        try {
            customUserService.attemptAuthentication(loginRequest);
            return new ResponseEntity<AuthenticationResponse>(new AuthenticationResponse
                    ("authenticate: success, OTP: sent"), headers, HttpStatus.OK);
        }
        catch(BadCredentialsException e){
            return new ResponseEntity<AuthenticationResponse>(new AuthenticationResponse
                    ("authenticate: failed, OTP: failed"), headers, HttpStatus.BAD_REQUEST);
        }
        catch(DisabledException e){
            return new ResponseEntity<AuthenticationResponse>(new AuthenticationResponse
                    ("authenticate: user disabled, OTP: failed"), headers, HttpStatus.BAD_REQUEST);
        }
        catch(UserIsLockedException e){
            return new ResponseEntity<AuthenticationResponse>(new AuthenticationResponse
                    ("authenticate: account locked, OTP: failed"), headers, HttpStatus.BAD_REQUEST);
        }
        catch(Exception e){
            return new ResponseEntity<AuthenticationResponse>(new AuthenticationResponse
                    ("authenticate: success, OTP: failed"), headers, HttpStatus.CONFLICT);
        }

    }
     @GetMapping("/signout")
    public ResponseEntity<AuthenticationResponse> signout(@RequestHeader("Authorization") String token){
         HttpHeaders headers = new HttpHeaders();
        try {
            customUserService.signOutUser(token);
            return new ResponseEntity<AuthenticationResponse>(new AuthenticationResponse
                    ("signout: success"), headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<AuthenticationResponse>(new AuthenticationResponse
                    ("signout: failed"), headers, HttpStatus.NOT_MODIFIED);

        }
     }
     @GetMapping("/resetpassword")
    public ResponseEntity<AuthenticationResponse> resetPassword(@RequestParam("un") String username) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        try{
            customUserService.sendResetPasswordLink(username);
            return new ResponseEntity<AuthenticationResponse>(new AuthenticationResponse
                    ("Reset Link sent"), headers, HttpStatus.OK);
        }
        catch (Exception e){
            return new ResponseEntity<AuthenticationResponse>(new AuthenticationResponse
                    ("Reset Link not sent"), headers, HttpStatus.NOT_MODIFIED);
        }
     }

     @PutMapping("/update")
     public ResponseEntity<AuthenticationResponse> updateUser(@RequestBody PsuedoApplicationUser pseudoApplication){
         HttpHeaders headers = new HttpHeaders();
         try {
             customUserService.updateApplicationUser(pseudoApplication);
             return new ResponseEntity<AuthenticationResponse>(new AuthenticationResponse
                     ("updated"), headers, HttpStatus.CREATED);
         }
         catch (Exception e){
             return new ResponseEntity<AuthenticationResponse>(new AuthenticationResponse
                     ("not updated"), headers, HttpStatus.NOT_MODIFIED);
         }
     }

     @PutMapping("/updatepassword")
     public ResponseEntity<AuthenticationResponse> updateUserPassword(@RequestBody PsuedoApplicationUser pseudoApplication,
                                                                      @RequestHeader("Authorization") String token) {
         HttpHeaders headers = new HttpHeaders();
         try {
             customUserService.resetApplicationUserPassword(pseudoApplication, token);
             return new ResponseEntity<AuthenticationResponse>(new AuthenticationResponse
                     ("updated"), headers, HttpStatus.CREATED);
         } catch (Exception e) {
             return new ResponseEntity<AuthenticationResponse>(new AuthenticationResponse
                     ("not updated"), headers, HttpStatus.NOT_MODIFIED);
         }
     }

     @GetMapping("verifylink")
    public ResponseEntity<AuthenticationResponse> verifyLink(@RequestParam("code") String token){
        String newToken = null;
        HttpHeaders headers = new HttpHeaders();
        try{
            String username = customUserService.verifyLink(token);
            if(customJwtService.isUserPresentInDb(username)){
                newToken = customJwtService.getTokenFromDb(username);
            }
            else{
                newToken = customJwtService.createAndSaveToken(username);
            }
            headers.add(securityConstants.getHeaderString(), securityConstants.getTokenPrefix() + newToken);
            return new ResponseEntity<AuthenticationResponse>(new AuthenticationResponse
                    ("token created"), headers, HttpStatus.OK);
        }
        catch (TokenNotFoundException | TokenExpiredException e){
            return new ResponseEntity<AuthenticationResponse>(new AuthenticationResponse
                    ("token not created"), headers, HttpStatus.CONFLICT);
        }
     }


}
