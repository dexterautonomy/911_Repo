package com.hingebridge.repository;

import com.hingebridge.model.FollowerObject;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowerObjectRepo extends JpaRepository<FollowerObject, Long>
{
    @Query("SELECT fobj FROM FollowerObject fobj WHERE fobj.user_id = :user_id AND fobj.flag = 1")
    public List<FollowerObject> getSelectedFollow(@Param("user_id")long user_id);
    
    @Query("SELECT fobj FROM FollowerObject fobj WHERE fobj.user_id = :user_id AND fobj.follower_id = :followed_id")
    public Optional<FollowerObject> followOrNotObject(@Param("user_id")long user_id, @Param("followed_id")long followed_id);
    
    public default boolean followOrNot(long user_id, long followed_id)
    {
        boolean followOrnot = false;
        Optional<FollowerObject> fobj = followOrNotObject(user_id, followed_id);
        
        if(fobj.orElse(null) != null)
        {
            if(fobj.get().getFlag() == 1)
            {
                followOrnot = true;
            }
            //if it is 0, its still false. On the other side, when you want to follow, it will check
            //first that you have not paired before.
        }
        return followOrnot;
    }
    
    @Query("SELECT fobj FROM FollowerObject fobj WHERE fobj.follower_id = :user_id AND fobj.flag = 1")
    public List<FollowerObject> myFollowers(@Param("user_id")long user_id);
    
}