package com.wonkmonk.digikhata.userauth.securityFilters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wonkmonk.digikhata.userauth.Utility.EmailHandler;
import com.wonkmonk.digikhata.userauth.Utility.OtpHandler;
import com.wonkmonk.digikhata.userauth.models.ApplicationUser;
import com.wonkmonk.digikhata.userauth.models.EMail;
import com.wonkmonk.digikhata.userauth.models.OtpKey;
import com.wonkmonk.digikhata.userauth.models.Retailer;
import com.wonkmonk.digikhata.userauth.repository.OtpRepository;
import com.wonkmonk.digikhata.userauth.services.CustomRetailerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Email;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import static com.wonkmonk.digikhata.userauth.constants.EmailConstants.SENDER_EMAIL;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {


    private  OtpHandler otpHandler;

    private EmailHandler emailHandler;

    OtpRepository otpRepository;

    private  AuthenticationManager authenticationManager;

    private String username;

    private CustomRetailerService customRetailerService;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager,
                                   OtpHandler otpHandler,
                                   EmailHandler emailHandler,
                                   OtpRepository otpRepository,
                                   CustomRetailerService customRetailerService) {
        this.authenticationManager = authenticationManager;
        this.otpHandler = otpHandler;
        this.otpRepository = otpRepository;
        this.emailHandler = emailHandler;
        this.customRetailerService = customRetailerService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
            throws AuthenticationException {
        try {

            ApplicationUser creds = new ObjectMapper().readValue(req.getInputStream(), ApplicationUser.class);
            this.username = creds.getUsername();

            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(creds.getUsername(),
                    creds.getPassword(), new ArrayList<>()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain,
                                            Authentication auth) throws IOException {

        String otp = otpHandler.generateOneTimePassword();
        String retailerId = username.split("-")[0];
        Retailer retailer = customRetailerService.findRetailerById(Long.parseLong(retailerId));
        EMail email = new EMail(retailer.getCompanyEmailAddress(), SENDER_EMAIL, "OneTimePassword", otp);
        try {
            emailHandler.sendEmail(email);
            OtpKey otpKey = new OtpKey(otp, username, new Date());
            otpRepository.save(otpKey);
            res.setStatus(200);
            res.getWriter().write("User: Verified, OTP: Sent");
        } catch (MessagingException e) {
            e.printStackTrace();
            res.setStatus(417);
            res.getWriter().write("User: Verified, OTP: Failed");
        }

    }



}
