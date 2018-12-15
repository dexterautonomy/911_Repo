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
    private Long id;
    
    @Column(name="likes")
    private int likes = 0;
    @Column(name="approved")
    private int approved = 1;
    
    @Column(name="content")
    private String content;
    @Column(name="postdate")
    private String postdate;
    
    // UserClass <--- CommentClass Relationship
    @Column(name = "user_id")
    private Long user_id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", insertable = false, updatable = false, nullable = false)
    private UserClass usertwo;
    
    // PostClass <--- CommentClass Relationship
    @Column(name="post_id")
    private Long post_id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "post_id", insertable = false, updatable = false, nullable = false)
    private PostClass postclassone;
    
    // CommentClass ---> SubCommentClass Relationship
    @OneToMany(mappedBy = "commentclassone")
    private List<SubCommentClass> subcomment;
    
    // CommentClass ---> MessageObject Relationship
    @OneToOne(mappedBy = "commentmessage")
    private MessageObject mobj;
    
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
    
    public CommentClass(Long user_id, Long post_id, String content, String postdate)
    {
        this.user_id = user_id;
        this.post_id = post_id;
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
    
    public void setPost_id(Long value)
    {
        this.post_id = value;
    }
    
    public Long getPost_id()
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
}
