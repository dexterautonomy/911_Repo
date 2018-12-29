package com.hingebridge.utility;

import com.hingebridge.model.CommentClass;
import com.hingebridge.model.FollowerObject;
import com.hingebridge.model.MessageObject;
import com.hingebridge.model.PostClass;
import com.hingebridge.model.SubCommentClass;
import com.hingebridge.model.UserClass;
import com.hingebridge.repository.CommentClassRepo;
import com.hingebridge.repository.FollowedPostDeleteObjectRepo;
import com.hingebridge.repository.FollowerObjectRepo;
import com.hingebridge.repository.MessageObjectRepo;
import com.hingebridge.repository.PostClassRepo;
import com.hingebridge.repository.SubCommentClassRepo;
import com.hingebridge.repository.UserClassRepo;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
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
    
    @Value("${value.a}")
    private int a;
    @Value("${value.b}")
    private int b;
    @Value("${value.c}")
    private int c;
    @Value("${value.e}")
    private int e;
    @Value("${users.size}")
    private int userSize;
    
    /*
    @Value("${value.d}")
    private int d;
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
    @Autowired
    private FollowedPostDeleteObjectRepo fpdor;
    
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
    
    public void userRankSetting(UserClass uc, long rank)
    {
        if(rank < 1500)
        {
            uc.setColorclass("user_low");
        }
        else if(rank > 1500 && rank < 5000)
        {
            uc.setColorclass("user_beginner");
        }
        else if(rank > 5000 && rank < 10000)
        {
            uc.setColorclass("user_pro");
        }
        else if(rank > 10000 && rank < 15000)
        {
            uc.setColorclass("user_master");
        }
        else if(rank > 15000 && rank < 20000)
        {
            uc.setColorclass("user_legend");
        }
        else if(rank > 20000 && rank < 25000)
        {
            uc.setColorclass("user_genius");
        }
        else if(rank > 25000 && rank < 30000)
        {
            uc.setColorclass("user_guru");
        }
        else if(rank > 30000 && rank < 30020)
        {
            uc.setColorclass("user_mod");
        }
        else
        {
            uc.setColorclass("user_god");
        }
    }
    
    
    public void alterUserRankingParameters(long user_id, String action, UserClassRepo ucr)
    {
        Optional<UserClass> uc = ucr.findById(user_id);
        long rank = uc.get().getUserrank();
        
        switch (action)
        {
            case "save_like":
            {
                rank = rank + a;
                long likes = uc.get().getGreenlike();
                likes = likes + 1;
                uc.get().setGreenlike(likes);
                userRankSetting(uc.get(), rank);
            }
            break;
            
            case "save_unlike":
            {
                rank = rank - a;
                long likes = uc.get().getGreenlike();
                likes = likes - 1;
                
                if(rank < 0)
                {
                    rank = 0;
                }
                if(likes < 0)
                {
                    likes = 0;
                }
                userRankSetting(uc.get(), rank);
                uc.get().setGreenlike(likes);
            }
            break;
            
            case "save_redflag":
            {
                rank = rank - e;
                
                if(rank < 0)
                {
                    rank = 0;
                }
                long redflag = uc.get().getRedflag();
                redflag = redflag + 1;
                userRankSetting(uc.get(), rank);
                uc.get().setRedflag(redflag);
            }
            break;
            
            case "save_unredflag":
            {
                //rank = rank + e;
                long redflag = uc.get().getRedflag();
                redflag = redflag - 1;
                
                if(redflag < 0)
                {
                    redflag = 0;
                }
                userRankSetting(uc.get(), rank);
                uc.get().setRedflag(redflag);
            }
            break;
            
            case "save_star":
            {
                rank = rank + c;
                long star = uc.get().getYellowstar();
                star = star + 1;
                userRankSetting(uc.get(), rank);
                uc.get().setYellowstar(star);
            }
            break;
            
            case "save_unstar":
            {
                rank = rank - c;
                long star = uc.get().getYellowstar();
                star = star - 1;
                
                if(rank < 0)
                {
                    rank = 0;
                }
                if(star < 0)
                {
                    star = 0;
                }
                userRankSetting(uc.get(), rank);
                uc.get().setYellowstar(star);
            }
            break;
            
            case "save_share":
            {
                rank = rank + b;
                long share = uc.get().getBlueshare();
                share = share + 1;
                userRankSetting(uc.get(), rank);
                uc.get().setBlueshare(share);
            }
            break;
            
            case "save_unshare":
            {
                rank = rank - b;
                long share = uc.get().getBlueshare();
                share = share - 1;
                
                if(rank < 0)
                {
                    rank = 0;
                }
                if(share < 0)
                {
                    share = 0;
                }
                userRankSetting(uc.get(), rank);
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
        
        uc.get().setUserrank(rank);
        ucr.save(uc.get());
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
        session.setAttribute("commentBan", getUser().getCommentban());
    }
    
    public void modelUser(ModelMap model)
    {
        model.addAttribute("user", getUser());
    }
    
    public long getMessageObjSize(ModelMap model)
    {
        long size;
        List<MessageObject> mo = mobjr.getMyMessage(getUser().getId());
        List<MessageObject> unReadMessagesList = new LinkedList<>();
        if(!mo.isEmpty())
        {
            for(MessageObject mObj : mo)
            {
                if(mObj.getUnread().equals("unread"))
                {
                    unReadMessagesList.add(mObj);
                }
            }
        
            if(!unReadMessagesList.isEmpty())
            {
                size = unReadMessagesList.size();
                model.addAttribute("unReadMessages", "alertNote");
            }
            else
            {
                size = mo.size();
            }
        }
        else
        {
            size = 0l;
        }
        return size;
    }
    
    public long getFollowedPostSize(ModelMap model)
    {
        long size;
        LinkedList<PostClass> pcList = new LinkedList<>();
        LinkedList<PostClass> unReadPcList = new LinkedList<>();
        List<FollowerObject> followedObj = fobjr.getSelectedFollow(getUser().getId());
        
        if(!followedObj.isEmpty())
        {
            List<PostClass> po = pcr.followersPost(followedObj);
            
            for(PostClass pClass : po)
            {
                boolean var = fpdor.getDeletedPostObject(pClass.getId(), getUser().getId());
                    
                if(!var)
                {
                    pcList.add(pClass);
                }
            }
            
            if(!pcList.isEmpty())
            {
                //size = pcList.size();
                
                for(PostClass pcObj : pcList)
                {
                    boolean var = fpdor.getReadPostObject(pcObj.getId(), getUser().getId());
                    
                    if(!var)
                    {
                        unReadPcList.add(pcObj);
                    }
                }
        
                if(!unReadPcList.isEmpty())
                {
                    size = unReadPcList.size();
                    model.addAttribute("unReadPosts", "alertNote");
                }
                else
                {
                    size = pcList.size();
                }
            }
            else
            {
                size = 0l;
            }
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
        model.addAttribute("size", getMessageObjSize(model));
        model.addAttribute("pize", getFollowedPostSize(model));
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
    
    public boolean checkCredit()
    {
        boolean gotCredit = false;
        
        if(getUser().getAdscredit() > 0)
        {
            gotCredit = true;
        }
        
        return gotCredit;
    }
    
    public boolean usernameExists(String username)  //Makes sure we have no duplicate usernames
    {
        boolean itExists = false;
        Optional<UserClass> uc = ucr.findByUsername(username);
        
        if(uc.orElse(null) != null)
        {
            itExists = true;
        }
        
        return itExists;
    }
    
    public boolean invalidEntry(String value)  //Makes sure we have no spaces
    {
        boolean containsNonAlphabet = false;
        char[] userNameArray = value.toCharArray();
        
        for(char c1 : userNameArray)
        {
            if(!Character.isLetterOrDigit(c1))
            {
                containsNonAlphabet = true;
            }
        }
        
        return containsNonAlphabet;
    }
    
    public boolean emailExists(String email)  //Makes sure we have no duplicate emails
    {
        boolean itExists = false;
        Optional<UserClass> uc = ucr.findByEmail(email);
        
        if(uc.orElse(null) != null)
        {
            itExists = true;
        }
        
        return itExists;
    }
    
    public boolean passwordCheck(String password)
    {
        boolean shortPassword = false;
        
        if(password.length() < 8)
        {
            shortPassword = true;
        }
        
        return shortPassword;
    }
    /*
    public boolean appBan()    //If true, you cannot make comments
    {
        boolean banned = false;
        Optional<UserClass> uc = ucr.findById(getUser().getId());
        
        if(uc.get().getAppban()== 1)
        {
            banned = true;
        }
        
        return banned;
    }
    */    

    public boolean checkPostBan()   //If true, you cannot make posts
    {
        boolean banned = false;
        Optional<UserClass> uc = ucr.findById(getUser().getId());
        
        if(uc.get().getPostban() == 1)
        {
            banned = true;
        }
        
        return banned;
    }
    
    public boolean checkCommentBan()    //If true, you cannot make comments
    {
        boolean banned = false;
        Optional<UserClass> uc = ucr.findById(getUser().getId());
        
        if(uc.get().getCommentban() == 1)
        {
            banned = true;
        }
        
        return banned;
    }
}