package com.wonkmonk.digikhata.userauth.services;

import com.wonkmonk.digikhata.userauth.Exception.TokenExpiredException;
import com.wonkmonk.digikhata.userauth.Exception.TokenNotFoundException;
import com.wonkmonk.digikhata.userauth.Exception.UserIsLockedException;
import com.wonkmonk.digikhata.userauth.Utility.EmailHandler;
import com.wonkmonk.digikhata.userauth.Utility.OtpHandler;
import com.wonkmonk.digikhata.userauth.Utility.TokenHandler;
import com.wonkmonk.digikhata.userauth.Utility.UserNameHandler;
import com.wonkmonk.digikhata.userauth.constants.PasswordResetVerificationTokenConstants;
import com.wonkmonk.digikhata.userauth.constants.SecurityConstants;
import com.wonkmonk.digikhata.userauth.models.*;
import com.wonkmonk.digikhata.userauth.repository.ApplicationUserRepository;
import com.wonkmonk.digikhata.userauth.repository.OtpRepository;
import com.wonkmonk.digikhata.userauth.repository.RoleRepository;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.util.Date;

import static com.wonkmonk.digikhata.userauth.constants.EmailConstants.SENDER_EMAIL;


@Service
public class CustomUserService{

    @Autowired
    private ApplicationUserRepository applicationUserRepository;
    @Autowired
    private CustomAddressService customAddressService;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    UserNameHandler userNameHandler;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private OtpHandler otpHandler;
    @Autowired
    private EmailHandler emailHandler;
    @Autowired
    private OtpRepository otpRepository;
    @Autowired
    private CustomRetailerService customRetailerService;
    @Autowired
    private CustomJwtService customJwtService;
    @Autowired
    private SecurityConstants securityConstants;
    @Autowired
    private CustomPasswordResetVerificationTokenService customPasswordResetVerificationTokenService;
    @Autowired
    private PasswordResetVerificationTokenConstants passwordResetVerificationTokenConstants;
    private TokenHandler tokenHandler;

    public void saveNewUser(ApplicationUser applicationUser){
        //        System.out.println(applicationUser.getPassword());
        applicationUser.setPassword(bCryptPasswordEncoder.encode(applicationUser.getPassword()));
        long retailerId = applicationUser.getRetailerId();
        String username = userNameHandler.getNewUserName(applicationUser.getUsername().toLowerCase(), String.valueOf(retailerId));
        applicationUser.setUsername(username);
        try {

            applicationUserRepository.save(applicationUser);
        }
        catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }

    }

    public ApplicationUser findUserByUsername(String username) {

        return applicationUserRepository.findApplicationUsersByUsername(username);
    }


    public void addAddress(Address address, String token) {
        try {
            tokenHandler = new TokenHandler(securityConstants);
            String username = tokenHandler.parseJwtToken(token);
            ApplicationUser user = this.findUserByUsername(username);
            customAddressService.save(address);
            user.setAddress(address);
            applicationUserRepository.save(user);
        }
        catch(Exception e){
            System.out.println(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public void attemptAuthentication(LoginRequest loginRequest) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                    loginRequest.getPassword()));
        }
        catch(BadCredentialsException e){
            throw new BadCredentialsException(e.getMessage(), e);
        }
        catch(DisabledException e){
            throw new DisabledException(e.getMessage(), e);
        }
        catch(Exception e){
            throw new Exception(e.getMessage(), e);
        }
        ApplicationUser applicationUser = findUserByUsername(loginRequest.getUsername());
//        if (!applicationUser.isEnabled()){
//            throw new UserIsNotEnableException("User is not Enabled");
//        }
        if(!applicationUser.isAccountNonLocked()){
            throw new UserIsLockedException("User Account is Locked");
        }
        onSuccessfulAuthentication(loginRequest.getUsername());
    }

    protected void onSuccessfulAuthentication(String username) throws Exception {

        String otp = otpHandler.generateOneTimePassword();
        String retailerId = username.split("-")[0];
        Retailer retailer = customRetailerService.findRetailerById(Long.parseLong(retailerId));
        EMail email = new EMail(retailer.getCompanyEmailAddress(), SENDER_EMAIL, "OneTimePassword", otp);
        try {
            emailHandler.sendEmail(email);
            OtpKey otpKey = new OtpKey(otp, username, new Date());
            otpRepository.save(otpKey);

        } catch (MessagingException e) {
            e.printStackTrace();
            throw new Exception("User: verified, OTP: failed", e);
        }

    }

    public void signOutUser(String token) throws Exception {
        tokenHandler = new TokenHandler(securityConstants);
        String username = null;
        try {
            username = tokenHandler.parseJwtToken(token);
        }
        catch(ExpiredJwtException e){
            throw new TokenExpiredException("token expired");
        }
        if(username !=null && customJwtService.isTokenPresentInDb(token)){
            customJwtService.deleteTokenInDb(username);
        }

    }

    public void sendResetPasswordLink(String username) throws Exception {
        ApplicationUser applicationUser = this.findUserByUsername(username);
        Retailer retailer = customRetailerService.findRetailerById(applicationUser.getRetailerId());
        if(customPasswordResetVerificationTokenService.isUserPresentInDb(username)){
            customPasswordResetVerificationTokenService.deleteTokenFromDbByUsername(username);
        }
        String subject = "Reset Password Link";
        StringBuilder content = new StringBuilder("Please click the link below to reset your password");
        TokenHandler tokenHandler = new TokenHandler(passwordResetVerificationTokenConstants);
        String verificationToken = tokenHandler.generateJwtToken(username);
        String verificationUrl = passwordResetVerificationTokenConstants.getEMAIL_VERIFICATION_URL()+"?code="+verificationToken;
        content.append("\n");
        content.append(verificationUrl);
        EMail eMail = new EMail(retailer.getCompanyEmailAddress(), SENDER_EMAIL, subject, content.toString());
        emailHandler.sendEmail(eMail);
        customPasswordResetVerificationTokenService
                .saveTokenInDb(verificationToken, username);

    }

    public void updateApplicationUser(PsuedoApplicationUser pseudoApplication) {
        ApplicationUser applicationUser = this.findUserByUsername(pseudoApplication.getUsername());
        if(!pseudoApplication.getFirstname().equals(applicationUser.getFirstName())){
            applicationUser.setFirstName(pseudoApplication.getFirstname());
        }
        if(!pseudoApplication.getLastname().equals(applicationUser.getLastName())){
            applicationUser.setLastName(pseudoApplication.getLastname());
        }
        applicationUserRepository.save(applicationUser);
    }

    public void resetApplicationUserPassword(PsuedoApplicationUser pseudoApplication, String token) throws Exception {
        TokenHandler tokenHandler = new TokenHandler(securityConstants);
        String username = null;
        try {
             username = tokenHandler.parseJwtToken(token);
        }
        catch(ExpiredJwtException e){
            throw new TokenExpiredException("token expired");
        }
        ApplicationUser applicationUser = this.findUserByUsername(username);
        applicationUser.setPassword(bCryptPasswordEncoder.encode(pseudoApplication.getPassword()));
        applicationUserRepository.save(applicationUser);
        customJwtService.deleteTokenInDb(username);
    }

    public String verifyLink(String token) throws TokenExpiredException, TokenNotFoundException {
        TokenHandler tokenHandler = new TokenHandler(passwordResetVerificationTokenConstants);
        String username = null;
        if (!customPasswordResetVerificationTokenService.isPasswordResetVerificationTokenPresentInDb(token)) {
            throw new TokenNotFoundException("token not found");
        }
        try{
            username = tokenHandler.parseJwtToken(token);
        }
        catch (ExpiredJwtException e){
            customPasswordResetVerificationTokenService.deleteTokenFromDbByToken(token);
            throw new TokenExpiredException("token expired");

        }
        if (username == null) {
            customPasswordResetVerificationTokenService.deleteTokenFromDbByToken(token);
            throw new TokenNotFoundException("invalid user");
        }

        customPasswordResetVerificationTokenService.deleteTokenFromDbByToken(token);
        return username;

    }
}
