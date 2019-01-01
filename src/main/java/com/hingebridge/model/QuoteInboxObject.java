package com.hingebridge.model;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "quoteinboxobject")
public class QuoteInboxObject implements Serializable
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "inbox_id")
    private Long inboxId;
    @Column(name = "reply_id")
    private Long replyId;
    
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "inbox_id", updatable = false, insertable = false, nullable = false)
    private InboxObject inboxquote;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reply_id", updatable = false, insertable = false, nullable = false)
    private ReplyObject replyquote;
    
    public QuoteInboxObject(){}
    
    public QuoteInboxObject(Long inboxId, Long replyId)
    {
        this.inboxId = inboxId;
        this.replyId = replyId;
    }
    
    public Long getId()
    {
        return id;
    }
    
    public void setInboxId(Long value)
    {
        inboxId = value;
    }
    
    public Long getInboxId()
    {
        return inboxId;
    }
    
    public void setReplyId(Long value)
    {
        replyId = value;
    }
    
    public Long getReplyId()
    {
        return replyId;
    }
    
    public void setInboxQuote(InboxObject value)
    {
        inboxquote = value;
    }
    
    public InboxObject getInboxQuote()
    {
        return inboxquote;
    }
    
    public void setReplyquote(ReplyObject value)
    {
        replyquote = value;
    }
    
    public ReplyObject getReplyquote()
    {
        return replyquote;
    }
}