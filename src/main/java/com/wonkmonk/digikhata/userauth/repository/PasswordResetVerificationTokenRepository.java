package com.wonkmonk.digikhata.userauth.repository;

import com.wonkmonk.digikhata.userauth.models.PasswordResetVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetVerificationTokenRepository extends JpaRepository<PasswordResetVerificationToken, Long> {
    PasswordResetVerificationToken findPasswordResetVerificationTokenByUsername(String Username);
    Optional<PasswordResetVerificationToken> findPasswordResetVerificationTokenByToken(String token);
    void deletePasswordResetVerificationTokenByToken(String token);


}
