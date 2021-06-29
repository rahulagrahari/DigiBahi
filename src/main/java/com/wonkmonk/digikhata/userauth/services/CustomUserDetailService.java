package com.wonkmonk.digikhata.userauth.services;

import com.wonkmonk.digikhata.userauth.models.ApplicationUser;
import com.wonkmonk.digikhata.userauth.models.CustomUserDetail;
import com.wonkmonk.digikhata.userauth.repository.ApplicationUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    private ApplicationUserRepository applicationUserRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) {
        ApplicationUser applicationUser = applicationUserRepository.findApplicationUsersByUsername(username);

        if (applicationUser == null) {
            throw new UsernameNotFoundException("Incorrect Username and Password");
        }
        return new CustomUserDetail(applicationUser);

    }
}
