package com.hingebridge.model;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table (name = "followedpostdeleteobject")
public class FollowedPostDeleteObject implements Serializable
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "post_id")
    private long postId;
    @Column(name = "user_id")
    private long userId;
    @Column(name = "flagdelete")
    private int flagDelete = 1;
    @Column(name = "flagread")
    private int flagRead = 1;
    
    
    public FollowedPostDeleteObject(){}
    
    public FollowedPostDeleteObject(long postId, long userId)   //Constructor for deleting followed post
    {
        this.postId = postId;
        this.userId = userId;
    }
    
    public FollowedPostDeleteObject(long postId, long userId, int flagRead)   //Constructor for marking followed post as read
    {
        this.postId = postId;
        this.userId = userId;
        this.flagDelete = 0;
        this.flagRead = flagRead;
    }
    
    
    public long getId()
    {
        return id;
    }
    
    public void setPostId(long postId)
    {
        this.postId = postId;
    }
    
    public long getPostId()
    {
        return postId;
    }
    
    public void setUserId(long userId)
    {
        this.userId = userId;
    }
    
    public long getUserId()
    {
        return userId;
    }
    
    public void setFlagDelete(int flag)
    {
        this.flagDelete = flag;
    }
    
    public long getFlagDelete()
    {
        return flagDelete;
    }
    
    public void setFlagRead(int flag)
    {
        this.flagRead = flag;
    }
    
    public long getFlagRead()
    {
        return flagRead;
    }
}