package com.hingebridge.repository;

import com.hingebridge.model.MessageObject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageObjectRepo extends JpaRepository<MessageObject, Long>
{
    
}