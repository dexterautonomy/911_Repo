package com.hingebridge.model;

import com.hingebridge.utility.DurationCalculator;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.*;

@Entity
@Table(name = "inboxobject")
public class InboxObject implements Serializable
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "date_sent")
    private String date;
    @Column(name = "deleteadminflag")
    private int deleteAdminFlag = 0;
    @Column(name = "deleteuserflag")
    private int deleteUserFlag = 0;
    @Column(name = "content")
    private String content;
    @Column(name = "admin_read")
    private int adminRead = 0;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", insertable = false, updatable = false, nullable = false)
    private UserClass userInbox;
    
    @OneToMany(mappedBy = "inboxreply")
    private List<ReplyObject> reply;
    
    @Transient
    private String duration;
    
    public InboxObject(){}
    
    public InboxObject(Long userId, String date, String content)
    {
        this.userId = userId;
        this.date = date;
        this.content = content;
    }
    
    public Long getId()
    {
        return id;
    }
    
    public void setUserId(Long userId)
    {
        this.userId = userId;
    }
    
    public Long getUserId()
    {
        return userId;
    }
    
    public void setDate(String value)
    {
        date = value;
    }
    
    public String getDate()
    {
        return date;
    }
    
    public String getDuration()
    {
        return new DurationCalculator().calculateDuration(date);
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
    
    public void setUserInbox(UserClass uc)
    {
        userInbox = uc;
    }
    
    public UserClass getUserInbox()
    {
        return userInbox;
    }
    
    public void setReply(List<ReplyObject> reply)
    {
        this.reply = reply;
    }
	
    public List<ReplyObject> getReply()
    {
        List<ReplyObject> replyObj = new LinkedList<>();
        for(ReplyObject ro : reply)
        {
            if(ro.getDeleteUserFlag() == 0)
            {
                replyObj.add(ro);
            }
        }
        return replyObj;
    }
    
    public void setAdminRead(int value)
    {
        adminRead = value;
    }
    
    public int getAdminRead()
    {
        return adminRead;
    }
}