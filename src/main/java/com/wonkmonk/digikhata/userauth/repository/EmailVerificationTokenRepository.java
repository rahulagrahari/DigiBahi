package com.wonkmonk.digikhata.userauth.repository;

import com.wonkmonk.digikhata.userauth.models.EmailVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {

    EmailVerificationToken findEmailVerificationTokenByRetailerId(long retailerId);
    Optional<EmailVerificationToken> findEmailVerificationTokenByToken(String token);
}
