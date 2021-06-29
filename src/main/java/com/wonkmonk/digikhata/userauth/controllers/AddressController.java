package com.wonkmonk.digikhata.userauth.controllers;

import com.wonkmonk.digikhata.userauth.ResponsePayload.AuthenticationResponse;
import com.wonkmonk.digikhata.userauth.models.Address;
import com.wonkmonk.digikhata.userauth.services.CustomAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/address")
public class AddressController {

    @Autowired
    private CustomAddressService customAddressService;

    @PostMapping("/add")
    public ResponseEntity<AuthenticationResponse> addAddress(@RequestBody Address address){
        HttpHeaders headers = new HttpHeaders();
        try {
            customAddressService.save(address);
            return new ResponseEntity<AuthenticationResponse>(new AuthenticationResponse(
                    "Retailer created successful"), headers, HttpStatus.CREATED);

        }
        catch (Exception e){
            String errorString = "address creation failed-->"+e.getMessage();
            return new ResponseEntity<AuthenticationResponse>(new AuthenticationResponse(
                    errorString), headers, HttpStatus.CONFLICT);
        }

    }


}
