package com.wonkmonk.digikhata.userauth.Utility;

import com.wonkmonk.digikhata.userauth.models.EMail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;

@Component
public class EmailHandler {
    @Autowired
    private JavaMailSender javaMailSender;

    public void sendEmail(EMail eMail) throws UnsupportedEncodingException, MessagingException {

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(eMail.getTo());
        msg.setSubject(eMail.getSubject());
        msg.setText(eMail.getContent());
        javaMailSender.send(msg);

    }
}
