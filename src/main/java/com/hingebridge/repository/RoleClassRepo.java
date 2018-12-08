package com.hingebridge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hingebridge.model.Role;

@Repository
public interface RoleClassRepo extends JpaRepository<Role, Long>
{
    public Role findByRolename(String rolename);
}