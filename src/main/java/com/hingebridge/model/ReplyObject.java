package com.hingebridge.model;

import com.hingebridge.utility.DurationCalculator;
import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "replyobject")
public class ReplyObject implements Serializable
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "inbox_id")
    private long inboxId;
    @Column(name = "date_sent")
    private String date;
    @Column(name = "deleteuserflag")
    private int deleteUserFlag = 0;
    @Column(name = "deleteadminflag")
    private int deleteAdminFlag = 0;
    @Column(name = "content")
    private String content;
    @Column(name = "user_read")
    private int userRead = 0;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "inbox_id", insertable = false, updatable = false, nullable = false)
    private InboxObject inboxreply;
    
    @Transient
    private String duration;
    
    public ReplyObject(){}
    
    public ReplyObject(long inboxId, String date, String content)
    {
        this.inboxId = inboxId;
        this.date = date;
        this.content = content;
    }
    
    public long getId()
    {
        return id;
    }
    
    public String getDuration()
    {
        return new DurationCalculator().calculateDuration(date);
    }
    
    public void setInboxId(long value)
    {
        this.inboxId = value;
    }
    
    public long getInboxId()
    {
        return inboxId;
    }
    
    public void setDate(String value)
    {
        date = value;
    }
    
    public String getDate()
    {
        return date;
    }
    
    public void setDeleteUserFlag(int value)
    {
        deleteUserFlag = value;
    }
    
    public int getDeleteUserFlag()
    {
        return deleteUserFlag;
    }
    
    public void setDeleteAdminFlag(int value)
    {
        deleteAdminFlag = value;
    }
    
    public int getDeleteAdminFlag()
    {
        return deleteAdminFlag;
    }
    
    public void setContent(String content)
    {
        this.content = content;
    }
    
    public String getContent()
    {
        return content;
    }
    
    public void setInboxreply(InboxObject io)
    {
        inboxreply = io;
    }
    
    public InboxObject getInboxreply()
    {
        return inboxreply;
    }
    
    public void setUserRead(int value)
    {
        userRead = value;
    }
    
    public int getUserRead()
    {
        return userRead;
    }
}