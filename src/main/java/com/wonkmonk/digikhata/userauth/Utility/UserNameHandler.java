package com.wonkmonk.digikhata.userauth.Utility;

import org.springframework.stereotype.Component;

@Component
public class UserNameHandler {

    public String getNewUserName(String userName, String retailerId){
        return retailerId +
                "-" +
                userName;
    }
}
