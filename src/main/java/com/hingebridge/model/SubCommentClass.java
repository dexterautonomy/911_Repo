package com.hingebridge.model;

import com.hingebridge.utility.DurationCalculator;
import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "subcommentclass")
public class SubCommentClass implements Serializable
{
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Column(name="likes")
    private int likes = 0;
    @Column(name="approved")
    private int approved = 1;
    
    @Column(name="content")
    private String content;
    @Column(name="postdate")
    private String postdate;
    
    // UserClass <--- SubCommentClass Relationship
    @Column(name="user_id")
    private Long user_id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", insertable = false, updatable = false, nullable = false)
    private UserClass userthree;
    
    // CommentClass <--- SubCommentClass Relationship
    @Column(name="comment_id")
    private Long comment_id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "comment_id", insertable = false, updatable = false, nullable = false)
    private CommentClass commentclassone;
    
    @Transient
    private String duration;
    
    public SubCommentClass(){}
    
    public SubCommentClass(UserClass uc, CommentClass cc, String content, String postdate)
    {
        this.user_id = uc.getId();
        this.comment_id = cc.getId();
        this.content = content;
        this.postdate = postdate;
    }
    
    public Long getId()
    {
        return id;
    }
    
    public void setLikes(int value)
    {
        this.likes = value;
    }
    
    public int getLikes()
    {
        return likes;
    }
    
    public void setApproved(int value)
    {
        this.approved = value;
    }
    
    public int getApproved()
    {
        return approved;
    }
    
    public void setContent(String value)
    {
        this.content = value;
    }
    
    public String getContent()
    {
        return content;
    }
    
    public void setPostdate(String value)
    {
        this.postdate = value;
    }
    
    public String getPostdate()
    {
        return postdate;
    }    
    
    //FOR NEW CHANGES
    public void setUser_id(Long value)
    {
        this.user_id = value;
    }
    
    public Long getUser_id()
    {
        return user_id;
    }
    
    public void setComment_id(Long value)
    {
        this.comment_id = value;
    }
    
    public Long getComment_id()
    {
        return comment_id;
    }
    
    public void setUserthree(UserClass uc)
    {
        this.userthree = uc;
    }
	
    public UserClass getUserthree()
    {
    	return userthree;
    }
    
    public void setCommentclassone(CommentClass cc)
    {
        this.commentclassone = cc;
    }
	
    public CommentClass getCommentclassone()
    {
    	return commentclassone;
    }
    
    //Calculate duration
    public String getDuration()
    {
        return new DurationCalculator().calculateDuration(postdate);
    }
}