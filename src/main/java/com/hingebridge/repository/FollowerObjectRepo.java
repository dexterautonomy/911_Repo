package com.hingebridge.repository;

import com.hingebridge.model.FollowerObject;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowerObjectRepo extends JpaRepository<FollowerObject, Long>
{
    @Query("SELECT fobj FROM FollowerObject fobj WHERE fobj.user_id = :user_id AND fobj.flag = 1")
    public List<FollowerObject> getSelectedFollow(@Param("user_id")long user_id);
}