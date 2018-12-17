package com.hingebridge.repository;

import com.hingebridge.model.QuoteObject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuoteObjectRepo extends JpaRepository<QuoteObject, Long>
{
    
}