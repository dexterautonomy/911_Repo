package com.hingebridge.repository;

import com.hingebridge.model.InboxObject;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface InboxObjectRepo extends JpaRepository<InboxObject, Long>
{
    @Query("SELECT iObj FROM InboxObject iObj WHERE iObj.deleteAdminFlag = 0")
    public List<InboxObject> getAllForSize();
}