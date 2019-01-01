package com.hingebridge.repository;

import com.hingebridge.model.InboxObject;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InboxObjectRepo extends JpaRepository<InboxObject, Long>
{
    @Query("SELECT iObj FROM InboxObject iObj WHERE iObj.deleteAdminFlag = 0")
    public List<InboxObject> getAdminInboxSize();
    
    @Query("SELECT iObj FROM InboxObject iObj WHERE iObj.deleteAdminFlag = 0 ORDER BY iObj.id DESC")
    public Page<InboxObject> getAdminInbox(Pageable pageable);
    
    @Query("SELECT iObj FROM InboxObject iObj WHERE LOWER(iObj.date) LIKE LOWER(CONCAT('%', :date, '%')) AND LOWER(iObj.content) LIKE(CONCAT('%', :content, '%'))")
    public Optional<InboxObject> findInboxObjectByDateAndContent(@Param("date")String date, @Param("content")String content);
}