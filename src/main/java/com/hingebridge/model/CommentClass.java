package com.hingebridge.model;

import com.hingebridge.utility.DurationCalculator;
import java.io.Serializable;
import java.util.List;
import javax.persistence.*;

@Entity
@Table(name = "commentclass")
public class CommentClass implements Serializable
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
    @Column(name = "share_flag")
    private long share = 0l;
    
    @Column(name="approved")
    private int approved = 1;
    
    @Column(name="content")
    private String content;
    @Column(name="postdate")
    private String postdate;
    
    // UserClass <--- CommentClass Relationship
    @Column(name = "user_id")
    private long user_id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", insertable = false, updatable = false, nullable = false)
    private UserClass usertwo;
    
    // PostClass <--- CommentClass Relationship
    @Column(name="post_id")
    private long post_id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "post_id", insertable = false, updatable = false, nullable = false)
    private PostClass postclassone;
    
    // CommentClass ---> SubCommentClass Relationship
    @OneToMany(mappedBy = "commentclassone")
    private List<SubCommentClass> subcomment;
    
    // CommentClass ---> MessageObject Relationship
    @OneToOne(mappedBy = "commentmessage")
    private MessageObject mobj;
    
    // CommentClass ---> QuoteObject Relationship
    @OneToOne(mappedBy = "commentquoteobj")
    private QuoteObject quoteobj;
    
    // CommentClass ---> CommentReactionClass Relationship
    @OneToMany(mappedBy = "commentreactionobj")
    private List<CommentReactionClass> commentreact;
    
    @Transient
    private String duration;
    
    public CommentClass(){}
    
    public CommentClass(UserClass uc, PostClass pc, String content, String postdate)
    {
        this.user_id = uc.getId();
        this.post_id = pc.getId();
        this.content = content;
        this.postdate = postdate;
    }
    
    public CommentClass(long user_id, long post_id, String content, String postdate)
    {
        this.user_id = user_id;
        this.post_id = post_id;
        this.content = content;
        this.postdate = postdate;
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
    
    public void setShare(long value)
    {
        share = value;
    }
    
    public long getShare()
    {
        return share;
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
    
    public void setPost_id(long value)
    {
        this.post_id = value;
    }
    
    public long getPost_id()
    {
        return post_id;
    }
    
    public void setUsertwo(UserClass uc)
    {
        this.usertwo = uc;
    }
	
    public UserClass getUsertwo()
    {
    	return usertwo;
    }
    
    public void setPostclassone(PostClass pc)
    {
        this.postclassone = pc;
    }
	
    public PostClass getPostclassone()
    {
    	return postclassone;
    }
    
    public void setSubcomment(List<SubCommentClass> sub)
    {
        this.subcomment = sub;
    }
	
    public List<SubCommentClass> getSubcomment()
    {
    	return subcomment;
    }
    
    //Calculate duration
    public String getDuration()
    {
        return new DurationCalculator().calculateDuration(postdate);
    }
    
    public void setMobj(MessageObject mobj)
    {
        this.mobj = mobj;
    }
	
    public MessageObject getMobj()
    {
    	return mobj;
    }
    
    public void setQuoteobj(QuoteObject qobj)
    {
        quoteobj = qobj;
    }
    
    public QuoteObject getQuoteobj()
    {
        return quoteobj;
    }
    
    public void setCommentreact(List<CommentReactionClass> crc)
    {
        this.commentreact = crc;
    }
	
    public List<CommentReactionClass> getCommentreact()
    {
    	return commentreact;
    }
}