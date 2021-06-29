package com.wonkmonk.digikhata.userauth.constants;

import org.springframework.stereotype.Component;


public interface ConstantsInterface {


    public String getSECRET();
    public long getExpirationTime();
    public String getTokenPrefix();

}
