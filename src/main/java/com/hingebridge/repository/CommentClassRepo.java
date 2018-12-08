package com.hingebridge.repository;

import com.hingebridge.model.CommentClass;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentClassRepo extends JpaRepository<CommentClass, Long>
{
    @Query("SELECT cc FROM CommentClass cc WHERE cc.post_id = :id AND cc.approved = 1")
    public Page<CommentClass> getApprovedComments(@Param("id")Long id, Pageable pageable);
    
    @Query("SELECT cc FROM CommentClass cc WHERE cc.id = :id")
    public Optional<CommentClass> getComment(@Param("id")Long id);
    
    //@Query("SELECT cc FROM CommentClass cc WHERE cc.post_id = :id AND cc.approved = 1")
    //public List<CommentClass> getApprovedComments(@Param("id")Long id);
}