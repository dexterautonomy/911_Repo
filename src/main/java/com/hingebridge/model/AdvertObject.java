package com.hingebridge.model;

import java.io.Serializable;
import javax.persistence.*;
import org.springframework.web.multipart.MultipartFile;

@Entity
@Table(name = "advertobject")
public class AdvertObject implements Serializable
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "approve")
    private int approve = 0;
    @Column(name = "payoption")
    private String payOption = "CPM";
    @Column(name = "startdate")
    private String startDate;
    @Column(name = "clicks")
    private Long clicks = 0l;
    @Column(name = "views")
    private Long views = 0l;
    @Column(name = "adsimage")
    private String adsImage;
    @Column(name = "landingpage")
    private String landingPage;
    @Column(name = "pause")
    private int pause = 0;
    @Column(name = "expired")
    private int expired = 0;
    
    @Transient
    private String actionButton;
    
    @Transient
    private MultipartFile file;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", insertable = false, updatable = false, nullable = false)
    private UserClass userAdvert;
    
    public AdvertObject(){}
    
    public AdvertObject(Long userId, String payOption, String startDate, String adsImage, String landingPage)
    {
        this.userId = userId;
        this.payOption = payOption;
        this.startDate = startDate;
        this.adsImage = adsImage;
        this.landingPage = landingPage;
    }
    
    public Long getId()
    {
        return id;
    }
    
    public void setUserId(Long userId)
    {
        this.userId = userId;
    }
    
    public Long getUserId()
    {
        return userId;
    }
    
    public void setApprove(int approve)
    {
        this.approve = approve;
    }
    
    public int getApprove()
    {
        return approve;
    }
    
    public void setPayOption(String payOption)
    {
        this.payOption = payOption;
    }
    
    public String getPayOption()
    {
        return payOption;
    }
    
    public void setStartDate(String startDate)
    {
        this.startDate = startDate;
    }
    
    public String getStartDate()
    {
        return startDate;
    }
    
    public void setClicks(Long clicks)
    {
        this.clicks = clicks;
    }
    
    public Long getClicks()
    {
        return clicks;
    }
    
    public void setViews(Long views)
    {
        this.views = views;
    }
    
    public Long getViews()
    {
        return views;
    }
    
    public void setAdsImage(String adsImage)
    {
        this.adsImage = adsImage;
    }
    
    public String getAdsImage()
    {
        return adsImage;
    }
    
    public void setLandingPage(String landingPage)
    {
        this.landingPage = landingPage;
    }
    
    public String getLandingPage()
    {
        return landingPage;
    }
    
    public void setPause(int pause)
    {
        this.pause = pause;
    }
    
    public int getPause()
    {
        return pause;
    }
    
    public void setExpired(int expired)
    {
        this.expired = expired;
    }
    
    public int getExpired()
    {
        return expired;
    }
    
    public void setFile(MultipartFile file)
    {
        this.file = file;
    }
    
    public MultipartFile getFile()
    {
        return file;
    }
    
    public void setUserAdvert(UserClass userAdvert)
    {
        this.userAdvert = userAdvert;
    }
    
    public UserClass getUserAdvert()
    {
        return userAdvert;
    }
    
    public void setActionButton(String value)
    {
        this.actionButton = value;
    }
    
    public String getActionButton()
    {
        return actionButton;
    }
}