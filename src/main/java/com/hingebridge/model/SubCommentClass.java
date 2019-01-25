package com.hingebridge.model;

import com.hingebridge.utility.DurationCalculator;
import java.io.Serializable;
import java.util.List;
import javax.persistence.*;

@Entity
@Table(name = "subcommentclass")
public class SubCommentClass implements Serializable
{
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    
    @Column(name="likes")
    private long likes = 0l;
    @Column(name = "red_flag")
    private long redflag = 0l;
    @Column(name = "star_flag")
    private long star = 0l;
    @Column(name="subcomment_rank")
    private int subcommentrank = 0;
    
    @Column(name="approved")
    private int approved = 1;
    @Column(name="content")
    private String content;
    @Column(name="postdate")
    private String postdate;
    
    @Column(name = "unread")
    private String unread = "unread";
    
    // UserClass <--- SubCommentClass Relationship
    @Column(name="user_id")
    private long user_id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", insertable = false, updatable = false, nullable = false)
    private UserClass userthree;
    
    // CommentClass <--- SubCommentClass Relationship
    @Column(name="comment_id")
    private long comment_id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "comment_id", insertable = false, updatable = false, nullable = false)
    private CommentClass commentclassone;
    
    // SubCommentClass ---> SubCommentReactionClass Relationship
    @OneToMany(mappedBy = "subcommentreactionobj")
    private List<SubCommentReactionClass> subcommentreact;
    
    @Transient
    private String duration;
    
    @Transient
    private SubCommentClass subcommentclass;
    
    public SubCommentClass(){}
    
    public SubCommentClass(UserClass uc, CommentClass cc, String content, String postdate)
    {
        this.user_id = uc.getId();
        this.comment_id = cc.getId();
        this.content = content;
        this.postdate = postdate;
    }
    
    public SubCommentClass(long uc_id, long cc_id, String content, String postdate)
    {
        user_id = uc_id;
        this.comment_id = cc_id;
        this.content = content;
        this.postdate = postdate;
    }
    
    public SubCommentClass(long uc_id, long cc_id, String content, String postdate, String read)    //No need marking your comments as unread, abi no be you write am??
    {
        user_id = uc_id;
        this.comment_id = cc_id;
        this.content = content;
        this.postdate = postdate;
        this.unread = read;
    }
    
    public long getId()
    {
        return id;
    }
    
    public void setLikes(long value)
    {
        this.likes = value;
    }
    
    public long getLikes()
    {
        return likes;
    }
    
    public void setRedflag(long value)
    {
        redflag = value;
    }
    
    public long getRedflag()
    {
        return redflag;
    }
    
    public void setStar(long value)
    {
        star = value;
    }
    
    public long getStar()
    {
        return star;
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
    public void setUser_id(long value)
    {
        this.user_id = value;
    }
    
    public long getUser_id()
    {
        return user_id;
    }
    
    public void setComment_id(long value)
    {
        this.comment_id = value;
    }
    
    public long getComment_id()
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
    
    public void setSubcommentreact(List<SubCommentReactionClass> scrc)
    {
        this.subcommentreact = scrc;
    }
	
    public List<SubCommentReactionClass> getSubcommentreact()
    {
    	return subcommentreact;
    }
    
    //Calculate duration
    public String getDuration()
    {
        return new DurationCalculator().calculateDuration(postdate);
    }
    
    public void setSubcommentrank(int value)
    {
    	subcommentrank = value;
    }
	
    public int getSubcommentrank()
    {
    	return subcommentrank;
    }
    
    public void setUnread(String value)
    {
        unread = value;
    }
    
    public String getUnread()
    {
        return unread;
    }
}