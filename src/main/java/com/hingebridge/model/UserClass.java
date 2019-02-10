package com.hingebridge.model;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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
    private Long myposts = 0l;
    @Column(name="adscredit")
    private Long adscredit = 0l;

    @Column(name="cpm")
    private int cpm = 0;
    @Column(name="cpc")
    private int cpc = 0;
    //@Column(name="appban")
    //private int appban = 0;
    @Column(name="postban")
    private int postban = 0;
    @Column(name="commentban")
    private int commentban = 0;
    //@Column(name="active")
    //private int active = 1;
    
    @Column(name="yellow_star")
    private Long yellowstar = 0l;
    @Column(name="red_flag")
    private Long redflag = 0l;
    @Column(name="blue_share")
    private Long blueshare = 0l;
    @Column(name="green_like")
    private Long greenlike = 0l;
    @Column(name="black_view")
    private Long blackview = 0l;
    @Column(name="user_rank")
    private Long userrank = 0l;
    @Column(name="followers")
    private Long followers = 0l;
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

    // UserClass ---> Role Relationship  //I should have made Role to map the relationship o! SO that 
    // instead of using a third table and the @JoinTable annotation, it would have been just 2 tables and the
    // @JoinColumn on thr Role side>>>>>>>>> But its ok
    @OneToMany(targetEntity = Role.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "userrole", joinColumns = {@JoinColumn(name = "userid")}, inverseJoinColumns = {@JoinColumn(name = "roleid")})
    private Set<Role> role;
    
    // UserClass ---> PostClass Relationship
    @OneToMany(mappedBy = "userone")
    private List<PostClass> postClass;
    
    // UserClass ---> CommentClass Relationship
    @OneToMany(mappedBy = "usertwo")
    private List<CommentClass> comments;
    
    // UserClass ---> SubCommentClass Relationship
    @OneToMany(mappedBy = "userthree")
    private List<SubCommentClass> quotes;
    
    // UserClass ---> PostReactionClass Relationship
    @OneToMany(mappedBy = "userlikeclass")
    private List<PostReactionClass> plc;
    
    // UserClass ---> QuoteObject Relationship
    @OneToMany(mappedBy = "userquoteobj")
    private List<QuoteObject> quoteobj;
    
    // UserClass ---> CommentReactionClass Relationship
    @OneToMany(mappedBy = "usercommentreactobj")
    private List<CommentReactionClass> commentreact;
    
    // UserClass ---> SubCommentReactionClass Relationship
    @OneToMany(mappedBy = "usersubcommentreactobj")
    private List<SubCommentReactionClass> subcommentreact;
    
    // UserClass ---> AdvertObject Relationship
    @OneToMany(mappedBy = "userAdvert")
    private List<AdvertObject> advertObject;
    
    // UserClass ---> InboxObject Relationship
    @OneToMany(mappedBy = "userInbox")
    private List<InboxObject> inboxObject;
    
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
	
    public void setMyposts(Long value)
    {
    	this.myposts = value;
    }
	
    public Long getMyposts()
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
	
    /*
    public void setAppban(int value)
    {
    	this.appban = value;
    }
	
    public int getAppban()
    {
    	return appban;
    }
    */
    
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
	
    /*
    public void setActive(int value)
    {
    	this.active = value;
    }
	
    public int getActive()
    {
    	return active;
    }
    */
    
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
	
    /*
    public void setPassword(String value)   //setters are associated with form backing object not constructors
    {
    	this.password = new BCryptPasswordEncoder().encode(value);
    }
    */
    
    public void setPassword(String value)
    {
    	this.password = value;
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
    public void setRole(Set<Role> role)
    {
        this.role = role;
    }
	
    public Set<Role> getRole()
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
    
    public void setPlc(List<PostReactionClass> plc)
    {
        this.plc = plc;
    }
	
    public List<PostReactionClass> getPlc()
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
    
    public void setQuoteobj(List<QuoteObject> qobj)
    {
        quoteobj = qobj;
    }
    
    public List<QuoteObject> getQuoteobj()
    {
        return quoteobj;
    }
    
    public void setCommentreact(List<CommentReactionClass> crc)
    {
        commentreact = crc;
    }
	
    public List<CommentReactionClass> getCommentreact()
    {
    	return commentreact;
    }
    
    public void setSubcommentreact(List<SubCommentReactionClass> scrc)
    {
        subcommentreact = scrc;
    }
	
    public List<SubCommentReactionClass> getSubcommentreact()
    {
    	return subcommentreact;
    }
    
    public void setAdvertObject(List<AdvertObject> adObj)
    {
        advertObject = adObj;
    }
	
    public List<AdvertObject> getAdvertObject()
    {
    	//return advertObject;
        List<AdvertObject> unExpiredAdObjList = new LinkedList<>();
        for(AdvertObject ao : advertObject)
        {
            if(ao.getExpired() == 0)
            {
                unExpiredAdObjList.add(ao);
            }
        }
        return unExpiredAdObjList;
    }
    
    public void setInboxObject(List<InboxObject> inboxObject)
    {
        this.inboxObject = inboxObject;
    }
	
    public List<InboxObject> getInboxObject()
    {
        List<InboxObject> inboxObj = new LinkedList<>();
        
        //Reverse order
        for(int count = inboxObject.size() - 1; count >= 0;  count--)
        {
            if(inboxObject.get(count).getDeleteUserFlag() == 0)
            {
                inboxObj.add(inboxObject.get(count));
            }
        }
        return inboxObj;
    }
}