package com.hingebridge.repository;

import com.hingebridge.model.FollowedPostDeleteObject;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowedPostDeleteObjectRepo extends JpaRepository<FollowedPostDeleteObject, Long>
{
    @Query("SELECT fpdo FROM FollowedPostDeleteObject fpdo WHERE fpdo.postId = :post_id AND fpdo.userId = :user_id")
    public Optional<FollowedPostDeleteObject> getDeletedPost(@Param("post_id")long post_id, @Param("user_id")long user_id);
    
    public default boolean getDeletedPostObject(long post_id, long user_id)
    {
        boolean delete = false;
        
        Optional<FollowedPostDeleteObject> fpdo = getDeletedPost(post_id, user_id);
        
        if(fpdo.orElse(null) != null)   //If the fpdo object exist or null if it does not exist
        {
            if(fpdo.get().getFlagDelete() == 1) //Xtra check: If the object exists and flagDelete is 1, it means followedPost has been deleted
            {
                delete = true;
            }
        }
        
        return delete;  //If the fpdo object does not exist, it means the followedPost has not been deleted yet
    }
    
    public default boolean getReadPostObject(long post_id, long user_id)
    {
        boolean unReadAndUndeleted = false;    //Has not been read and has not been deleted
        
        Optional<FollowedPostDeleteObject> fpdo = getDeletedPost(post_id, user_id);
        
        if(fpdo.orElse(null) != null)   //If the fpdo object exist or null if it does not exist
        {
            if(fpdo.get().getFlagRead() == 1 && fpdo.get().getFlagDelete() == 1) //When you delete without reading
            {
                unReadAndUndeleted = true;
            }
            else if(fpdo.get().getFlagRead() == 1 && fpdo.get().getFlagDelete() == 0) //Read it but have not deleted yet
            {
                unReadAndUndeleted = true;
            }
        }
        
        return unReadAndUndeleted;   //If the fpdo object does not exist, it means the followedPost has not been read yet
    }
}