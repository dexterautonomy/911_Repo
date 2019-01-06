package com.hingebridge.model;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "messageobject")
public class MessageObject implements Serializable
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "recipient_id")
    private long recipient_id;
    @Column(name = "flag")
    private int flag = 0;
    @Column(name = "postlink")
    private String postlink;
    @Column(name = "unread")
    private String unread = "unread";
    @Column(name = "comment_id")
    private long commentid;
    
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "comment_id", updatable = false, insertable = false, nullable = false)
    private CommentClass commentmessage;
    
    public MessageObject(){}
    
    public MessageObject(long recipient_id, long commentid, String postlink)
    {
        this.recipient_id = recipient_id;
        this.commentid = commentid;
        this.postlink = postlink;
    }

    public long getId()
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

    public void setRecipient_id(long value)
    {
        recipient_id = value;
    }
    
    public long getRecipient_id()
    {
        return recipient_id;
    }
    
    public void setPostlink(String value)
    {
        postlink = value;
    }
    
    public String getPostlink()
    {
        return postlink;
    }
    
    public void setUnread(String value)
    {
        unread = value;
    }
    
    public String getUnread()
    {
        return unread;
    }
    
    public void setCommentid(long value)
    {
        commentid = value;
    }
    
    public long getCommentid()
    {
        return commentid;
    }
    
    public void setCommentmessage(CommentClass value)
    {
        commentmessage = value;
    }
    
    public CommentClass getCommentmessage()
    {
        return commentmessage;
    }
}