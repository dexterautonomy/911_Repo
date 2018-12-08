package com.hingebridge.repository;

import com.hingebridge.model.PostLikeClass;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostLikeClassRepo extends JpaRepository<PostLikeClass, Long>
{
    @Query("SELECT plc FROM PostLikeClass plc WHERE plc.post_id = :post_id AND plc.user_id = :user_id")
    public Optional<PostLikeClass> checkLikedBefore(@Param("post_id")Long post_id, @Param("user_id")Long user_id);

    public default String likedBefore(Long post_id, Long user_id)
    {
        String likedBeforeVar;
        Optional<PostLikeClass> plc = checkLikedBefore(post_id, user_id);   //gets the postlike object
        
        if(plc.orElse(null) == null)
        {
            save(new PostLikeClass(post_id, user_id));  //save the new like
            likedBeforeVar = "";
        }
        else
        {
            if(plc.get().getFlag() == 1)
            {
                plc.get().setFlag(0);   //updates the previous like/unlike
                save(plc.get());
                likedBeforeVar = "liked";
            }
            else
            {
                plc.get().setFlag(1);   //updates the previous like/unlike
                save(plc.get());
                likedBeforeVar = "unliked";
            }
        }
        return likedBeforeVar;
    }
}