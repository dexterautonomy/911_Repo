package com.hingebridge.model;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "subcommentreactionclass")
public class SubCommentReactionClass implements Serializable
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "subcomment_id")
    private long subcomment_id;
    @Column(name = "user_id")
    private long user_id;
    @Column(name = "like_flag")
    private int likeflag;
    @Column(name = "red_flag")
    private int redflag;
    @Column(name = "star_flag")
    private int starflag;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "subcomment_id", insertable = false, updatable = false, nullable = false)
    private SubCommentClass subcommentreactionobj;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", insertable = false, updatable = false, nullable = false)
    private UserClass usersubcommentreactobj;
    
    public SubCommentReactionClass(){}
    
    public SubCommentReactionClass(long subcomment_id, long user_id, int likeflag, int redflag, int starflag)
    {
        this.subcomment_id = subcomment_id;
        this.user_id = user_id;
        this.likeflag = likeflag;
        this.redflag = redflag;
        this.starflag = starflag;
    }
    
    public long getId()
    {
        return id;
    }
    
    public void setSubcomment_id(long value)
    {
        subcomment_id = value;
    }
    
    public long getSubcomment_id()
    {
        return subcomment_id;
    }
    
    public void setUser_id(long value)
    {
        user_id = value;
    }
    
    public long getUser_id()
    {
        return user_id;
    }
    
    public void setLikeflag(int value)
    {
        likeflag = value;
    }
    
    public int getLikeflag()
    {
        return likeflag;
    }
    
    public void setRedflag(int value)
    {
        redflag = value;
    }
    
    public int getRedflag()
    {
        return redflag;
    }
    
    public void setStarflag(int value)
    {
        starflag = value;
    }
    
    public int getStarflag()
    {
        return starflag;
    }
    
    public void setSubcommentreactionobj(SubCommentClass scc)
    {
        subcommentreactionobj = scc;
    }
    
    public SubCommentClass getSubcommentreactionobj()
    {
        return subcommentreactionobj;
    }
    
    public void setUsersubcommentreactobj(UserClass uc)
    {
        usersubcommentreactobj = uc;
    }
    
    public UserClass getUsersubcommentreactobj()
    {
        return usersubcommentreactobj;
    }
}