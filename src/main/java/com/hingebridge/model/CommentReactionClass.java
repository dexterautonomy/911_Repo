package com.hingebridge.model;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "commentreactionclass")
public class CommentReactionClass implements Serializable
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "comment_id")
    private Long comment_id;
    @Column(name = "user_id")
    private Long user_id;
    @Column(name = "like_flag")
    private int likeflag;
    @Column(name = "red_flag")
    private int redflag;
    @Column(name = "star_flag")
    private int starflag;
    @Column(name = "share_flag")
    private int shareflag;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "comment_id", insertable = false, updatable = false, nullable = false)
    private CommentClass commentreactionobj;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", insertable = false, updatable = false, nullable = false)
    private UserClass usercommentreactobj;
    
    public CommentReactionClass(){}
    
    public CommentReactionClass(Long comment_id, Long user_id, int likeflag, int redflag, int starflag, int shareflag)
    {
        this.comment_id = comment_id;
        this.user_id = user_id;
        this.likeflag = likeflag;
        this.redflag = redflag;
        this.starflag = starflag;
        this.shareflag = shareflag;
    }
    
    public Long getId()
    {
        return id;
    }
    
    public void setComment_id(Long value)
    {
        comment_id = value;
    }
    
    public Long getComment_id()
    {
        return comment_id;
    }
    
    public void setUser_id(Long value)
    {
        user_id = value;
    }
    
    public Long getUser_id()
    {
        return user_id;
    }
    
    public void setLikeflag(int value)
    {
        likeflag = value;
    }
    
    public int getLikeflag()
    {
        return likeflag;
    }
    
    public void setRedflag(int value)
    {
        redflag = value;
    }
    
    public int getRedflag()
    {
        return redflag;
    }
    
    public void setStarflag(int value)
    {
        starflag = value;
    }
    
    public int getStarflag()
    {
        return starflag;
    }
    
    public void setShareflag(int value)
    {
        shareflag = value;
    }
    
    public int getShareflag()
    {
        return shareflag;
    }
    
    public void setCommentreactionobj(CommentClass cc)
    {
        commentreactionobj = cc;
    }
    
    public CommentClass getCommentreactionobj()
    {
        return commentreactionobj;
    }
    
    public void setUsercommentreactobj(UserClass uc)
    {
        usercommentreactobj = uc;
    }
    
    public UserClass getUsercommentreactobj()
    {
        return usercommentreactobj;
    }
}