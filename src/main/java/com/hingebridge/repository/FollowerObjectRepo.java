package com.hingebridge.repository;

import com.hingebridge.model.FollowerObject;
import java.util.LinkedList;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowerObjectRepo extends JpaRepository<FollowerObject, Long>
{
    @Query("SELECT fobj FROM FollowerObject fobj WHERE fobj.user_id = :user_id AND fobj.flag = 1")
    public List<FollowerObject> getSelectedFollow(@Param("user_id")Long user_id);
    
    /*
    public default List<Long> getUnflaggedID(Long user_id)
    {
        List<FollowerObject> fobj = getSelectedFollow(user_id);
        List<Long> fobjID = new LinkedList<>();
        
        if(fobj != null && !fobj.isEmpty())
        {
            for(FollowerObject fid : fobj)
            {
                fobjID.add(fid.getFollower_id());
            }
        }
        else
        {
            fobjID = null;
        }
        return fobjID;
    }
    */
}