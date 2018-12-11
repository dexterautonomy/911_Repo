package com.hingebridge.repository;

import com.hingebridge.model.FollowerObject;
import com.hingebridge.model.PostClass;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
    
    @Query("SELECT pc FROM PostClass pc WHERE pc.user_id = :user_id AND LOWER(pc.date) LIKE LOWER(CONCAT('%', :date, '%')) AND LOWER(pc.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    public PostClass getOnePost(@Param("user_id")Long user_id, @Param("date")String date, @Param("title")String title);

    @Query("SELECT pc FROM PostClass pc WHERE LOWER(pc.category) = :category AND pc.approved = 1 ORDER BY pc.id DESC")
    public Page<PostClass> getApprovedPost(@Param("category")String category, Pageable pageable);
    
    @Query("SELECT pc FROM PostClass pc WHERE pc.id = :id AND LOWER(pc.title) LIKE LOWER(CONCAT('%', :title, '%'))")    //Remember ASC and DESC order
    public Optional<PostClass> getPostReader(@Param("id")Long id, @Param("title")String title);
    
    //For follower post
    @Query("SELECT pc FROM PostClass pc ORDER BY pc.id DESC")
    public Page<PostClass> getFollowerPost(Pageable pageable);
    
    public default Page<PostClass> followersPost(List<FollowerObject> fObjID, Pageable pageable)
    {
        Page<PostClass> page1 = getFollowerPost(pageable);
        List<PostClass> page2 = new LinkedList<>();
        
        if(page1 != null)
        {
            for(PostClass p : page1)
            {
                fObjID.stream().filter((followID) -> (p.getUser_id().equals(followID.getFollower_id()))).forEachOrdered((_item) -> {
                    page2.add(p);
                });
            }
            
            if(!page2.isEmpty())
            {
                page1 = new PageImpl<>(page2, pageable, page1.getTotalElements());
            }
            else
            {
                page1 = null;
            }
        }
        else
        {
            page1 = null;   //No post, work it out
        }
        return page1;
    }
}