package com.wonkmonk.digikhata.userauth.repository;

import com.wonkmonk.digikhata.userauth.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByName(String roleName);
}
