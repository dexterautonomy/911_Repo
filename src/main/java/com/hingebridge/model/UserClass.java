package com.hingebridge.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.*;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Entity
@Table(name="userclass")
public class UserClass implements Serializable
{
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;
    @Column(name="myposts")
    private Long myposts = 0L;
    @Column(name="adscredit")
    private Long adscredit = 0L;

    @Column(name="cpm")
    private int cpm = 0;
    @Column(name="cpc")
    private int cpc = 0;
    @Column(name="appban")
    private int appban = 0;
    @Column(name="postban")
    private int postban = 0;
    @Column(name="commentban")
    private int commentban = 0;
    @Column(name="active")
    private int active = 1;
    
    @Column(name="yellow_star")
    private Long yellowstar = 0L;
    @Column(name="red_flag")
    private Long redflag = 0L;
    @Column(name="blue_share")
    private Long blueshare = 0L;
    @Column(name="green_like")
    private Long greenlike = 0L;
    @Column(name="black_view")
    private Long blackview = 0L;
    @Column(name="user_rank")
    private Long userrank = 0L;
    @Column(name="followers")
    private Long followers = 0L;
    @Column(name="color_class")
    private String colorclass = "user_beginner";
	
    @Column(name="pix")
    private String pix = "empty.png";
    @Column(name="email")
    private String email;
    @Column(name="gender")
    private String gender;
    @Column(name="username")
    private String username;
    @Column(name="password")
    private String password;
    @Column(name="confirmemail")
    private String confirmemail;

    // UserClass ---> Role Relationship
    @OneToOne(targetEntity = Role.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "userrole", joinColumns = {@JoinColumn(name = "userid")}, inverseJoinColumns = {@JoinColumn(name = "roleid")})
    private Role role;
    
    // UserClass ---> PostClass Relationship
    @OneToMany(mappedBy = "userone")
    private List<PostClass> postClass;
    
    // UserClass ---> CommentClass Relationship
    @OneToMany(mappedBy = "usertwo")
    private List<CommentClass> comments;
    
    // UserClass ---> SubCommentClass Relationship
    @OneToMany(mappedBy = "userthree")
    private List<SubCommentClass> quotes;
    
    // UserClass ---> PostLikeClass Relationship
    @OneToMany(mappedBy = "userlikeclass")
    private List<PostLikeClass> plc;
    
    public UserClass(){}
	
    public UserClass(String email, String gender, String username, String password, String confirmemail)
    {
    	this.email = email;
	this.gender = gender;
	this.username = username;
        this.password = password;
	this.confirmemail = confirmemail;
    }
	
    public Long getId()
    {
    	return id;
    }
	
    public void setMypost(Long value)
    {
    	this.myposts = value;
    }
	
    public Long getMypost()
    {
    	return myposts;
    }
	
    public void setAdscredit(Long value)
    {
    	this.adscredit = value;
    }
	
    public Long getAdscredit()
    {
    	return adscredit;
    }
	
    public void setCpm(int value)
    {
	this.cpm = value;
    }
	
    public int getCpm()
    {
        return cpm;
    }
	
    public void setCpc(int value)
    {
    	this.cpc = value;
    }
	
    public int getCpc()
    {
    	return cpc;
    }
	
    public void setAppban(int value)
    {
    	this.appban = value;
    }
	
    public int getAppban()
    {
    	return appban;
    }
	
    public void setPostban(int value)
    {
    	this.postban = value;
    }
	
    public int getPostban()
    {
    	return postban;
    }
	
    public void setCommentban(int value)
    {
    	this.commentban = value;
    }
	
    public int getCommentban()
    {
    	return commentban;
    }
	
    public void setActive(int value)
    {
    	this.active = value;
    }
	
    public int getActive()
    {
    	return active;
    }
	
    public void setPix(String value)
    {
    	this.pix = value;
    }
	
    public String getPix()
    {
    	return pix;
    }
	
    public void setEmail(String value)
    {
    	this.email = value;
    }
	
    public String getEmail()
    {
    	return email;
    }
	
    public void setGender(String value)
    {
    	this.gender = value;
    }
	
    public String getGender()
    {
    	return gender;
    }
	
    public void setUsername(String value)
    {
    	this.username = value;
    }
	
    public String getUsername()
    {
    	return username;
    }
	
    public void setPassword(String value)
    {
    	this.password = new BCryptPasswordEncoder().encode(value);
        
    }
	
    public String getPassword()
    {
    	return password;
    }

    public void setConfirmemail(String value)
    {
    	this.confirmemail = new BCryptPasswordEncoder().encode(value);
    }
	
    public String getConfirmemail()
    {
    	return confirmemail;
    }
	
    // RELATIONSHIP METHODS
    public void setRole(Role role)
    {
        this.role = role;
    }
	
    public Role getRole()
    {
    	return role;
    }
        
    public void setPostClass(List<PostClass> pc)
    {
        this.postClass = pc;
    }
	
    public List<PostClass> getPostClass()
    {
    	return postClass;
    }
    
    public void setComments(List<CommentClass> cc)
    {
        this.comments = cc;
    }
	
    public List<CommentClass> getComments()
    {
    	return comments;
    }
    
    public void setQuotes(List<SubCommentClass> qc)
    {
        this.quotes = qc;
    }
	
    public List<SubCommentClass> getQuotes()
    {
    	return quotes;
    }
    
    public void setPlc(List<PostLikeClass> plc)
    {
        this.plc = plc;
    }
	
    public List<PostLikeClass> getPlc()
    {
    	return plc;
    }
    
    public void setYellowstar(Long value)
    {
    	yellowstar = value;
    }
	
    public Long getYellowstar()
    {
    	return yellowstar;
    }
    
    public void setRedflag(Long value)
    {
    	redflag = value;
    }
	
    public Long getRedflag()
    {
    	return redflag;
    }
    
    public void setBlueshare(Long value)
    {
    	blueshare = value;
    }
	
    public Long getBlueshare()
    {
    	return blueshare;
    }
    
    public void setGreenlike(Long value)
    {
    	greenlike = value;
    }
	
    public Long getGreenlike()
    {
    	return greenlike;
    }
    
    public void setBlackview(Long value)
    {
    	blackview = value;
    }
	
    public Long getBlackview()
    {
    	return blackview;
    }
    
    public void setUserrank(Long value)
    {
    	userrank = value;
    }
	
    public Long getUserrank()
    {
    	return userrank;
    }
    
    public void setFollowers(Long value)
    {
    	followers = value;
    }
	
    public Long getFollowers()
    {
    	return followers;
    }
    
    public void setColorclass(String value)
    {
    	colorclass = value;
    }
	
    public String getColorclass()
    {
    	return colorclass;
    }
    
}