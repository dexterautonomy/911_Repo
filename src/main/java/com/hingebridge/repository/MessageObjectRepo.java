package com.hingebridge.repository;

import com.hingebridge.model.MessageObject;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageObjectRepo extends JpaRepository<MessageObject, Long>
{
    @Query("SELECT mo FROM MessageObject mo WHERE mo.recipient_id = :user_id AND mo.flag = 1 ORDER BY mo.id DESC")
    public List<MessageObject> getMyMessage(@Param("user_id")Long user_id);
}