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
import com.hingebridge.repository.SubCommentClassRepo;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.ui.ModelMap;

@Component
@PropertySource("classpath:application.properties")
public class UtilityClass
{
    @Value("${file.path}")
    String filePath;
    
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
        return uc.orElse(null);
    }
    
    public String getFilePath()
    {
        return filePath;
    }
    
    /*
    public PostClass getPost(Long id, String title)
    {
        Optional<PostClass> pc = pcr.getPostReader(id, title);
        return pc.get();
    }
    
    public CommentClass getComment(Long id)
    {
        Optional<CommentClass> cc = ccr.getComment(id);
        return cc.get();
    }
    
    public SubCommentClass getSubComment(Long id)
    {
        Optional<SubCommentClass> subcom = qcr.getSubComment(id);
        return subcom.get();
    }
    */
    
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
    
    /*
    public void dispBlock(HttpSession session, String showBlock, String[] hideBlocks)
    {
        session.setAttribute(showBlock, "");
        for(String s: hideBlocks)
        {
            session.setAttribute(s, "hidden");
        }
    }
    */
    
    public void userRankSetting(Optional<UserClass> uc)//(long user_id)
    {
        Long likes, red, star, share, followers, rank;
        //Optional<UserClass> uc = ucr.findById(user_id);
        
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
        else if(rank > 30000)
        {
            uc.get().setColorclass("user_mod");
        }
        
        uc.get().setUserrank(rank);
        ucr.save(uc.get());
    }
    
    
    public void alterUserRateParameters(long user_id, String action)
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
    
    public void sessionUsername(HttpServletRequest req)
    {
        HttpSession session = req.getSession();
        session.setAttribute("username", getUser().getUsername());
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
}