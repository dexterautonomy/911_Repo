package com.hingebridge.utility;

import com.hingebridge.model.CommentClass;
import com.hingebridge.model.FollowerObject;
import com.hingebridge.model.MessageObject;
import com.hingebridge.model.PostClass;
import com.hingebridge.model.SubCommentClass;
import com.hingebridge.model.UserClass;
import com.hingebridge.repository.CommentClassRepo;
import com.hingebridge.repository.FollowerObjectRepo;
import com.hingebridge.repository.MessageObjectRepo;
import com.hingebridge.repository.PostClassRepo;
import com.hingebridge.repository.SubCommentClassRepo;
import com.hingebridge.repository.UserClassRepo;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.ui.ModelMap;

@Component
@PropertySource("classpath:application.properties")
public class UtilityClass
{
    @Value("${file.path}")
    String filePath;
    
    @Value("${value.b}")
    private int b;
    @Value("${value.c}")
    private int c;
    @Value("${users.size}")
    private int userSize;
    
    /*
    @Value("${value.a}")
    private int a;
    @Value("${value.d}")
    private int d;
    @Value("${value.e}")
    private int e;
    @Value("${value.f}")
    private int f;
    @Value("${value.g}")
    private int g;
    @Value("${value.h}")
    private int h;
    @Value("${value.i}")
    private int i;
    @Value("${value.j}")
    private int j;
    */
    
    @Autowired
    private UserClassRepo ucr;
    @Autowired
    private MessageObjectRepo mobjr;
    @Autowired
    private PostClassRepo pcr;
    @Autowired
    private FollowerObjectRepo fobjr;
    
    public UserClass getUser()
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Optional<UserClass> uc = ucr.findByUsername(username);
        return uc.get();
    }
    
    public String getFilePath()
    {
        return filePath;
    }
    
    public String getDate()
    {
        String daySuffix;
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy,  hh:mm:ss a");
        String[] dateAndTime=sdf.format(date).split(" ");
        int day=Integer.parseInt(dateAndTime[1]);
        switch (day) 
        {
            case 1:
                daySuffix = String.valueOf(day + "st");
                break;
            case 2:
                daySuffix = String.valueOf(day+"nd");
                break;
            case 3:
                daySuffix = String.valueOf(day+"rd");
                break;
            case 21:
                daySuffix = String.valueOf(day+"st");
                break;
            case 22:
                daySuffix = String.valueOf(day+"nd");
                break;
            case 23:
                daySuffix = String.valueOf(day+"rd");
                break;
            case 31:
                daySuffix = String.valueOf(day+"st");
                break;
            default:
                daySuffix = String.valueOf(day+"th");
                break;
        }
        return sdf.format(date).replaceFirst(dateAndTime[1], daySuffix);
    }
    
    public String toTitleCase(String title)
    {
        String titleCase="";
        if(!title.matches("\\s*"))
        {
            String[] words=title.trim().split("\\s+");           
            for(int i=0; i<words.length; i++)
            {
                words[i]=Character.toUpperCase(words[i].charAt(0)) + words[i].substring(1).toLowerCase();
                titleCase += words[i] + " ";
            }
        }
        return titleCase;
    }
    
    public void dispBlock(ModelMap model, String showBlock, String[] hideBlocks)
    {
        model.addAttribute(showBlock, "");
        for(String s: hideBlocks)
        {
            model.addAttribute(s, "hidden");
        }
    }
    
    public void updateViews(Optional<PostClass> pc)
    {
        long views = pc.get().getViews();
        views = views + 1;
        pc.get().setViews(views);
        pcr.save(pc.get());
    }
    
    public void userRankSetting(Optional<UserClass> uc)
    {
        long likes, red, star, share, followers, rank;
        
        likes = uc.get().getGreenlike();
        red = 10 * uc.get().getRedflag();
        star = 2 * uc.get().getYellowstar();
        share = 2 * uc.get().getBlueshare();
        followers = uc.get().getFollowers();
        
        rank = (likes + star + share + followers) - red;
        
        if(rank < 0)
        {
            rank = 0l;
        }
        
        if(rank < 1500)
        {
            uc.get().setColorclass("user_low");
        }
        else if(rank > 1500 && rank < 5000)
        {
            uc.get().setColorclass("user_beginner");
        }
        else if(rank > 5000 && rank < 10000)
        {
            uc.get().setColorclass("user_pro");
        }
        else if(rank > 10000 && rank < 15000)
        {
            uc.get().setColorclass("user_master");
        }
        else if(rank > 15000 && rank < 20000)
        {
            uc.get().setColorclass("user_legend");
        }
        else if(rank > 20000 && rank < 25000)
        {
            uc.get().setColorclass("user_genius");
        }
        else if(rank > 25000 && rank < 30000)
        {
            uc.get().setColorclass("user_guru");
        }
        else if(rank > 30000 && rank < 30020)
        {
            uc.get().setColorclass("user_mod");
        }
        else
        {
            uc.get().setColorclass("user_god");
        }
        
        
        uc.get().setUserrank(rank);
        ucr.save(uc.get());
    }
    
    
    public void alterUserRankingParameters(long user_id, String action, UserClassRepo ucr)
    {
        Optional<UserClass> uc = ucr.findById(user_id);
        
        switch (action)
        {
            case "save_like":
            {
                long likes = uc.get().getGreenlike();
                likes = likes + 1;
                uc.get().setGreenlike(likes);
            }
            break;
            
            case "save_unlike":
            {
                long likes = uc.get().getGreenlike();
                likes = likes - 1;
                
                if(likes < 0)
                {
                    likes = 0;
                }
                uc.get().setGreenlike(likes);
            }
            break;
            
            case "save_redflag":
            {
                long redflag = uc.get().getRedflag();
                redflag = redflag + 1;
                uc.get().setRedflag(redflag);
            }
            break;
            
            case "save_unredflag":
            {
                long redflag = uc.get().getRedflag();
                redflag = redflag - 1;
                
                if(redflag < 0)
                {
                    redflag = 0;
                }
                uc.get().setRedflag(redflag);
            }
            break;
            
            case "save_star":
            {
                long star = uc.get().getYellowstar();
                star = star + 1;
                uc.get().setYellowstar(star);
            }
            break;
            
            case "save_unstar":
            {
                long star = uc.get().getYellowstar();
                star = star - 1;
                
                if(star < 0)
                {
                    star = 0;
                }
                uc.get().setYellowstar(star);
            }
            break;
            
            case "save_share":
            {
                long share = uc.get().getBlueshare();
                share = share + 1;
                uc.get().setBlueshare(share);
            }
            break;
            
            case "save_unshare":
            {
                long share = uc.get().getBlueshare();
                share = share - 1;
                
                if(share < 0)
                {
                    share = 0;
                }
                uc.get().setBlueshare(share);
            }
            break;
            
            case "save_view":
            {
                long view = uc.get().getBlackview();
                view = view + 1;
                uc.get().setBlackview(view);
            }
            break;
        }
        
        ucr.save(uc.get());
        userRankSetting(uc);
    }
    
    public void alterCommentRankingParameters(Optional<CommentClass> cc, CommentClassRepo ccr)
    {
        long likes, redflag, star, share, rank;
        
        likes = cc.get().getLikes();
        redflag = cc.get().getRedflag();
        star = cc.get().getStar();
        share = cc.get().getShare();
        
        rank = likes + (b * star) + (c * share);
        redflag = c * redflag;
        Long rankDiff = rank - redflag;
        
        if(rankDiff > 0)
        {
            rankDiff = (rankDiff * 100)/cc.get().getCommentreact().size();
            
            if(rankDiff > 100)
            {
                rankDiff = 100l;
            }
        }
        else
        {
            rankDiff = 0l;
        }
        
        if(cc.get().getCommentreact().size() > userSize && redflag > rank)  //All these hard-coded values should come via the application.properties file to allow for easy update
        {
            cc.get().setApproved(0);
        }
        
        cc.get().setCommentrank(rankDiff.intValue());
        ccr.save(cc.get());
    }
    
    public void alterSubCommentRankingParameters(Optional<SubCommentClass> scc, SubCommentClassRepo sccr)
    {
        long likes, redflag, star, rank;
        
        likes = scc.get().getLikes();
        redflag = scc.get().getRedflag();
        star = scc.get().getStar();
        
        rank = likes + (b * star);
        
        Long rankDiff = rank - redflag;
        
        if(rankDiff > 0)
        {
            rankDiff = (rankDiff * 100)/scc.get().getSubcommentreact().size();
            
            if(rankDiff > 100)
            {
                rankDiff = 100l;
            }
        }
        else
        {
            rankDiff = 0l;
        }
        
        if(scc.get().getSubcommentreact().size() > userSize && redflag > rank)  //All these hard-coded values should come via the application.properties file to allow for easy update
        {
            scc.get().setApproved(0);
        }
        
        scc.get().setSubcommentrank(rankDiff.intValue());
        sccr.save(scc.get());
    }
    
    public void sessionUserDetails(HttpServletRequest req)  //important for pages/reader.html because session seems not to be able to call object properties
    {
        HttpSession session = req.getSession();
        session.setAttribute("username", getUser().getUsername());
        session.setAttribute("usercolorclass", getUser().getColorclass());
    }
    
    public void modelUser(ModelMap model)
    {
        model.addAttribute("user", getUser());
    }
    
    public long getMessageObjSize()
    {
        List<MessageObject> mo = mobjr.getMyMessage(getUser().getId());
        return mo.size();
    }
    
    public long getFollowedPostSize()
    {
        long size;
        
        List<FollowerObject> followedObj = fobjr.getSelectedFollow(getUser().getId());
        if(!followedObj.isEmpty())
        {
            List<PostClass> po = pcr.followersPost(followedObj);
            size = po.size();
        }
        else
        {
            size = 0l;
        }
        return size;
    }
    
    public long getMyTrendSize()
    { 
        List<PostClass> trendSize = pcr.getAllMyPost(getUser().getId());
        return trendSize.size();
    }
    
    public void modelTransfer(ModelMap model)
    {
        model.addAttribute("size", getMessageObjSize());
        model.addAttribute("pize", getFollowedPostSize());
        model.addAttribute("tize", getMyTrendSize());
    }
    
    public void updateInbox(MessageObjectRepo mobjr, long user_id, long commentid, String postlink)
    {
        Optional<MessageObject> mo = mobjr.findByCommentid(commentid);
        
        if(mo.orElse(null) != null)
        {
            switch(mo.get().getFlag())
            {
                case 0:
                {
                    if(!getUser().getId().equals(mo.get().getRecipient_id()))   //No need notifying yourself on a subcomment you made na
                    {
                        if(!mo.get().getUnread().equals("unread"))
                        {
                            mo.get().setUnread("unread");
                        }
                    
                        mo.get().setFlag(1);
                        mobjr.save(mo.get());
                    }
                }
                break;
                
                case 1:
                {
                    if(!getUser().getId().equals(mo.get().getRecipient_id()))   //No need notifying yourself on a subcomment you made na
                    {
                        if(!mo.get().getUnread().equals("unread"))
                        {
                            mo.get().setUnread("unread");
                            mobjr.save(mo.get());
                        }
                    }
                    
                }
                break;
            }
        }
        else
        {
            MessageObject messageObj = new MessageObject(user_id, commentid, postlink);
            mobjr.save(messageObj);
        }
    }
}