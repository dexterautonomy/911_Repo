package com.hingebridge.model;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "")
public class MessageObject implements Serializable
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "")
    private Long id;
    @Column(name = "")
    private Long sender_id;
    @Column(name = "")
    private Long recipient_id;
    @Column(name = "")
    private int flag = 1;
    @Column(name = "")
    private String postlink;
    @Column(name = "")
    private String textone;
    @Column(name = "")
    private String texttwo;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "", updatable = false, insertable = false, nullable = false)
    private UserClass usermessageobject;
    
    public MessageObject(){}
    
    public MessageObject(Long sender_id, Long recipient_id, String postlink, String textone, String texttwo)
    {
        this.sender_id = sender_id;
        this.recipient_id = recipient_id;
        this.postlink = postlink;
        this.textone = textone;
        this.texttwo = texttwo;
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
    
    /*
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
    */
}
