package com.hingebridge.repository;

import com.hingebridge.model.SubCommentClass;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SubCommentClassRepo extends JpaRepository<SubCommentClass, Long>
{
    @Query("SELECT subcom FROM SubCommentClass subcom WHERE subcom.comment_id = :id AND subcom.approved = 1")
    public Page<SubCommentClass> getApprovedSubComment(@Param("id")Long id, Pageable pageable);
    
    @Query("SELECT subcom FROM SubCommentClass subcom WHERE subcom.id = :id")
    public Optional<SubCommentClass> getSubComment(@Param("id")Long id);
}