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
        String likedBeforeVar = null;
        Optional<PostLikeClass> plc = checkLikedBefore(post_id, user_id);
        
        if(plc.get() != null)
        {
            if(plc.get().getFlag() == 1)
            {
                likedBeforeVar = "liked";
            }
            else
            {
                likedBeforeVar = "unliked";
            }
        }
        return likedBeforeVar;
    }
}