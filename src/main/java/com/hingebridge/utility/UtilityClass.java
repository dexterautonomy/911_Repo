package com.hingebridge.utility;

import com.hingebridge.model.CommentClass;
import com.hingebridge.model.PostClass;
import com.hingebridge.model.SubCommentClass;
import com.hingebridge.model.UserClass;
import com.hingebridge.repository.CommentClassRepo;
import com.hingebridge.repository.PostClassRepo;
import com.hingebridge.repository.UserClassRepo;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import com.hingebridge.repository.SubCommentClassRepo;
import javax.servlet.http.HttpSession;

@Component
@PropertySource("classpath:application.properties")
public class UtilityClass
{
    @Value("${file.path}")
    String filePath;
    
    @Autowired
    UserClassRepo ucr;
    @Autowired
    PostClassRepo pcr;
    
    @Autowired
    CommentClassRepo ccr;
    @Autowired
    SubCommentClassRepo qcr;
    
    public UserClass getUser()
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Optional<UserClass> uc = ucr.findByUsername(username);
        return uc.orElse(null);
    }
    
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
}