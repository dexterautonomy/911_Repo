package com.hingebridge.model;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "followerobject")
public class FollowerObject implements Serializable
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "user_id")
    private Long user_id;
    @Column(name = "follower_id")
    private Long follower_id;
    @Column(name = "flag")
    private int flag = 1;
    
    public FollowerObject(){}
    
    public FollowerObject(Long user_id, Long follower_id)
    {
        this.user_id = user_id;
        this.follower_id = follower_id;
    }
    
    public Long getId()
    {
        return id;
    }
    
    public void setFlag(int flag)
    {
        this.flag = flag;
    }
    
    public int getFlag()
    {
        return flag;
    }
    
    public void setUser_id(Long value)
    {
        user_id = value;
    }
    
    public Long getUser_id()
    {
        return user_id;
    }
    
    public void setFollower_id(Long value)
    {
        follower_id = value;
    }
    
    public Long getFollower_id()
    {
        return follower_id;
    }
}