package com.wonkmonk.digikhata.userauth.repository;

import com.wonkmonk.digikhata.userauth.models.Address;
import com.wonkmonk.digikhata.userauth.models.Retailer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    Address findAddressById(long id);


}
