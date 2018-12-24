package com.hingebridge.repository;

import com.hingebridge.model.AdvertObject;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdvertObjectRepo extends JpaRepository<AdvertObject, Long>
{
    //public Optional<AdvertObject> getUnexpiredAds(long userId);
}