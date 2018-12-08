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
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "likes")
    private Long likes = 0L;
    
    @Column(name = "views")
    private Long views = 0L;
    
    @Column(name = "approved")
    private int approved = 0;
    
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
    
    //@Column(name = "username")
    //private String username;
    
    @Column(name = "coverimage")
    private String coverImage;
     
    @Transient
    private String actionButton;
    @Transient
    private MultipartFile contentFile;
    @Transient 
    private MultipartFile coverFile;
    
    // UserClass <--- PostClass Relationship
    @Column(name = "user_id")
    private Long user_id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", insertable = false, updatable = false, nullable = false)
    private UserClass userone;
    
    // PostClass ---> CommentClass Relationship
    @OneToMany(mappedBy = "postclassone", fetch = FetchType.EAGER)
    private List<CommentClass> comments;
    
    public PostClass(){}
    
    public PostClass(UserClass uc, String title, String content, String category, String coverImage, String date)
    {
        this.user_id = uc.getId();
        this.title = title;
        this.content = content;
        this.category = category;
        this.coverImage = coverImage;
        this.date = date;
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
    
    public void setCategory(String value)
    {
        this.category = value;
    }
    
    public String getCategory()
    {
        return category;
    }
    
    /*
    public void setUsername(String value)
    {
        this.username = value;
    }
    
    public String getUsername()
    {
        return username;
    }
    */
    
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
}