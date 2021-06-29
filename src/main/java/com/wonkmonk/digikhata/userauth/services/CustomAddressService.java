package com.wonkmonk.digikhata.userauth.services;

import com.wonkmonk.digikhata.userauth.Exception.AddressNotFoundException;
import com.wonkmonk.digikhata.userauth.models.Address;
import com.wonkmonk.digikhata.userauth.repository.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomAddressService {

    @Autowired
    AddressRepository addressRepository;

    public long save(Address address) throws RuntimeException {
        try{
            addressRepository.save(address);
            return address.getId();
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public Address findAddressById(long addressId) throws AddressNotFoundException {
        Address address = addressRepository.findAddressById(addressId);
        if (address == null){
            throw new AddressNotFoundException("Address not found with the given id");
        }
        return address;

    }

}
