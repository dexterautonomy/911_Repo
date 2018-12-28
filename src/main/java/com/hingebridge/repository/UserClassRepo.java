package com.hingebridge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hingebridge.model.UserClass;
import java.util.Optional;

@Repository
public interface UserClassRepo extends JpaRepository<UserClass, Long>
{
    public Optional<UserClass> findByUsername(String username);
    public Optional<UserClass> findByEmail(String email);
}