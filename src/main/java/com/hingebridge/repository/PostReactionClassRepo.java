package com.hingebridge.repository;

import com.hingebridge.model.PostReactionClass;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostReactionClassRepo extends JpaRepository<PostReactionClass, Long>
{
    @Query("SELECT prc FROM PostReactionClass prc WHERE prc.post_id = :post_id AND prc.user_id = :user_id")
    public Optional<PostReactionClass> checkReaction(@Param("post_id")Long post_id, @Param("user_id")Long user_id);

    public default String likedBefore(Long post_id, Long user_id)
    {
        String likedBeforeVar;
        Optional<PostReactionClass> plc = checkReaction(post_id, user_id);   //gets the postlike object
        
        if(plc.orElse(null) == null)
        {
            save(new PostReactionClass(post_id, user_id, 1, 0, 0));  //save the new like
            likedBeforeVar = "";
        }
        else
        {
            if(plc.get().getLikeflag() == 1)
            {
                plc.get().setLikeflag(0);   //updates the previous like/unlike
                save(plc.get());
                likedBeforeVar = "liked";
            }
            else
            {
                plc.get().setLikeflag(1);   //updates the previous like/unlike
                save(plc.get());
                likedBeforeVar = "unliked";
            }
        }
        return likedBeforeVar;
    }
    
    public default String redFlaggedBefore(Long post_id, Long user_id)
    {
        String redFlaggedBeforeVar;
        Optional<PostReactionClass> plc = checkReaction(post_id, user_id);   //gets the postreaction object
        
        if(plc.orElse(null) == null)
        {
            save(new PostReactionClass(post_id, user_id, 0, 1, 0));  //save the new redflag
            redFlaggedBeforeVar = "";
        }
        else
        {
            if(plc.get().getRedflag()== 1)
            {
                plc.get().setRedflag(0);   //updates the previous redflag/unredflag
                save(plc.get());
                redFlaggedBeforeVar = "redflagged";
            }
            else
            {
                plc.get().setRedflag(1);   //updates the previous redflag/unredflag
                save(plc.get());
                redFlaggedBeforeVar = "notredflagged";
            }
        }
        return redFlaggedBeforeVar;
    }
    
    public default String starredBefore(Long post_id, Long user_id)
    {
        String starredBeforeVar;
        Optional<PostReactionClass> plc = checkReaction(post_id, user_id);   //gets the postreaction object
        
        if(plc.orElse(null) == null)
        {
            save(new PostReactionClass(post_id, user_id, 0, 0, 1));  //save the new star
            starredBeforeVar = "";
        }
        else
        {
            if(plc.get().getStarflag()== 1)
            {
                plc.get().setStarflag(0);   //updates the previous star/unstar
                save(plc.get());
                starredBeforeVar = "starred";
            }
            else
            {
                plc.get().setStarflag(1);   //updates the previous star/unstar
                save(plc.get());
                starredBeforeVar = "notstarred";
            }
        }
        return starredBeforeVar;
    }
}