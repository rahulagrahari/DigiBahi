package com.wonkmonk.digikhata.userauth.services;

import com.wonkmonk.digikhata.userauth.Utility.EmailHandler;
import com.wonkmonk.digikhata.userauth.Utility.TokenHandler;
import com.wonkmonk.digikhata.userauth.Utility.UserNameHandler;
import com.wonkmonk.digikhata.userauth.constants.EmailVerificationTokenConstants;
import com.wonkmonk.digikhata.userauth.constants.SecurityConstants;
import com.wonkmonk.digikhata.userauth.models.*;
import com.wonkmonk.digikhata.userauth.repository.RetailerRepository;
import com.wonkmonk.digikhata.userauth.repository.RoleRepository;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.*;

import static com.wonkmonk.digikhata.userauth.constants.EmailConstants.SENDER_EMAIL;

@Service
public class CustomRetailerService {

    @Autowired
    RetailerRepository retailerRepository;
    @Autowired
    CustomAddressService customerAddressService;
    @Autowired
    CustomEmailVerificationTokenService customEmailVerificationTokenService;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    CustomUserService customerUserService;
    @Autowired
    SecurityConstants securityConstants;
    @Autowired
    EmailHandler emailHandler;
    @Autowired
    CustomJwtService customJwtService;
    @Autowired
    UserNameHandler userNameHandler;
    @Autowired
    EmailVerificationTokenConstants emailVerificationTokenConstants;

    public Retailer findRetailerById(long id){

        return retailerRepository.findByRetailerId(id);
    }

    public List<Retailer> findAllRetailer(){
        return retailerRepository.findAll();
    }

    public void save(Retailer retailer){
        try {
            retailerRepository.save(retailer);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public void sendEmailVerificationMail(Retailer retailer) throws UnsupportedEncodingException, MessagingException {

        String subject = "Email Verification";
        StringBuilder content = new StringBuilder("Please click the link below to verify your email");
        TokenHandler tokenHandler = new TokenHandler(emailVerificationTokenConstants);
        String verificationToken = tokenHandler.generateJwtToken(String.valueOf(retailer.getRetailerId()));
        String verificationUrl = securityConstants.getEmailVerificationUrl()+"?code="+verificationToken;
        content.append("\n");
        content.append(verificationUrl);
        EMail eMail = new EMail(retailer.getCompanyEmailAddress(), SENDER_EMAIL, subject, content.toString());
        emailHandler.sendEmail(eMail);
        customEmailVerificationTokenService
                .save(new EmailVerificationToken(verificationToken, retailer.getRetailerId(), new Date()));
;
    }

    public void createUserForNewRetailer(Retailer retailer){
        Role userRole = roleRepository.findByName("ROLE_USER"); // Default Role is USER
        Role adminRole = roleRepository.findByName("ROLE_ADMIN");
        List<Role> roleList = new ArrayList<Role>();
        roleList.add(userRole);
        roleList.add(adminRole);
        ApplicationUser applicationUser = new ApplicationUser(retailer.getOwnerFirstName(),
                retailer.getOwnerFirstName(),
                retailer.getOwnerLastName(),
                retailer.getAddress(),
                RandomString.make(8),
                retailer.getRetailerId(),
                false,
                false,
                true,
                new HashSet<Role>(roleList));
        customerUserService.saveNewUser(applicationUser);


    }
    public String loginNewRetailerUser(Retailer retailer){
        String username = userNameHandler.getNewUserName(retailer.getOwnerFirstName(),
                String.valueOf(retailer.getRetailerId()));
        ApplicationUser user = customerUserService.findUserByUsername(username);
        if (user != null){
            return customJwtService.createAndSaveToken(user.getUsername());
        }
        else {
            throw new UsernameNotFoundException("user not found");
        }
    }

    public void addAddress(Address address, String token) {
        try {
            TokenHandler tokenHandler = new TokenHandler(securityConstants);
            String username = tokenHandler.parseJwtToken(token);
            Retailer retailer = this.findRetailerById(customerUserService.findUserByUsername(username).getRetailerId());
            customerAddressService.save(address);
            retailer.setAddress(address);
            this.save(retailer);
        }
        catch(Exception e){
            System.out.println(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
}
