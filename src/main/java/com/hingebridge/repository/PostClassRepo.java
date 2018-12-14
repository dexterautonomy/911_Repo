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
    
    /*
    public default Page<PostClass> followersPost(List<FollowerObject> fObjID, Pageable pageable)
    {
        Page<PostClass> page1 = getFollowerPost(pageable);
        List<PostClass> page2 = new LinkedList<>();
        
        if(!page1.isEmpty())
        {
            for(PostClass p : page1)
            {
                for(FollowerObject followID : fObjID)
                {
                    if(p.getUser_id().equals(followID.getFollower_id()))
                    {
                        page2.add(p);
                    }
                }
            }
            page1 = new PageImpl<>(page2, pageable, page1.getTotalElements());
        }
        else
        {
            page1 = null;
        }
        return page1;
    }
    */
    
    //For follower post
    @Query("SELECT pc FROM PostClass pc ORDER BY pc.id DESC")
    public List<PostClass> getFollowerPost();
    
    public default List<PostClass> followersPost(List<FollowerObject> fObjID)//, int range)
    {
        //int init = 0;
        //int end = 5;
        List<PostClass> pcList = getFollowerPost();
        List<PostClass> pcList2 = new LinkedList<>();
        //List<PostClass> pcList3 = new LinkedList<>();
        
        /*
        if(range > 1)
        {
            init = (range - 1) * end;
            end = end * range;
        }
        */
        
        if(!pcList.isEmpty())
        {
            for(PostClass p : pcList)
            {
                for(FollowerObject followID : fObjID)
                {
                    if(p.getUser_id().equals(followID.getFollower_id()))
                    {
                        pcList2.add(p);
                        //put it here instead
                    }
                }
            }
            
            /*
            for(int count = init; count < end; count++)
            {
                if(pcList2.get(count) != null)
                {
                    pcList3.add(pcList2.get(count));
                }
            }
            */
                    
            return pcList2;
        }
        else
        {
            return null;
        }
    }
}