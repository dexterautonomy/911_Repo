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
    //@Column(name = "sender_id")
    //private Long sender_id;
    @Column(name = "recipient_id")
    private Long recipient_id;
    @Column(name = "flag")
    private int flag = 1;
    @Column(name = "postlink")
    private String postlink;
    @Column(name = "unread")
    private String unread = "unread";
    
    /*
    @Column(name = "textone")
    private String textone;
    @Column(name = "texttwo")
    private String texttwo;
    */
    
    @Column(name = "comment_id")
    private Long comment_id;
    
    
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "comment_id", updatable = false, insertable = false, nullable = false)
    private CommentClass commentmessage;
    
    
    /*
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sender_id", updatable = false, insertable = false, nullable = false)
    private UserClass sender;
    */
    
    public MessageObject(){}
    
    
    /*
    public MessageObject(Long sender_id, Long recipient_id, String postlink, String textone, String texttwo)
    {
        this.sender_id = sender_id;
        this.recipient_id = recipient_id;
        this.postlink = postlink;
        this.textone = textone;
        this.texttwo = texttwo;
    }
    */
    
    public MessageObject(Long recipient_id, Long comment_id)
    {
        this.recipient_id = recipient_id;
        this.comment_id = comment_id;
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
    public void setSender_id(Long value)
    {
        sender_id = value;
    }
    
    public Long getSender_id()
    {
        return sender_id;
    }
    */

    public void setRecipient_id(Long value)
    {
        recipient_id = value;
    }
    
    public Long getRecipient_id()
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
    /*
    public void setTextone(String value)
    {
        textone = value;
    }
    
    public String getTextone()
    {
        return textone;
    }
    
    public void setTexttwo(String value)
    {
        texttwo = value;
    }
    
    public String getTexttwo()
    {
        return texttwo;
    }
    
    public void setSender(UserClass uc)
    {
        sender = uc;
    }
    
    public UserClass getSender()
    {
        return sender;
    }
    */
    
    public void setComment_id(Long value)
    {
        comment_id = value;
    }
    
    public Long getComment_id()
    {
        return comment_id;
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