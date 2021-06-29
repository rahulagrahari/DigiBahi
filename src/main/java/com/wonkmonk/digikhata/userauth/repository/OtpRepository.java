package com.wonkmonk.digikhata.userauth.repository;

import com.wonkmonk.digikhata.userauth.models.OtpKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OtpRepository extends JpaRepository<OtpKey, Long> {

    OtpKey findOtpKeyByUsername(String username);
    void deleteByUsername(String username);
}
