package com.hingebridge.repository;

import com.hingebridge.model.QuoteInboxObject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuoteInboxObjectRepo extends JpaRepository<QuoteInboxObject, Long>
{
    
}