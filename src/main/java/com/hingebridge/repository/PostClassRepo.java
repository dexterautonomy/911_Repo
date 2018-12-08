package com.hingebridge.repository;

import com.hingebridge.model.PostClass;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostClassRepo extends JpaRepository<PostClass, Long>
{
    //@Query(value="SELECT pc FROM PostClass pc WHERE LOWER(pc.category) = :category AND pc.approved = 0 ORDER BY pc.id DESC")
    //public List<PostClass> getApprovedPost(@Param("category")String category);
    
    //@Query("SELECT pc FROM PostClass pc WHERE LOWER(pc.username) = :username AND pc.date = :date")
    //public PostClass getPost(@Param("username")String username, @Param("date")String date);

    //change the approved in the @Query to 1 later.
    @Query("SELECT pc FROM PostClass pc WHERE LOWER(pc.category) = :category AND pc.approved = 0 ORDER BY pc.id DESC")
    public Page<PostClass> getApprovedPost(@Param("category")String category, Pageable pageable);
    
    @Query("SELECT pc FROM PostClass pc WHERE pc.id = :id AND LOWER(pc.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    public Optional<PostClass> getPostReader(@Param("id")Long id, @Param("title")String title);
}