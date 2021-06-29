package com.wonkmonk.digikhata.userauth.repository;

import com.wonkmonk.digikhata.userauth.models.JwtToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JwtTokenRepo extends JpaRepository<JwtToken, String> {

    Optional<JwtToken> findJwtTokenByUsername(String username);
    void deleteJwtTokenByUsername(String username);

}
