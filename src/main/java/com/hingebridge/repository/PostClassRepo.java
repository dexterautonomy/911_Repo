package com.hingebridge.repository;

import com.hingebridge.model.FollowerObject;
import com.hingebridge.model.PostClass;
import java.util.LinkedList;
import java.util.List;
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
    @Query("SELECT pc FROM PostClass pc WHERE pc.user_id = :user_id AND LOWER(pc.date) LIKE LOWER(CONCAT('%', :date, '%')) AND LOWER(pc.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    public PostClass getOnePost(@Param("user_id")Long user_id, @Param("date")String date, @Param("title")String title);

    @Query("SELECT pc FROM PostClass pc WHERE LOWER(pc.category) = :category AND pc.approved = 1 ORDER BY pc.id DESC")
    public Page<PostClass> getApprovedPost(@Param("category")String category, Pageable pageable);
    
    @Query("SELECT pc FROM PostClass pc WHERE pc.id = :id AND pc.approved = :approved AND LOWER(pc.title) LIKE LOWER(CONCAT('%', :title, '%'))")    //Remember ASC and DESC order
    public Optional<PostClass> getPostReader(@Param("id")Long id, @Param("title")String title, @Param("approved")int approved);
    
    @Query("SELECT pc FROM PostClass pc ORDER BY pc.id DESC")
    public Page<PostClass> getFollowerPost(Pageable pageable);
    
    @Query("SELECT pc FROM PostClass pc ORDER BY pc.id DESC")
    public List<PostClass> getFollowerPost();
    
    public default List<PostClass> followersPost(List<FollowerObject> fObjID)
    {
        List<PostClass> pcList = getFollowerPost();
        if(!pcList.isEmpty())
        {
            List<PostClass> pcList2 = new LinkedList<>();
            for(PostClass p : pcList)
            {
                for(FollowerObject followID : fObjID)
                {
                    if(p.getUser_id().equals(followID.getFollower_id()))
                    {
                        pcList2.add(p);
                    }
                }
            }
            return pcList2;
        }
        else
        {
            return null;
        }
    }
    
    @Query("SELECT pc FROM PostClass pc WHERE pc.user_id = :user_id AND pc.flag = 1 ORDER BY pc.id DESC")
    public List<PostClass> getAllMyPost(@Param("user_id")Long user_id);
}