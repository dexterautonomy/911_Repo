package com.hingebridge.model;

import com.hingebridge.utility.DurationCalculator;
import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "quoteobject")
public class QuoteObject implements Serializable
{
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    
    @Column(name="user_id")
    private long user_id;
    
    @Column(name="comment_id")
    private long comment_id;
    
    @Column(name="content")
    private String content;
    @Column(name="quote_date")
    private String quotedate;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", insertable = false, updatable = false, nullable = false)
    private UserClass userquoteobj;
    
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "comment_id", insertable = false, updatable = false, nullable = false)
    private CommentClass commentquoteobj;
    
    
    @Transient
    private String duration;
    
    public QuoteObject(){}
    
    public QuoteObject(long user_id, long comment_id, String content, String quotedate)
    {
        this.user_id = user_id;
        this.comment_id = comment_id;
        this.content = content;
        this.quotedate = quotedate;
    }
    
    public long getId()
    {
        return id;
    }
    
    public void setUser_id(long user_id)
    {
        this.user_id = user_id;
    }
    
    public long getUser_id()
    {
        return user_id;
    }
    
    public void setComment_id(long comment_id)
    {
        this.comment_id = comment_id;
    }
    
    public long getComment_id()
    {
        return comment_id;
    }
    
    public void setContent(String content)
    {
        this.content = content;
    }
    
    public String getContent()
    {
        return content;
    }
    
    public void setQuotedate(String quotedate)
    {
        this.quotedate = quotedate;
    }
    
    public String getQuotedate()
    {
        return quotedate;
    }
    
    public void setUserquoteobj(UserClass uc)
    {
        userquoteobj = uc;
    }
    
    public UserClass getUserquoteobj()
    {
        return userquoteobj;
    }
    
    public void setCommentquoteobj(CommentClass cc)
    {
        commentquoteobj = cc;
    }
    
    public CommentClass getCommentquoteobj()
    {
        return commentquoteobj;
    }
    
    public String getDuration()
    {
        return new DurationCalculator().calculateDuration(quotedate);
    }
}