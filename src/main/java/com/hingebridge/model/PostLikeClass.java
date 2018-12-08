package com.hingebridge.model;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "postlikeclass")
public class PostLikeClass implements Serializable
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    @Column(name = "postid")
    private Long post_id;
    @Column(name = "userid")
    private Long user_id;
    @Column(name = "flag")
    private int flag = 1;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "postid", insertable = false, updatable = false, nullable = false)
    private PostClass postlikeclass;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userid", insertable = false, updatable = false, nullable = false)
    private UserClass userlikeclass;
    
    public PostLikeClass(){}
    
    public PostLikeClass(Long post_id, Long user_id)
    {
        this.post_id = post_id;
        this.user_id = user_id;
    }
    
    public Long getId()
    {
        return id;
    }
    
    public void setPost_id(Long value)
    {
        this.post_id = value;
    }
    
    public Long getPost_id()
    {
        return post_id;
    }
    
    public void setUser_id(Long value)
    {
        this.user_id = value;
    }
    
    public Long getUser_id()
    {
        return user_id;
    }
    
    public void setFlag(int value)
    {
        this.flag = value;
    }
    
    public int getFlag()
    {
        return flag;
    }
    
    public void setPostlikeclass(PostClass pc)
    {
        this.postlikeclass = pc;
    }
    
    public PostClass getPostlikeclass()
    {
        return postlikeclass;
    }
    
    public void setUserlikeclass(UserClass uc)
    {
        this.userlikeclass = uc;
    }
    
    public UserClass getUserlikeclass()
    {
        return userlikeclass;
    }
}