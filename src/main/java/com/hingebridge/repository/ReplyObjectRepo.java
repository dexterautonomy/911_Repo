package com.hingebridge.repository;

import com.hingebridge.model.ReplyObject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReplyObjectRepo extends JpaRepository<ReplyObject, Long>
{
    
}