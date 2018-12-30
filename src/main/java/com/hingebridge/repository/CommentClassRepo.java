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
    
    @Query("SELECT cc FROM CommentClass cc WHERE cc.id = :id")  //Find out who is using this method
    public Optional<CommentClass> getComment(@Param("id")Long id);
    
    @Query("SELECT cc FROM CommentClass cc WHERE cc.user_id = :user_id AND cc.post_id = :post_id AND LOWER(cc.postdate) LIKE LOWER(CONCAT('%', :postdate, '%'))")
    public Optional<CommentClass> getExactPost(@Param("user_id")long user_id, @Param("post_id")long post_id, @Param("postdate")String postdate);
    
    //FOR ADMIN
    @Query("SELECT cc FROM CommentClass cc WHERE cc.post_id = :id AND cc.approved = 1")
    public Page<CommentClass> getCommentsForAdmin(@Param("id")Long id, Pageable pageable);
}