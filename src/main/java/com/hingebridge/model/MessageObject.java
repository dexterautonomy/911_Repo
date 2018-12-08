package com.hingebridge.model;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "messageobject")
public class MessageObject implements Serializable
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    @Column(name = "post_id")
    private Long post_id;
    @Column(name = "user_id")
    private Long user_id;
    @Column(name = "flag")
    private int flag = 1;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "post_id", updatable = false, insertable = false, nullable = false)
    private PostClass postmessageobject;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", updatable = false, insertable = false, nullable = false)
    private UserClass usermessageobject;
    
    public MessageObject(){}
    
    public MessageObject(Long post_id, Long user_id)
    {
        this.post_id = post_id;
        this.user_id = user_id;
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
    
    public void setPostmessageobject(PostClass pc)
    {
        postmessageobject = pc;
    }
    
    public PostClass getPostmessageobject()
    {
        return postmessageobject;
    }
    
    public void setUsermessageobject(UserClass uc)
    {
        usermessageobject = uc;
    }
    
    public UserClass getUsermessageobject()
    {
        return usermessageobject;
    }
}