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
    private long id;
    @Column(name = "user_id")
    private long userId;
    @Column(name = "approve")
    private int approve = 0;
    @Column(name = "payoption")
    private String payOption;
    @Column(name = "startdate")
    private String startDate;
    @Column(name = "clicks")
    private long clicks = 0;
    @Column(name = "views")
    private long views = 0;
    @Column(name = "adsimage")
    private String adsImage;
    @Column(name = "landingpage")
    private String landingPage;
    @Column(name = "pause")
    private int pause = 0;
    @Column(name = "expired")
    private int expired = 0;
    
    @Transient
    private MultipartFile file;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", insertable = false, updatable = false, nullable = false)
    private UserClass userAdvert;
    
    public AdvertObject(){}
    
    public AdvertObject(long userId, String payOption, String startDate, String adsImage, String landingPage)
    {
        this.userId = userId;
        this.payOption = payOption;
        this.startDate = startDate;
        this.adsImage = adsImage;
        this.landingPage = landingPage;
    }
    
    public long getId()
    {
        return id;
    }
    
    public void setUserId(long userId)
    {
        this.userId = userId;
    }
    
    public long getUserId()
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
    
    public void setClicks(long clicks)
    {
        this.clicks = clicks;
    }
    
    public long getClicks()
    {
        return clicks;
    }
    
    public void setViews(long views)
    {
        this.views = views;
    }
    
    public long getViews()
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
}