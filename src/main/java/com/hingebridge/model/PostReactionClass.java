package com.hingebridge.model;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "postreactionclass")
public class PostReactionClass implements Serializable
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "postid")
    private Long post_id;
    @Column(name = "userid")
    private Long user_id;
    @Column(name = "like_flag")
    private int likeflag;
    @Column(name = "red_flag")
    private int redflag;
    @Column(name = "star_flag")
    private int starflag;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "postid", insertable = false, updatable = false, nullable = false)
    private PostClass postlikeclass;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userid", insertable = false, updatable = false, nullable = false)
    private UserClass userlikeclass;
    
    public PostReactionClass(){}
    
    public PostReactionClass(Long post_id, Long user_id, int likeflag, int redflag, int starflag)
    {
        this.post_id = post_id;
        this.user_id = user_id;
        this.likeflag = likeflag;
        this.redflag = redflag;
        this.starflag = starflag;
    }
    
    public Long getId()
    {
        return id;
    }
    
    public void setPost_id(Long value)
    {
        post_id = value;
    }
    
    public Long getPost_id()
    {
        return post_id;
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
    
    
    public void setPostlikeclass(PostClass pc)
    {
        postlikeclass = pc;
    }
    
    public PostClass getPostlikeclass()
    {
        return postlikeclass;
    }
    
    public void setUserlikeclass(UserClass uc)
    {
        userlikeclass = uc;
    }
    
    public UserClass getUserlikeclass()
    {
        return userlikeclass;
    }
}