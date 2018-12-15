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
    /*@Autowired
    private CommentClassRepo ccr;
    @Autowired
    private SubCommentClassRepo qcr;
    */
    
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
    
    public long rateCalculator(long red, long yellow, long share, long likes, long views, long followers)
    {
        red = 2 * red;
        long gain = (yellow * 20) + (share * 10) + (likes * 50) + (views * 5) + (followers * 2);
        long rate = gain/red;
        
        return rate;
    }
    
    public void sessionUsername(HttpServletRequest req)
    {
        HttpSession session = req.getSession();
        session.setAttribute("username", getUser().getUsername());
    }
    
    public void modelUsername(ModelMap model)
    {
        model.addAttribute("username", getUser().getUsername());
    }
}