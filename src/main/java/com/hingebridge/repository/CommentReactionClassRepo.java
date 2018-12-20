package com.hingebridge.repository;

import com.hingebridge.model.CommentReactionClass;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentReactionClassRepo extends JpaRepository<CommentReactionClass, Long>
{
    @Query("SELECT crc FROM CommentReactionClass crc WHERE crc.comment_id = :comment_id AND crc.user_id = :user_id")
    public Optional<CommentReactionClass> checkReaction(@Param("comment_id")long comment_id, @Param("user_id")long user_id);

    public default String likedBefore(long comment_id, long user_id)
    {
        String likedBeforeVar;
        Optional<CommentReactionClass> crc = checkReaction(comment_id, user_id);   //gets the commentreact object
        
        if(crc.orElse(null) == null)
        {
            save(new CommentReactionClass(comment_id, user_id, 1, 0, 0, 0));  //save the new like
            likedBeforeVar = "";
        }
        else
        {
            if(crc.get().getLikeflag() == 1)
            {
                crc.get().setLikeflag(0);   //updates the previous like/unlike
                save(crc.get());
                likedBeforeVar = "liked";
            }
            else
            {
                crc.get().setLikeflag(1);   //updates the previous like/unlike
                save(crc.get());
                likedBeforeVar = "unliked";
            }
        }
        return likedBeforeVar;
    }
    
    public default String redFlaggedBefore(long comment_id, long user_id)
    {
        String redFlaggedBeforeVar;
        Optional<CommentReactionClass> crc = checkReaction(comment_id, user_id);   //gets the commentreaction object
        
        if(crc.orElse(null) == null)
        {
            save(new CommentReactionClass(comment_id, user_id, 0, 1, 0, 0));  //save the new redflag
            redFlaggedBeforeVar = "";
        }
        else
        {
            if(crc.get().getRedflag()== 1)
            {
                crc.get().setRedflag(0);   //updates the previous redflag/unredflag
                save(crc.get());
                redFlaggedBeforeVar = "redflagged";
            }
            else
            {
                crc.get().setRedflag(1);   //updates the previous redflag/unredflag
                save(crc.get());
                redFlaggedBeforeVar = "notredflagged";
            }
        }
        return redFlaggedBeforeVar;
    }
    
    public default String starredBefore(long comment_id, long user_id)
    {
        String starredBeforeVar;
        Optional<CommentReactionClass> crc = checkReaction(comment_id, user_id);   //gets the commentreaction object
        
        if(crc.orElse(null) == null)
        {
            save(new CommentReactionClass(comment_id, user_id, 0, 0, 1, 0));  //save the new star
            starredBeforeVar = "";
        }
        else
        {
            if(crc.get().getStarflag()== 1)
            {
                crc.get().setStarflag(0);   //updates the previous star/unstar
                save(crc.get());
                starredBeforeVar = "starred";
            }
            else
            {
                crc.get().setStarflag(1);   //updates the previous star/unstar
                save(crc.get());
                starredBeforeVar = "notstarred";
            }
        }
        return starredBeforeVar;
    }
    
    public default String sharedBefore(long comment_id, long user_id)
    {
        String sharedBeforeVar;
        Optional<CommentReactionClass> crc = checkReaction(comment_id, user_id);   //gets the commentreaction object
        
        if(crc.orElse(null) == null)
        {
            save(new CommentReactionClass(comment_id, user_id, 0, 0, 0, 1));  //save the new share
            sharedBeforeVar = "";
        }
        else
        {
            if(crc.get().getShareflag()== 1)
            {
                crc.get().setShareflag(0);   //updates the previous share/unshared
                save(crc.get());
                sharedBeforeVar = "shared";
            }
            else
            {
                crc.get().setShareflag(1);   //updates the previous share/unshared
                save(crc.get());
                sharedBeforeVar = "unshared";
            }
        }
        return sharedBeforeVar;
    }
}