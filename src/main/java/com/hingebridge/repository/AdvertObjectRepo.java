package com.hingebridge.repository;

import com.hingebridge.model.AdvertObject;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AdvertObjectRepo extends JpaRepository<AdvertObject, Long>
{
    @Query("SELECT adObj FROM AdvertObject adObj WHERE adObj.pause = 0 AND adObj.expired = 0 AND adObj.approve = 1")
    public List<AdvertObject> getActiveAds();
}