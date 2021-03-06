package com.hingebridge.model;

import com.hingebridge.utility.DurationCalculator;
import java.io.Serializable;
import java.util.List;
import javax.persistence.*;
import org.springframework.web.multipart.MultipartFile;

@Entity
@Table(name = "postclass")
public class PostClass implements Serializable
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;    
    @Column(name = "likes")
    private Long likes = 0l;
    
    @Column(name = "red_flag")
    private long redflag = 0l;
    @Column(name = "star_flag")
    private long star = 0l;
    
    @Column(name = "views")
    private Long views = 0l;    
    @Column(name = "approved")
    private int approved = 0;    
    @Column(name = "post_rank")
    private Long postrank;
    
    // Add flag column so that the owners can delete the post from their trend list
    @Column(name = "flag")
    private int flag = 1;
    
    @Column(name = "postdate")
    private String date;
    @Column(name = "title")
    private String title;
    @Column(name = "content")
    private String content;
    @Column(name = "category")
    private String category = "mypost";
    
    @Transient
    private String duration;
    
    @Column(name = "coverimage")
    private String coverImage;
    
    @Transient
    private String content_2;
    @Transient
    private String actionButton;
    @Transient
    private MultipartFile contentFile;
    @Transient 
    private MultipartFile coverFile;
    @Transient
    private String rank;
    
    // UserClass <--- PostClass Relationship
    @Column(name = "user_id")
    private Long user_id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", insertable = false, updatable = false, nullable = false)
    private UserClass userone;
    
    // PostClass ---> CommentClass Relationship
    @OneToMany(mappedBy = "postclassone", fetch = FetchType.EAGER)
    private List<CommentClass> comments;
    
    // PostClass ---> PostReactionClass Relationship
    @OneToMany(mappedBy = "postlikeclass")
    private List<PostReactionClass> plc;
    
    public PostClass(){}
    
    //Constructor for ranked post
    public PostClass(UserClass uc, String title, String content, String category, String coverImage, String date)
    {
        this.user_id = uc.getId();
        this.title = title;
        this.content = content;
        this.category = category;
        this.coverImage = coverImage;
        this.date = date;
        this.postrank = uc.getUserrank();
    }
    
    //Constructor for unranked post
    public PostClass(UserClass uc, Long postrank, String title, String content, String category, String coverImage, String date)
    {
        this.user_id = uc.getId();
        this.title = title;
        this.content = content;
        this.category = category;
        this.coverImage = coverImage;
        this.date = date;
        this.postrank = postrank;
    }
    
    public Long getId()
    {
        return id;
    }
    
    public void setLikes(Long value)
    {
        this.likes = value;
    }
    
    public Long getLikes()
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
    
    public void setViews(Long value)
    {
        this.views = value;
    }
    
    public Long getViews()
    {
        return views;
    }
    
    public void setApproved(int value)
    {
        this.approved = value;
    }
    
    public int getApproved()
    {
        return approved;
    }
    
    public void setPostrank(Long value)
    {
        this.postrank = value;
    }
    
    public Long getPostrank()
    {
        return postrank;
    }
    
    public void setTitle(String value)
    {
        this.title = value;
    }
    
    public String getTitle()
    {
        return title;
    }
    
    public void setContent(String value)
    {
        this.content = value;
    }
    
    public String getContent()
    {
        return content;
    }
    
    public void setContent_2(String value)
    {
        this.content_2 = value;
    }
    
    public String getContent_2()
    {
        return content_2;
    }
    
    public void setCategory(String value)
    {
        this.category = value;
    }
    
    public String getCategory()
    {
        return category;
    }
    
    public void setCoverImage(String value)
    {
        this.coverImage = value;
    }
    
    public String getCoverImage()
    {
        return coverImage;
    }
    
    public void setDate(String value)
    {
        this.date = value;
    }
    
    public String getDate()
    {
        return date;
    }
    
    public void setActionButton(String value)
    {
        this.actionButton = value;
    }
    
    public String getActionButton()
    {
        return actionButton;
    }
    
    public void setContentFile(MultipartFile value)
    {
        this.contentFile = value;
    }
    
    public MultipartFile getContentFile()
    {
        return contentFile;
    }
    
    public void setCoverFile(MultipartFile value)
    {
        this.coverFile = value;
    }
    
    public MultipartFile getCoverFile()
    {
        return coverFile;
    }
    
    public void setRank(String value)
    {
        this.rank = value;
    }
    
    public String getRank()
    {
        return rank;
    }
    
    // FOR NEW CHANGES
    public void setUser_id(Long value)
    {
        this.user_id = value;
    }
    
    public Long getUser_id()
    {
        return user_id;
    }
    
    public void setUserone(UserClass uc)
    {
        this.userone = uc;
    }
	
    public UserClass getUserone()
    {
    	return userone;
    }
    
    public void setComments(List<CommentClass> cc)
    {
        this.comments = cc;
    }
	
    public List<CommentClass> getComments()
    {
    	return comments;
    }
    
    //Calculate duration
    public String getDuration()
    {
        return new DurationCalculator().calculateDuration(date);
    }
    
    public void setPlc(List<PostReactionClass> plc)
    {
        this.plc = plc;
    }
	
    public List<PostReactionClass> getPlc()
    {
    	return plc;
    }
    
    // For deletion from trend list
    public void setFlag(int value)
    {
        this.flag = value;
    }
    
    public int getFlag()
    {
        return flag;
    }
}