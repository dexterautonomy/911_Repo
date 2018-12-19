package com.hingebridge.repository;

import com.hingebridge.model.SubCommentReactionClass;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SubCommentReactionClassRepo extends JpaRepository<SubCommentReactionClass, Long>
{
    @Query("SELECT scrc FROM SubCommentReactionClass scrc WHERE scrc.subcomment_id = :subcomment_id AND scrc.user_id = :user_id")
    public Optional<SubCommentReactionClass> checkReaction(@Param("subcomment_id")long subcomment_id, @Param("user_id")long user_id);

    public default String likedBefore(long subcomment_id, long user_id)
    {
        String likedBeforeVar;
        Optional<SubCommentReactionClass> scrc = checkReaction(subcomment_id, user_id);   //gets the subcommentreact object
        
        if(scrc.orElse(null) == null)
        {
            save(new SubCommentReactionClass(subcomment_id, user_id, 1, 0, 0));  //save the new like
            likedBeforeVar = "";
        }
        else
        {
            if(scrc.get().getLikeflag() == 1)
            {
                scrc.get().setLikeflag(0);   //updates the previous like/unlike
                save(scrc.get());
                likedBeforeVar = "liked";
            }
            else
            {
                scrc.get().setLikeflag(1);   //updates the previous like/unlike
                save(scrc.get());
                likedBeforeVar = "unliked";
            }
        }
        return likedBeforeVar;
    }
    
    public default String redFlaggedBefore(long subcomment_id, long user_id)
    {
        String redFlaggedBeforeVar;
        Optional<SubCommentReactionClass> scrc = checkReaction(subcomment_id, user_id);   //gets the subcommentreaction object
        
        if(scrc.orElse(null) == null)
        {
            save(new SubCommentReactionClass(subcomment_id, user_id, 0, 1, 0));  //save the new redflag
            redFlaggedBeforeVar = "";
        }
        else
        {
            if(scrc.get().getRedflag()== 1)
            {
                scrc.get().setRedflag(0);   //updates the previous redflag/unredflag
                save(scrc.get());
                redFlaggedBeforeVar = "redflagged";
            }
            else
            {
                scrc.get().setRedflag(1);   //updates the previous redflag/unredflag
                save(scrc.get());
                redFlaggedBeforeVar = "notredflagged";
            }
        }
        return redFlaggedBeforeVar;
    }
    
    public default String starredBefore(long subcomment_id, long user_id)
    {
        String starredBeforeVar;
        Optional<SubCommentReactionClass> scrc = checkReaction(subcomment_id, user_id);   //gets the subcommentreaction object
        
        if(scrc.orElse(null) == null)
        {
            save(new SubCommentReactionClass(subcomment_id, user_id, 0, 0, 1));  //save the new star
            starredBeforeVar = "";
        }
        else
        {
            if(scrc.get().getStarflag()== 1)
            {
                scrc.get().setStarflag(0);   //updates the previous star/unstar
                save(scrc.get());
                starredBeforeVar = "starred";
            }
            else
            {
                scrc.get().setStarflag(1);   //updates the previous star/unstar
                save(scrc.get());
                starredBeforeVar = "notstarred";
            }
        }
        return starredBeforeVar;
    }
}