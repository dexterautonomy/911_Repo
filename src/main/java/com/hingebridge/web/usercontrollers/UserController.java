package com.hingebridge.web.usercontrollers;

import com.google.gson.Gson;
import com.hingebridge.model.AdvertObject;
import com.hingebridge.model.CommentClass;
import com.hingebridge.model.DynamicContent;
import com.hingebridge.model.FollowedPostDeleteObject;
import com.hingebridge.model.FollowerObject;
import com.hingebridge.model.InboxObject;
import com.hingebridge.model.MessageObject;
import com.hingebridge.model.PostClass;
import com.hingebridge.model.QuoteInboxObject;
import com.hingebridge.model.QuoteObject;
import com.hingebridge.model.ReplyObject;
import com.hingebridge.model.SubCommentClass;
import com.hingebridge.model.UserClass;
import com.hingebridge.repository.AdvertObjectRepo;
import com.hingebridge.repository.CommentClassRepo;
import com.hingebridge.repository.CommentReactionClassRepo;
import com.hingebridge.repository.FollowedPostDeleteObjectRepo;
import com.hingebridge.repository.FollowerObjectRepo;
import com.hingebridge.repository.InboxObjectRepo;
import com.hingebridge.repository.MessageObjectRepo;
import com.hingebridge.repository.PostClassRepo;
import com.hingebridge.utility.UtilityClass;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.hingebridge.repository.PostReactionClassRepo;
import com.hingebridge.repository.QuoteInboxObjectRepo;
import com.hingebridge.repository.QuoteObjectRepo;
import com.hingebridge.repository.ReplyObjectRepo;
import com.hingebridge.repository.SubCommentClassRepo;
import com.hingebridge.repository.SubCommentReactionClassRepo;
import com.hingebridge.repository.UserClassRepo;
import com.hingebridge.utility.AdvertAlgorithmClass;
import org.springframework.web.bind.annotation.ResponseBody;

@PreAuthorize("hasRole('USER')")
@Controller
@RequestMapping("/user")
public class UserController
{
    @Autowired
    private UtilityClass utc;
    @Autowired
    private AdvertAlgorithmClass aac;
    @Autowired
    private UserClassRepo ucr;
    @Autowired
    private PostClassRepo pcr;
    @Autowired
    private CommentClassRepo ccr;
    @Autowired
    private SubCommentClassRepo sccr;
    @Autowired
    private PostReactionClassRepo prcr;
    @Autowired
    private FollowerObjectRepo fobjr;
    @Autowired
    private MessageObjectRepo mobjr;
    @Autowired
    private QuoteObjectRepo qobjr;
    @Autowired
    private CommentReactionClassRepo crcr;
    @Autowired
    private SubCommentReactionClassRepo scrcr;
    @Autowired
    private FollowedPostDeleteObjectRepo fpdor;
    @Autowired
    private AdvertObjectRepo aor;
    @Autowired
    private InboxObjectRepo ior;
    @Autowired
    private ReplyObjectRepo ror;
    @Autowired
    private QuoteInboxObjectRepo qior;
    
    @GetMapping("/login")
    public String userHomePage(HttpServletRequest req, ModelMap model)
    {
        aac.displayAdvert(model);   //This line is for adverts
        
        String[] hideBlocks = {"secondBlock", "thirdBlock", "fourthBlock"};
        utc.dispBlock(model, "firstBlock", hideBlocks);
        utc.userModel(model);
        utc.sessionUserDetails(req);    //very important
        model.addAttribute("postclass", new PostClass());
        
        if(utc.checkPostBan())
        {
            return "redirect:/user/notf?pg=1";
        }
        
        return "pages/userpage";
    }
    
    @PostMapping("/postcontrol")
    public String userPostControl(@ModelAttribute("postclass")PostClass pc, HttpServletRequest req, 
    ModelMap model, RedirectAttributes ra) throws IOException
    {
        aac.displayAdvert(model);   //This line is for adverts
        
        if(utc.checkPostBan())
        {
            //You cannot make a post when you are postbanned
            return "redirect:/user/notf?pg=1";
        }
        
        String[] hideBlocks = {"secondBlock", "thirdBlock", "fourthBlock"};
        utc.dispBlock(model, "firstBlock", hideBlocks);
        utc.userModel(model);
        String path = utc.getFilePath()+"dist_img";
        String date = utc.getDate();
        
        String title = utc.toTitleCase(pc.getTitle().trim());
        String content = pc.getContent().trim();
        String rankMyPost = pc.getRank();
        String category = pc.getCategory();
        
        switch(pc.getActionButton())
        {
            case "add_img":
            {
                pc.setTitle(title);
                pc.setContent(content);
                pc.setCategory(category);
                
                MultipartFile contentFile =  pc.getContentFile();
                
                if(contentFile.isEmpty())
                {
                    model.addAttribute("alert", "No image file selected");
                    return "pages/userpage";
                }
                
                if(contentFile.getSize() > 0 && contentFile.getSize() <= 4000000)
                {
                    String contentFileName = contentFile.getOriginalFilename();
                    if(contentFileName != null && contentFileName.length() <= 50)
                    {
                        if(contentFileName.endsWith(".jpg") || contentFileName.endsWith(".png") || contentFileName.endsWith(".gif") 
                        || contentFileName.endsWith(".jpeg") || contentFileName.endsWith(".JPG") || contentFileName.endsWith(".PNG") 
                        || contentFileName.endsWith(".GIF") || contentFileName.endsWith(".JPEG") || contentFileName.endsWith(".webp") 
                        || contentFileName.endsWith(".WEBP"))
                        {
                            String imageRef="<_" + contentFileName + "_>";
                            pc.setTitle(title);
                            pc.setContent(content + imageRef);
                            pc.setCategory(category);
                                
                            model.addAttribute("alert", "Image added to content");
                            File pathToFile=new File(path, contentFileName);
                            contentFile.transferTo(pathToFile);
                        }
                        else
                        {
                            model.addAttribute("alert", "Invalid image format (suported: jpg, png, gif, webp)");
                        }
                    }
                    else
                    {
                        model.addAttribute("alert", "Content image name too long (less than 50 characters)");
                    }
                }
                else if(contentFile.getSize() == 0)
                {
                    model.addAttribute("alert", "");
                }
                else
                {
                    model.addAttribute("alert", "File size exceeded (4MB or less)");
                }
            }
            break;
            
            case "post":
            {
                pc.setTitle(title);
                pc.setContent(content);
                pc.setCategory(category);
                pc.setRank(rankMyPost);
                PostClass postClass = null;
                
                if(!utc.checkTag2(title))  //Arrange this well
                {
                    model.addAttribute("alert", "Title must not contain < or >");
                    return "pages/userpage";
                }
                
                if(!utc.checkTag(content))  //Arrange this well
                {
                    model.addAttribute("alert", "Some tags are not properly closed [<_ must end with _>]");
                    return "pages/userpage";
                }
                
                content=content.replaceAll("<_", "<br/><img alt='content image' width='250' height='150' src='/9jaforum/files/dist_img/");
                content=content.replaceAll("_>", "'/><br/>");
                
                MultipartFile coverFile = pc.getCoverFile();
                
                if(!title.matches("\\s*"))
                {
                    if(title.length() > 0 && title.length() <= 100)
                    {
                        if(!content.matches("\\s*"))
                        {
                            if(rankMyPost != null)
                            {
                                if(coverFile.getSize() > 0 && coverFile.getSize() <= 4000000)
                                {
                                    String coverFileName = coverFile.getOriginalFilename();
                                    if(coverFileName != null && coverFileName.length() <= 50)
                                    {
                                        if(coverFileName.endsWith(".jpg") || coverFileName.endsWith(".png") || coverFileName.endsWith(".gif") 
                                        || coverFileName.endsWith(".jpeg") || coverFileName.endsWith(".JPG") || coverFileName.endsWith(".PNG") 
                                        || coverFileName.endsWith(".GIF") || coverFileName.endsWith(".JPEG") || coverFileName.endsWith(".webp") 
                                        || coverFileName.endsWith(".WEBP"))
                                        {
                                            switch(category)
                                            {
                                                case "mypost":
                                                {
                                                    if(content.length() < 1500)
                                                    {
                                                        model.addAttribute("alert", "Content must be more than 1500 characters [" + pc.getContent().length() +"]");
                                                        return "pages/userpage";
                                                    }
                                                    postClass = new PostClass(utc.getUser(), title, content, category, coverFileName, date);
                                                }
                                                break;
                                    
                                                case "opinion":
                                                {
                                                    if(content.length() < 1500)
                                                    {
                                                        model.addAttribute("alert", "Content must be more than 1500 characters [" + pc.getContent().length() +"]");
                                                        return "pages/userpage";
                                                    }
                                                    postClass = new PostClass(utc.getUser(), title, content, category, coverFileName, date);
                                                }
                                                break;
                                    
                                                case "memelogic":
                                                {
                                                    if(pc.getContent().contains("<_") && pc.getContent().contains("_>"))
                                                    {
                                                        postClass = new PostClass(utc.getUser(), title, content, category, coverFileName, date);
                                                    }
                                                    else
                                                    {
                                                        model.addAttribute("alert", "Content must have at least one image file");
                                                        return "pages/userpage";
                                                    }
                                                }
                                                break;
                                    
                                                case "poem_sarc":
                                                {
                                                    if(content.length() < 1500)
                                                    {
                                                        model.addAttribute("alert", "Content must be more than 1500 characters [" + pc.getContent().length() +"]");
                                                        return "pages/userpage";
                                                    }
                                                    postClass = new PostClass(utc.getUser(), title, content, category, coverFileName, date);
                                                }
                                                break;
                                    
                                                case "zex_battle":
                                                {
                                                    if(content.length() < 1500)
                                                    {
                                                        model.addAttribute("alert", "Content must be more than 1500 characters [" + pc.getContent().length() +"]");
                                                        return "pages/userpage";
                                                    }
                                                    postClass = new PostClass(utc.getUser(), title, content, category, coverFileName, date);
                                                }
                                                break;
                                            }
                                        
                                            pcr.save(postClass);
                                            utc.updatePosts(); //Updates your number of posts
                                            File pathToFile=new File(path, coverFileName);
                                            coverFile.transferTo(pathToFile);
                                            ra.addFlashAttribute("alert", "Posted");
                                            return "redirect:/user/login";
                                        }
                                        else
                                        {
                                            model.addAttribute("alert", "Invalid image format (suported: jpg, png, gif, webp)");
                                        }
                                    }
                                    else
                                    {
                                        model.addAttribute("alert", "Cover Image name too long (less than 50 characters)");
                                    }
                                }
                                //At this point make only memelogic upload cover image
                                else if(coverFile.getSize() == 0)   //why not use coverFile.isEmpty()
                                {
                                    String coverFileName = "empty.png";
                    
                                    switch(category)
                                    {
                                        case "mypost":
                                        {
                                            if(content.length() < 1500)
                                            {
                                                model.addAttribute("alert", "Content must be more than 1500 characters [" + pc.getContent().length() +"]");
                                                return "pages/userpage";
                                            }
                                            postClass = new PostClass(utc.getUser(), title, content, category, coverFileName, date);
                                        }
                                        break;
                                    
                                        case "opinion":
                                        {
                                            if(content.length() < 1500)
                                            {
                                                model.addAttribute("alert", "Content must be more than 1500 characters [" + pc.getContent().length() +"]");
                                                return "pages/userpage";
                                            }
                                            postClass = new PostClass(utc.getUser(), title, content, category, coverFileName, date);
                                        }
                                        break;
                            
                                        case "memelogic":
                                        {
                                            model.addAttribute("alert", "Memelogic must have a cover image");
                                            return "pages/userpage";
                                        }
                                    
                                        case "poem_sarc":
                                        {
                                            if(content.length() < 1500)
                                            {
                                                model.addAttribute("alert", "Content must be more than 1500 characters [" + pc.getContent().length() +"]");
                                                return "pages/userpage";
                                            }
                                            postClass = new PostClass(utc.getUser(), title, content, category, coverFileName, date);
                                        }
                                        break;
                                    
                                        case "zex_battle":
                                        {
                                            if(content.length() < 1500)
                                            {
                                                model.addAttribute("alert", "Content must be more than 1500 characters [" + pc.getContent().length() +"]");
                                                return "pages/userpage";
                                            }
                                            postClass = new PostClass(utc.getUser(), title, content, category, coverFileName, date);
                                        }
                                    }
                    
                                    pcr.save(postClass);
                                    utc.updatePosts(); //Updates your number of posts
                                    ra.addFlashAttribute("alert", "Posted");
                                    return "redirect:/user/login";
                                }
                                else
                                {
                                    model.addAttribute("alert", "File size exceeded (4MB or less)");
                                }
                            }
                            else
                            {
                                if(coverFile.getSize() > 0 && coverFile.getSize() <= 4000000)
                                {
                                    String coverFileName = coverFile.getOriginalFilename();
                                    if(coverFileName != null && coverFileName.length() <= 50)
                                    {
                                        if(coverFileName.endsWith(".jpg") || coverFileName.endsWith(".png") || coverFileName.endsWith(".gif") 
                                        || coverFileName.endsWith(".jpeg") || coverFileName.endsWith(".JPG") || coverFileName.endsWith(".PNG") 
                                        || coverFileName.endsWith(".GIF") || coverFileName.endsWith(".JPEG") || coverFileName.endsWith(".webp") 
                                        || coverFileName.endsWith(".WEBP"))
                                        {
                                            switch(category)
                                            {
                                                case "mypost":
                                                {
                                                    if(content.length() < 1500)
                                                    {
                                                        model.addAttribute("alert", "Content must be more than 1500 characters [" + pc.getContent().length() +"]");
                                                        return "pages/userpage";
                                                    }
                                                    postClass = new PostClass(utc.getUser(), 0L, title, content, category, coverFileName, date);
                                                }
                                                break;
                                    
                                                case "opinion":
                                                {
                                                    if(content.length() < 1500)
                                                    {
                                                        model.addAttribute("alert", "Content must be more than 1500 characters [" + pc.getContent().length() +"]");
                                                        return "pages/userpage";
                                                    }
                                                    postClass = new PostClass(utc.getUser(), 0L, title, content, category, coverFileName, date);
                                                }
                                                break;
                                    
                                                case "memelogic":
                                                {
                                                    if(pc.getContent().contains("<_") && pc.getContent().contains("_>"))
                                                    {
                                                        postClass = new PostClass(utc.getUser(), 0L, title, content, category, coverFileName, date);
                                                    }
                                                    else
                                                    {
                                                        model.addAttribute("alert", "Content must have at least one image file");
                                                        return "pages/userpage";
                                                    }
                                                }
                                                break;
                                    
                                                case "poem_sarc":
                                                {
                                                    if(content.length() < 1500)
                                                    {
                                                        model.addAttribute("alert", "Content must be more than 1500 characters [" + pc.getContent().length() +"]");
                                                        return "pages/userpage";
                                                    }
                                                    postClass = new PostClass(utc.getUser(), 0L, title, content, category, coverFileName, date);
                                                }
                                                break;
                                    
                                                case "zex_battle":
                                                {
                                                    if(content.length() < 1500)
                                                    {
                                                        model.addAttribute("alert", "Content must be more than 1500 characters [" + pc.getContent().length() +"]");
                                                        return "pages/userpage";
                                                    }
                                                    postClass = new PostClass(utc.getUser(), 0L, title, content, category, coverFileName, date);
                                                }
                                                break;
                                            }
                                        
                                            pcr.save(postClass);
                                            utc.updatePosts(); //Updates your number of posts
                                            File pathToFile=new File(path, coverFileName);
                                            coverFile.transferTo(pathToFile);
                                            ra.addFlashAttribute("alert", "Posted");
                                            return "redirect:/user/login";
                                        }
                                        else
                                        {
                                            model.addAttribute("alert", "Invalid image format (suported: jpg, png, gif, webp)");
                                        }
                                    }
                                    else
                                    {
                                        model.addAttribute("alert", "Cover Image name too long (less than 50 characters)");
                                    }
                                }
                                //At this point make only memelogic upload cover image
                                else if(coverFile.getSize() == 0)   //why not use coverFile.isEmpty()
                                {
                                    String coverFileName = "empty.png";
                    
                                    switch(category)
                                    {
                                        case "mypost":
                                        {
                                            if(content.length() < 1500)
                                            {
                                                model.addAttribute("alert", "Content must be more than 1500 characters [" + pc.getContent().length() +"]");
                                                return "pages/userpage";
                                            }
                                            postClass = new PostClass(utc.getUser(), 0L, title, content, category, coverFileName, date);
                                        }
                                        break;
                                    
                                        case "opinion":
                                        {
                                            if(content.length() < 1500)
                                            {
                                                model.addAttribute("alert", "Content must be more than 1500 characters [" + pc.getContent().length() +"]");
                                                return "pages/userpage";
                                            }
                                            postClass = new PostClass(utc.getUser(), 0L, title, content, category, coverFileName, date);
                                        }
                                        break;
                            
                                        case "memelogic":
                                        {
                                            model.addAttribute("alert", "Memelogic must have a cover image");
                                            return "pages/userpage";
                                        }
                                    
                                        case "poem_sarc":
                                        {
                                            if(content.length() < 1500)
                                            {
                                                model.addAttribute("alert", "Content must be more than 1500 characters [" + pc.getContent().length() +"]");
                                                return "pages/userpage";
                                            }
                                            postClass = new PostClass(utc.getUser(), 0L, title, content, category, coverFileName, date);
                                        }
                                        break;
                                    
                                        case "zex_battle":
                                        {
                                            if(content.length() < 1500)
                                            {
                                                model.addAttribute("alert", "Content must be more than 1500 characters [" + pc.getContent().length() +"]");
                                                return "pages/userpage";
                                            }
                                            postClass = new PostClass(utc.getUser(), 0L, title, content, category, coverFileName, date);
                                        }
                                    }
                    
                                    pcr.save(postClass);
                                    utc.updatePosts(); //Updates your number of posts
                                    ra.addFlashAttribute("alert", "Posted");
                                    return "redirect:/user/login";
                                }
                                else
                                {
                                    model.addAttribute("alert", "File size exceeded (4MB or less)");
                                }
                            }
                        }
                        else
                        {
                            model.addAttribute("alert", "Content cannot be empty");
                        }
                    }
                    else
                    {
                        model.addAttribute("alert", "Title is too long (should be less than 100 characters)");
                    }
                }
                else
                {
                    model.addAttribute("alert", "Title cannot be empty");
                }
            }
            break;
        }
        return "pages/userpage";
    }
    
    @GetMapping("/cmt")
    public String comment(@RequestParam("pos")Optional<Long> post_id, @RequestParam("t")Optional<String> title, 
    @RequestParam("p") Optional<Integer> pg, ModelMap model, @RequestParam("page")Optional<Integer> commentPaginate, 
    @RequestParam("akt")Optional<String> action, HttpServletRequest req, RedirectAttributes ra, 
    @RequestParam("cid")Optional<Long> comment_id, @RequestParam("sid")Optional<Long> subcomment_id, 
    @RequestParam("cog")Optional<String> cog)
    {
        aac.displayAdvert(model);   //This line is for adverts
        
        String ret= null;
        utc.sessionUserDetails(req);    //Do not ever remove this
        Optional<PostClass> pc = pcr.findById(post_id.get());
        
        long id = utc.getUser().getId();
        
        switch(action.orElse("cmt"))
        {
            case "cmt":
            {
                if(!utc.checkCommentBan())
                {
                    long userrank = utc.getUser().getUserrank();
                
                    if(userrank >= pc.get().getPostrank())
                    {
                        model.addAttribute("postclass", new PostClass());
                        model.addAttribute("pos", post_id.get());
                        model.addAttribute("t", title.get());
                        model.addAttribute("p", pg.get());
                        model.addAttribute("page", commentPaginate.get());
                        
                        if(!cog.get().equals(""))
                        {
                            model.addAttribute("cog", cog.get());
                        }
                        
                        ret = "pages/commentpage";
                    }
                    else
                    {
                        String alert = "Your rank is lesser than the post rank";
                        if(!cog.get().equals(""))
                        {
                            ret = "redirect:/b_ch?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get()+"&cog="+cog.get()+"&alertx="+alert;
                        }
                        else
                        {
                            ret = "redirect:/b_ch?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get()+"&alertx="+alert;
                        }
                    }
                }
                else
                {
                    String alert = "No access";
                    
                    if(!cog.get().equals(""))
                    {
                        ret = "redirect:/b_ch?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get()+"&cog="+cog.get()+"&alertx="+alert;
                    }
                    else
                    {
                        ret = "redirect:/b_ch?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get()+"&alertx="+alert;
                    }
                }
            }
            break;
            
            case "lk":
            {
                String check = prcr.likedBefore(post_id.get(), id);  //has this user liked this post before now?
                long likes = pc.get().getLikes();
                
                switch(check)
                {
                    case "":
                    {
                        likes = likes + 1;
                        pc.get().setLikes(likes);
                        pcr.save(pc.get());
                        //increase the overall likes of the owner of the post by 1
                        utc.alterUserRankingParameters(pc.get().getUser_id(), "save_like");
                    }
                    break;
                    
                    case "liked":
                    {
                        likes = likes - 1;
                        pc.get().setLikes(likes);
                        //decrease the overall likes of the owner of the post by 1
                        //check if his overall likes == 0 first, if its 0, leave it like that
                        utc.alterUserRankingParameters(pc.get().getUser_id(), "save_unlike");
                    }
                    break;
                    
                    case "unliked":
                    {
                        likes = likes + 1;
                        pc.get().setLikes(likes);
                        //increase the overall likes of the owner of the post by 1
                        utc.alterUserRankingParameters(pc.get().getUser_id(), "save_like");
                    }
                    break;
                }
                
                pcr.save(pc.get());
                
                if(!cog.get().equals(""))
                {
                    ret = "redirect:/b_ch?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get()+"&cog="+cog.get();
                }
                else
                {
                    ret = "redirect:/b_ch?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get();
                }
            }
            break;
            
            case "flg":
            {
                String check = prcr.redFlaggedBefore(post_id.get(), id);  //has this user liked this post before now?
                long redflag = pc.get().getRedflag();
                
                switch(check)
                {
                    case "":
                    {
                        redflag = redflag + 1;
                        pc.get().setRedflag(redflag);
                        //increase the overall redflags of the owner of the post by 1
                        utc.alterUserRankingParameters(pc.get().getUser_id(), "save_redflag");
                    }
                    break;
                    
                    case "redflagged":
                    {
                        redflag = redflag - 1;
                        pc.get().setRedflag(redflag);
                        //decrease the overall redflags of the owner of the post by 1
                        //check if his overall redflags == 0 first, if its 0, leave it like that
                        utc.alterUserRankingParameters(pc.get().getUser_id(), "save_unredflag");
                    }
                    break;
                    
                    case "notredflagged":
                    {
                        redflag = redflag + 1;
                        pc.get().setRedflag(redflag);
                        //increase the overall redflags of the owner of the post by 1
                        utc.alterUserRankingParameters(pc.get().getUser_id(), "save_redflag");
                    }
                    break;
                }
                
                pcr.save(pc.get());
                
                if(!cog.get().equals(""))
                {
                    ret = "redirect:/b_ch?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get()+"&cog="+cog.get();
                }
                else
                {
                    ret = "redirect:/b_ch?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get();
                }
            }
            break;
            
            case "str":
            {
                String check = prcr.starredBefore(post_id.get(), id);  //has this user liked this post before now?
                long star = pc.get().getStar();
                
                switch(check)
                {
                    case "":
                    {
                        star = star + 1;
                        pc.get().setStar(star);
                        //increase the overall star of the owner of the post by 1
                        utc.alterUserRankingParameters(pc.get().getUser_id(), "save_star");
                    }
                    break;
                    
                    case "starred":
                    {
                        star = star - 1;
                        pc.get().setStar(star);
                        //decrease the overall star of the owner of the post by 1
                        //check if his overall star == 0 first, if its 0, leave it like that
                        utc.alterUserRankingParameters(pc.get().getUser_id(), "save_unstar");
                    }
                    break;
                    
                    case "notstarred":
                    {
                        star = star + 1;
                        pc.get().setStar(star);
                        //increase the overall star of the owner of the post by 1
                        utc.alterUserRankingParameters(pc.get().getUser_id(), "save_star");
                    }
                    break;
                }
                
                pcr.save(pc.get());
                
                if(!cog.get().equals(""))
                {
                    ret = "redirect:/b_ch?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get()+"&cog="+cog.get();
                }
                else
                {
                    ret = "redirect:/b_ch?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get();
                }
            }
            break;
            
            case "cmt_s":
            {
                if(!utc.checkCommentBan())
                {
                    long userrank = utc.getUser().getUserrank();
                
                    if(userrank >= pc.get().getPostrank())
                    {
                        model.addAttribute("postclass", new PostClass());
                        model.addAttribute("pos", post_id.get());
                        model.addAttribute("cid", comment_id.get());
                        model.addAttribute("t", title.get());
                        model.addAttribute("p", pg.get());
                        model.addAttribute("page", commentPaginate.get());
                        
                        if(!cog.get().equals(""))
                        {
                            model.addAttribute("cog", cog.get());
                        }
                        ret = "pages/subcommentpage";
                    }
                    else
                    {
                        String alert = "Your rank is lesser than the post rank";
                        
                        if(!cog.get().equals(""))
                        {
                            ret = "redirect:/b_ch?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get()+"&cog="+cog.get();
                        }
                        else
                        {
                            ret = "redirect:/b_ch?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get()+"&alertx="+alert;
                        }
                    }
                }
                else
                {
                    String alert = "No access";
                    
                    if(!cog.get().equals(""))
                    {
                        ret = "redirect:/b_ch?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get()+"&cog="+cog.get();
                    }
                    else
                    {
                        ret = "redirect:/b_ch?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get()+"&alertx="+alert;
                    }
                }
            }
            break;
            
            case "qte_s":
            {
                if(!utc.checkCommentBan())
                {
                    long userrank = utc.getUser().getUserrank();
                
                    if(userrank >= pc.get().getPostrank())
                    {
                        Optional<CommentClass> ccid = ccr.findById(comment_id.get());
                        PostClass poc = new PostClass();
                        String quotedText = ccid.get().getContent();
                    
                        quotedText = quotedText.replaceAll("<br/><img alt='content image' width='250' height='150' src='/9jaforum/files/dist_img/", "<_");
                        quotedText = quotedText.replaceAll("'/><br/>", "_>");
                    
                        poc.setContent_2(quotedText);
                    
                        model.addAttribute("postclass", poc);
                    
                        model.addAttribute("pos", post_id.get());
                        model.addAttribute("cid", comment_id.get());
                        model.addAttribute("t", title.get());
                        model.addAttribute("p", pg.get());
                        model.addAttribute("page", commentPaginate.get());
                        
                        if(!cog.get().equals(""))
                        {
                            model.addAttribute("cog", cog.get());
                        }
                        ret = "pages/quotepage";
                    }
                    else
                    {
                        String alert = "Your rank is lesser than the post rank";
                        
                        if(!cog.get().equals(""))
                        {
                            ret = "redirect:/b_ch?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get()+"&cog="+cog.get();
                        }
                        else
                        {
                            ret = "redirect:/b_ch?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get()+"&alertx="+alert;
                        }
                    }
                }
                else
                {
                    String alert = "No access";
                    
                    if(!cog.get().equals(""))
                    {
                        ret = "redirect:/b_ch?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get()+"&cog="+cog.get();
                    }
                    else
                    {
                        ret = "redirect:/b_ch?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get()+"&alertx="+alert;
                    }
                }
            }
            break;
            
            case "lk_s":
            {
                Optional<CommentClass> cc = ccr.findById(comment_id.get());
                String check = crcr.likedBefore(comment_id.get(), id);  //has this user liked this comment before now?
                long likes = cc.get().getLikes();
                
                switch(check)
                {
                    case "":
                    {
                        likes = likes + 1;
                        cc.get().setLikes(likes);
                        utc.alterUserRankingParameters(cc.get().getUser_id(), "save_like");
                    }
                    break;
                    
                    case "liked":
                    {
                        likes = likes - 1;
                        cc.get().setLikes(likes);
                        utc.alterUserRankingParameters(cc.get().getUser_id(), "save_unlike");
                    }
                    break;
                    
                    case "unliked":
                    {
                        likes = likes + 1;
                        cc.get().setLikes(likes);
                        utc.alterUserRankingParameters(cc.get().getUser_id(), "save_like");
                    }
                    break;
                }
                
                ccr.save(cc.get());
                utc.alterCommentRankingParameters(cc);
                
                if(!cog.get().equals(""))
                {
                    ret = "redirect:/b_ch?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get()+"&cog="+cog.get();
                }
                else
                {
                    ret = "redirect:/b_ch?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get();
                }
            }
            break;
            
            case "flg_s":
            {
                Optional<CommentClass> cc = ccr.findById(comment_id.get());
                String check = crcr.redFlaggedBefore(comment_id.get(), id);  //has this user liked this comment before now?
                long redflag = cc.get().getRedflag();
                
                switch(check)
                {
                    case "":
                    {
                        redflag = redflag + 1;
                        cc.get().setRedflag(redflag);
                        utc.alterUserRankingParameters(cc.get().getUser_id(), "save_redflag");
                    }
                    break;
                    
                    case "redflagged":
                    {
                        redflag = redflag - 1;
                        cc.get().setRedflag(redflag);
                        utc.alterUserRankingParameters(cc.get().getUser_id(), "save_unredflag");
                    }
                    break;
                    
                    case "notredflagged":
                    {
                        redflag = redflag + 1;
                        cc.get().setRedflag(redflag);
                        utc.alterUserRankingParameters(cc.get().getUser_id(), "save_redflag");
                    }
                    break;
                }
                
                ccr.save(cc.get());
                utc.alterCommentRankingParameters(cc);
                
                if(!cog.get().equals(""))
                {
                    ret = "redirect:/b_ch?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get()+"&cog="+cog.get();
                }
                else
                {
                    ret = "redirect:/b_ch?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get();
                }
            }
            break;
            
            case "str_s":
            {
                Optional<CommentClass> cc = ccr.findById(comment_id.get());
                String check = crcr.starredBefore(comment_id.get(), id);  //has this user liked this comment before now?
                long star = cc.get().getStar();
                
                switch(check)
                {
                    case "":
                    {
                        star = star + 1;
                        cc.get().setStar(star);
                        utc.alterUserRankingParameters(cc.get().getUser_id(), "save_star");
                    }
                    break;
                    
                    case "starred":
                    {
                        star = star - 1;
                        cc.get().setStar(star);
                        utc.alterUserRankingParameters(cc.get().getUser_id(), "save_unstar");
                    }
                    break;
                    
                    case "notstarred":
                    {
                        star = star + 1;
                        cc.get().setStar(star);
                        utc.alterUserRankingParameters(cc.get().getUser_id(), "save_star");
                    }
                    break;
                }
                
                ccr.save(cc.get());
                utc.alterCommentRankingParameters(cc);
                
                if(!cog.get().equals(""))
                {
                    ret = "redirect:/b_ch?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get()+"&cog="+cog.get();
                }
                else
                {
                    ret = "redirect:/b_ch?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get();
                }
            }
            break;
            
            case "shr_s":
            {
                if(!utc.checkCommentBan())
                {
                    Optional<CommentClass> cc = ccr.findById(comment_id.get());
                    String check = crcr.sharedBefore(comment_id.get(), id);  //has this user liked this comment before now?
                    long shares = cc.get().getShare();
                
                    switch(check)
                    {
                        case "":
                        {
                            shares = shares + 1;
                            cc.get().setShare(shares);
                            utc.alterUserRankingParameters(cc.get().getUser_id(), "save_share");
                        }
                        break;
                    
                        case "shared":
                        {
                            shares = shares - 1;
                            cc.get().setShare(shares);
                            utc.alterUserRankingParameters(cc.get().getUser_id(), "save_unshare");
                        }
                        break;
                    
                        case "unshared":
                        {
                            shares = shares + 1;
                            cc.get().setShare(shares);
                            utc.alterUserRankingParameters(cc.get().getUser_id(), "save_share");
                        }
                        break;
                    }
                
                    ccr.save(cc.get());
                    utc.alterCommentRankingParameters(cc);
                    
                    if(!cog.get().equals(""))
                    {
                        ret = "redirect:/b_ch?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get()+"&cog="+cog.get();
                    }
                    else
                    {
                        ret = "redirect:/b_ch?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get();
                    }
                }
                else
                {
                    if(!cog.get().equals(""))
                    {
                        ret = "redirect:/b_ch?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get()+"&cog="+cog.get();
                    }
                    else
                    {
                        ret = "redirect:/b_ch?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get();
                    }
                }
            }
            break;
            
            case "edit_s":
            {
                if(!utc.checkCommentBan())
                {
                    Optional<CommentClass> cc = ccr.findById(comment_id.get());
                    String content = cc.get().getContent();
                
                    content=content.replaceAll("<br/><img alt='content image' width='250' height='150' src='/9jaforum/files/dist_img/", "<_");
                    content=content.replaceAll("'/><br/>", "_>");
                
                    if(utc.getUser().getId().equals(cc.get().getUser_id()))
                    {
                        PostClass pClass = new PostClass();
                        pClass.setContent(content);
                        model.addAttribute("postclass", pClass);
                        model.addAttribute("pos", post_id.get());
                        model.addAttribute("cid", comment_id.get());
                        model.addAttribute("t", title.get());
                        model.addAttribute("p", pg.get());
                        model.addAttribute("page", commentPaginate.get());
                        
                        if(!cog.get().equals(""))
                        {
                            model.addAttribute("cog", cog.get());
                        }
                        ret = "pages/editcommentpage";
                    }
                    else
                    {
                        String alert = "Cannot edit comment";
                        
                        if(!cog.get().equals(""))
                        {
                            ret = "redirect:/b_ch?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get()+"&cog="+cog.get()+"&alertx="+alert;
                        }
                        else
                        {
                            ret = "redirect:/b_ch?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get()+"&alertx="+alert;
                        }
                    }
                }
                else
                {
                    String alert = "No access";
                    
                    if(!cog.get().equals(""))
                    {
                        ret = "redirect:/b_ch?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get()+"&cog="+cog.get()+"&alertx="+alert;
                    }
                    else
                    {
                        ret = "redirect:/b_ch?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get()+"&alertx="+alert;
                    }
                }
            }
            break;
            
            case "dlt_s":
            {
                String alert;
                Optional<CommentClass> cc = ccr.findById(comment_id.get());
                
                if(utc.getUser().getId().equals(cc.get().getUser_id()))
                {
                    cc.get().setApproved(0);
                    ccr.save(cc.get());
                    alert = "Deleted";
                }
                else
                {
                    alert = "Cannot delete comment";
                }
                
                if(!cog.get().equals(""))
                {
                    ret = "redirect:/b_ch?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get()+"&cog="+cog.get()+"&alertx="+alert;
                }
                else
                {
                    ret = "redirect:/b_ch?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get()+"&alertx="+alert;
                }
            }
            break;
            
            case "dlt_sMod":
            {
                String alert;
                Optional<CommentClass> cc = ccr.findById(comment_id.get());
                
                if(utc.getUser().getColorclass().equals("user_mod"))
                {
                    cc.get().setApproved(0);
                    ccr.save(cc.get());
                    alert = "Deleted";
                }
                else
                {
                    alert = "Cannot delete comment";
                }
                
                if(!cog.get().equals(""))
                {
                    ret = "redirect:/b_ch?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get()+"&cog="+cog.get()+"&alertx="+alert;
                }
                else
                {
                    ret = "redirect:/b_ch?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get()+"&alertx="+alert;
                }
            }
            break;
            
            case "lk_x":
            {
                Optional<SubCommentClass> scc = sccr.findById(subcomment_id.get());
                String check = scrcr.likedBefore(subcomment_id.get(), id);  //has this user liked this subcomment before now?
                long likes = scc.get().getLikes();
                
                switch(check)
                {
                    case "":
                    {
                        likes = likes + 1;
                        scc.get().setLikes(likes);
                        utc.alterUserRankingParameters(scc.get().getUser_id(), "save_like");
                    }
                    break;
                    
                    case "liked":
                    {
                        likes = likes - 1;
                        scc.get().setLikes(likes);
                        utc.alterUserRankingParameters(scc.get().getUser_id(), "save_unlike");
                    }
                    break;
                    
                    case "unliked":
                    {
                        likes = likes + 1;
                        scc.get().setLikes(likes);
                        utc.alterUserRankingParameters(scc.get().getUser_id(), "save_like");
                    }
                    break;
                }
                
                sccr.save(scc.get());
                utc.alterSubCommentRankingParameters(scc, sccr);
                
                if(!cog.get().equals(""))
                {
                    ret = "redirect:/b_ch?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get()+"&cog="+cog.get();
                }
                else
                {
                    ret = "redirect:/b_ch?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get();
                }
            }
            break;
            
            case "flg_x":
            {
                Optional<SubCommentClass> scc = sccr.findById(subcomment_id.get());
                String check = scrcr.redFlaggedBefore(subcomment_id.get(), id);  //has this user liked this subcomment before now?
                long redflag = scc.get().getRedflag();
                
                switch(check)
                {
                    case "":
                    {
                        redflag = redflag + 1;
                        scc.get().setRedflag(redflag);
                        utc.alterUserRankingParameters(scc.get().getUser_id(), "save_redflag");
                    }
                    break;
                    
                    case "redflagged":
                    {
                        redflag = redflag - 1;
                        scc.get().setRedflag(redflag);
                        utc.alterUserRankingParameters(scc.get().getUser_id(), "save_unredflag");
                    }
                    break;
                    
                    case "notredflagged":
                    {
                        redflag = redflag + 1;
                        scc.get().setRedflag(redflag);
                        utc.alterUserRankingParameters(scc.get().getUser_id(), "save_redflag");
                    }
                    break;
                }
                
                sccr.save(scc.get());
                utc.alterSubCommentRankingParameters(scc, sccr);
                
                if(!cog.get().equals(""))
                {
                    ret = "redirect:/b_ch?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get()+"&cog="+cog.get();
                }
                else
                {
                    ret = "redirect:/b_ch?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get();
                }
            }
            break;
            
            case "str_x":
            {
                Optional<SubCommentClass> scc = sccr.findById(subcomment_id.get());
                String check = scrcr.starredBefore(subcomment_id.get(), id);  //has this user liked this subcomment before now?
                long starred = scc.get().getStar();
                
                switch(check)
                {
                    case "":
                    {
                        starred = starred + 1;
                        scc.get().setStar(starred);
                        utc.alterUserRankingParameters(scc.get().getUser_id(), "save_star");
                    }
                    break;
                    
                    case "starred":
                    {
                        starred = starred - 1;
                        scc.get().setStar(starred);
                        utc.alterUserRankingParameters(scc.get().getUser_id(), "save_unstar");
                    }
                    break;
                    
                    case "notstarred":
                    {
                        starred = starred + 1;
                        scc.get().setStar(starred);
                        utc.alterUserRankingParameters(scc.get().getUser_id(), "save_star");
                    }
                    break;
                }
                
                sccr.save(scc.get());
                utc.alterSubCommentRankingParameters(scc, sccr);
                
                if(!cog.get().equals(""))
                {
                    ret = "redirect:/b_ch?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get()+"&cog="+cog.get();
                }
                else
                {
                    ret = "redirect:/b_ch?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get();
                }
            }
            break;
            
            case "edit_x":
            {
                Optional<SubCommentClass> scc = sccr.findById(subcomment_id.get());
                String content = scc.get().getContent();
                
                content=content.replaceAll("<br/><img alt='content image' width='250' height='150' src='/9jaforum/files/dist_img/", "<_");
                content=content.replaceAll("'/><br/>", "_>");
                
                if(utc.getUser().getId().equals(scc.get().getUser_id()))
                {
                    PostClass pClass = new PostClass();
                    pClass.setContent(content);
                    model.addAttribute("postclass", pClass);
                    model.addAttribute("pos", post_id.get());
                    model.addAttribute("sid", subcomment_id.get());
                    model.addAttribute("cid", comment_id.get());
                    model.addAttribute("t", title.get());
                    model.addAttribute("p", pg.get());
                    model.addAttribute("page", commentPaginate.get());
                    
                    if(!cog.get().equals(""))
                    {
                        model.addAttribute("cog", cog.get());
                    }
                    ret = "pages/editsubcommentpage";
                }
                else
                {
                    String alert = "Cannot edit comment";
                    
                    if(!cog.get().equals(""))
                    {
                        ret = "redirect:/b_ch?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get()+"&cog="+cog.get()+"&alertx="+alert;
                    }
                    else
                    {
                        ret = "redirect:/b_ch?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get()+"&alertx="+alert;
                    }
                }
            }
            break;
            
            case "dlt_x":
            {
                String alert;
                Optional<SubCommentClass> scc = sccr.findById(subcomment_id.get());
                
                if(utc.getUser().getId().equals(scc.get().getUser_id()))
                {
                    scc.get().setApproved(0);
                    sccr.save(scc.get());
                    alert = "Deleted";
                }
                else
                {
                    alert = "Cannot delete comment";
                }
                
                if(!cog.get().equals(""))
                {
                    ret = "redirect:/b_ch?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get()+"&cog="+cog.get()+"&alertx="+alert;
                }
                else
                {
                    ret = "redirect:/b_ch?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get()+"&alertx="+alert;
                }
            }
            break;
            
            case "dlt_xMod":
            {
                String alert;
                Optional<SubCommentClass> scc = sccr.findById(subcomment_id.get());
                
                if(utc.getUser().getColorclass().equals("user_mod"))
                {
                    scc.get().setApproved(0);
                    sccr.save(scc.get());
                    alert = "Deleted";
                }
                else
                {
                    alert = "Cannot delete comment";
                }
                
                if(!cog.get().equals(""))
                {
                    ret = "redirect:/b_ch?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get()+"&cog="+cog.get()+"&alertx="+alert;
                }
                else
                {
                    ret = "redirect:/b_ch?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get()+"&alertx="+alert;
                }
            }
            break;
        }
        return ret;
    }
    
    @PostMapping("cmtpost")
    public String cmtPost(@RequestParam("pos")Optional<Long> post_id, @RequestParam("t")Optional<String> title, 
    @RequestParam("p") Optional<Integer> pg, ModelMap model, @RequestParam("page")Optional<Integer> commentPaginate, 
    @ModelAttribute("postclass")PostClass pc, RedirectAttributes ra, @RequestParam("akt")Optional<String> action, 
    @RequestParam("cid")Optional<Long> comment_id, @RequestParam("sid")Optional<Long> subcomment_id, 
    @RequestParam("cog")Optional<String> cog)
    {
        aac.displayAdvert(model);   //This line is for adverts
        
        if(utc.checkCommentBan())
        {
            //It won't come to this but just leave am there
            //Do not forget to add provisions for those banned etc, they should not be able to write post, comment etc
            return "redirect:/";
        }
        
        String ret = null;
        String path = utc.getFilePath()+"dist_img";
        String date = utc.getDate();
        String content = pc.getContent().trim();
        
        switch(action.orElse("cmt"))
        {
            case "cmt":
            {
                switch(pc.getActionButton())
                {
                    case "add_img":
                    {
                        model.addAttribute("pos", post_id.get());
                        model.addAttribute("t", title.get());
                        model.addAttribute("p", pg.get());
                        model.addAttribute("page", commentPaginate.get());
                        
                        if(!cog.get().equals(""))
                        {
                            model.addAttribute("cog", cog.get());
                        }
                
                        pc.setContent(content);
                
                        MultipartFile contentFile =  pc.getContentFile();
                        if(contentFile.isEmpty())
                        {
                            model.addAttribute("alert", "No image file selected");
                        }
                        else if(contentFile.getSize() > 0 && contentFile.getSize() <= 4000000)
                        {
                            String contentFileName = contentFile.getOriginalFilename();
                            if(contentFileName != null && contentFileName.length() <= 50)
                            {
                                if(contentFileName.endsWith(".jpg") || contentFileName.endsWith(".png") || contentFileName.endsWith(".gif") 
                                || contentFileName.endsWith(".jpeg") || contentFileName.endsWith(".JPG") || contentFileName.endsWith(".PNG") 
                                || contentFileName.endsWith(".GIF") || contentFileName.endsWith(".JPEG") || contentFileName.endsWith(".webp") 
                                || contentFileName.endsWith(".WEBP"))
                                {
                                    try
                                    {
                                        String imageRef="<_" + contentFileName + "_>";
                                        pc.setContent(content + imageRef);
                                
                                        model.addAttribute("alert", "Image added to content");
                                        File pathToFile=new File(path, contentFileName);
                                        contentFile.transferTo(pathToFile);
                                    }
                                    catch (IllegalStateException | IOException ex) 
                                    {
                                        Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                                else
                                {
                                    model.addAttribute("alert", "Invalid image format (suported: jpg, png, gif, webp)");
                                }
                            }
                            else
                            {
                                model.addAttribute("alert", "Content image name too long (less than 50 characters)");
                            }
                        }
                        else if(contentFile.getSize() == 0)
                        {
                            model.addAttribute("alert", "");
                        }
                        else
                        {
                            model.addAttribute("alert", "File size exceeded (4MB or less)");
                        }
                    }
                    break;
            
                    case "post":
                    {
                        pc.setContent(content);
                
                        if(!utc.checkTag(content))  //Arrange this well
                        {
                            model.addAttribute("pos", post_id.get());
                            model.addAttribute("t", title.get());
                            model.addAttribute("p", pg.get());
                            model.addAttribute("page", commentPaginate.get());
                            model.addAttribute("alert", "Some tags are not properly closed [<_ must end with _>]");
                            
                            if(!cog.get().equals(""))
                            {
                                model.addAttribute("cog", cog.get());
                            }
                            return "pages/commentpage";
                        }
                        else
                        {
                            content=content.replaceAll("<_", "<br/><img alt='content image' width='250' height='150' src='/9jaforum/files/dist_img/");
                            content=content.replaceAll("_>", "'/><br/>");
                        
                            if(!content.matches("\\s*"))
                            {
                                if(content.length() > 1500)
                                {
                                    model.addAttribute("pos", post_id.get());
                                    model.addAttribute("t", title.get());
                                    model.addAttribute("p", pg.get());
                                    model.addAttribute("page", commentPaginate.get());
                                    model.addAttribute("alert", "Comment must be less than 1500 characters [" + pc.getContent().length() +"]");
                                
                                    if(!cog.get().equals(""))
                                    {
                                        model.addAttribute("cog", cog.get());
                                    }
                                }
                                else
                                {
                                    CommentClass cc = new CommentClass(utc.getUser().getId(), post_id.get(), content, date);
                                    ccr.save(cc);
                                
                                    if(!cog.get().equals(""))
                                    {
                                        return "redirect:/b_ch?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get()+"&cog="+cog.get()+"&alertx=Posted";
                                    }
                                    else
                                    {
                                        return "redirect:/b_ch?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get()+"&alertx=Posted";
                                    }
                                }
                            }
                            else
                            {
                                ra.addFlashAttribute("alertx", "Comment cannot be empty");
                            }
                        }
                    }
                    break;
                }
                ret = "pages/commentpage";
            }
            break;
            
            case "cmt_sub":
            {
                switch(pc.getActionButton())
                {
                    case "add_img":
                    {
                        model.addAttribute("pos", post_id.get());
                        model.addAttribute("cid", comment_id.get());
                        model.addAttribute("t", title.get());
                        model.addAttribute("p", pg.get());
                        model.addAttribute("page", commentPaginate.get());
                        
                        if(!cog.get().equals(""))
                        {
                            model.addAttribute("cog", cog.get());
                        }
                
                        pc.setContent(content);
                
                        MultipartFile contentFile =  pc.getContentFile();
                        if(contentFile.isEmpty())
                        {
                            model.addAttribute("alert", "No image file selected");
                        }
                        else if(contentFile.getSize() > 0 && contentFile.getSize() <= 4000000)
                        {
                            String contentFileName = contentFile.getOriginalFilename();
                            if(contentFileName != null && contentFileName.length() <= 50)
                            {
                                if(contentFileName.endsWith(".jpg") || contentFileName.endsWith(".png") || contentFileName.endsWith(".gif") 
                                || contentFileName.endsWith(".jpeg") || contentFileName.endsWith(".JPG") || contentFileName.endsWith(".PNG") 
                                || contentFileName.endsWith(".GIF") || contentFileName.endsWith(".JPEG") || contentFileName.endsWith(".webp") 
                                || contentFileName.endsWith(".WEBP"))
                                {
                                    try
                                    {
                                        String imageRef="<_" + contentFileName + "_>";
                                        pc.setContent(content + imageRef);
                                
                                        model.addAttribute("alert", "Image added to content");
                                        File pathToFile=new File(path, contentFileName);
                                        contentFile.transferTo(pathToFile);
                                    }
                                    catch (IllegalStateException | IOException ex) 
                                    {
                                        Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                                else
                                {
                                    model.addAttribute("alert", "Invalid image format (suported: jpg, png, gif, webp)");
                                }
                            }
                            else
                            {
                                model.addAttribute("alert", "Content image name too long (less than 50 characters)");
                            }
                        }
                        else if(contentFile.getSize() == 0)
                        {
                            model.addAttribute("alert", "");
                        }
                        else
                        {
                            model.addAttribute("alert", "File size exceeded (4MB or less)");
                        }
                    }
                    break;
            
                    case "post":
                    {
                        pc.setContent(content);
                
                        if(!utc.checkTag(content))  //Arrange this well
                        {
                            model.addAttribute("pos", post_id.get());
                            model.addAttribute("cid", comment_id.get());
                            model.addAttribute("t", title.get());
                            model.addAttribute("p", pg.get());
                            model.addAttribute("page", commentPaginate.get());
                            model.addAttribute("alert", "Some tags are not properly closed [<_ must end with _>]");
                            
                            if(!cog.get().equals(""))
                            {
                                model.addAttribute("cog", cog.get());
                            }
                            return "pages/subcommentpage";
                        }
                        else
                        {
                            content=content.replaceAll("<_", "<br/><img alt='content image' width='250' height='150' src='/9jaforum/files/dist_img/");
                            content=content.replaceAll("_>", "'/><br/>");
                
                            if(!content.matches("\\s*"))
                            {
                                if(content.length() > 1500)
                                {
                                    model.addAttribute("pos", post_id.get());
                                    model.addAttribute("cid", comment_id.get());
                                    model.addAttribute("t", title.get());
                                    model.addAttribute("p", pg.get());
                                    model.addAttribute("page", commentPaginate.get());
                                    model.addAttribute("alert", "Comment must be less than 1500 characters [" + pc.getContent().length() +"]");
                                
                                    if(!cog.get().equals(""))
                                    {
                                        model.addAttribute("cog", cog.get());
                                    }
                                }
                                else
                                {
                                    Optional<CommentClass> cc = ccr.findById(comment_id.get());  //Comment you are subcommenting on
                                    SubCommentClass scc;
                                
                                    if(utc.getUser().getId().equals(cc.get().getUser_id()))  //No need marking your comment as unread na, abi no be you write am??
                                    {
                                        scc = new SubCommentClass(utc.getUser().getId(), comment_id.get(), content, date, "read");
                                    }
                                    else
                                    {
                                        scc = new SubCommentClass(utc.getUser().getId(), comment_id.get(), content, date);
                                    }
                                
                                    sccr.save(scc);
                                
                                    //Notify the owner of the comment here: Very important
                                    String postlink = "s_ch?pos="+post_id.get()+"&t="+title.get()+"&p="+pg.get()+"&cid="+comment_id.get()+"#"+comment_id.get();
                                    utc.updateInbox(cc.get().getUser_id(), comment_id.get(), postlink);
                                
                                    if(!cog.get().equals(""))
                                    {
                                        return "redirect:/b_ch?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get()+"&cog="+cog.get()+"&alertx=Posted";
                                    }
                                    else
                                    {
                                        return "redirect:/b_ch?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get()+"&alertx=Posted";
                                    }
                                }
                            }
                            else
                            {
                                ra.addFlashAttribute("alertx", "Comment cannot be empty");
                            }
                        }
                    }
                    break;
                }
                ret = "pages/subcommentpage";
            }
            break;
                
            case "qte_sub":
            {
                String content_2 = pc.getContent_2().trim();
                switch(pc.getActionButton())
                {
                    case "add_img":
                    {
                        model.addAttribute("pos", post_id.get());
                        model.addAttribute("cid", comment_id.get());
                        model.addAttribute("t", title.get());
                        model.addAttribute("p", pg.get());
                        model.addAttribute("page", commentPaginate.get());
                
                        if(!cog.get().equals(""))
                        {
                            model.addAttribute("cog", cog.get());
                        }
                        pc.setContent_2(content_2);
                        pc.setContent(content);
                
                        MultipartFile contentFile =  pc.getContentFile();
                        if(contentFile.isEmpty())
                        {
                            model.addAttribute("alert", "No image file selected");
                        }
                        else if(contentFile.getSize() > 0 && contentFile.getSize() <= 4000000)
                        {
                            String contentFileName = contentFile.getOriginalFilename();
                            if(contentFileName != null && contentFileName.length() <= 50)
                            {
                                if(contentFileName.endsWith(".jpg") || contentFileName.endsWith(".png") || contentFileName.endsWith(".gif") 
                                || contentFileName.endsWith(".jpeg") || contentFileName.endsWith(".JPG") || contentFileName.endsWith(".PNG") 
                                || contentFileName.endsWith(".GIF") || contentFileName.endsWith(".JPEG") || contentFileName.endsWith(".webp") 
                                || contentFileName.endsWith(".WEBP"))
                                {
                                    try
                                    {
                                        String imageRef="<_" + contentFileName + "_>";
                                        pc.setContent(content + imageRef);
                                
                                        model.addAttribute("alert", "Image added to content");
                                        File pathToFile=new File(path, contentFileName);
                                        contentFile.transferTo(pathToFile);
                                    }
                                    catch (IllegalStateException | IOException ex) 
                                    {
                                        Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                                else
                                {
                                    model.addAttribute("alert", "Invalid image format (suported: jpg, png, gif, webp)");
                                }
                            }
                            else
                            {
                                model.addAttribute("alert", "Content image name too long (less than 50 characters)");
                            }
                        }
                        else if(contentFile.getSize() == 0)
                        {
                            model.addAttribute("alert", "");
                        }
                        else
                        {
                            model.addAttribute("alert", "File size exceeded (4MB or less)");
                        }
                    }
                    break;
            
                    case "post":
                    {
                        pc.setContent_2(content_2);
                        pc.setContent(content);
                
                        if(!utc.checkTag(content))
                        {
                            model.addAttribute("pos", post_id.get());
                            model.addAttribute("cid", comment_id.get());
                            model.addAttribute("t", title.get());
                            model.addAttribute("p", pg.get());
                            model.addAttribute("page", commentPaginate.get());
                            model.addAttribute("alert", "Some tags in textarea 2 are not properly closed [<_ must end with _>]");
                
                            if(!cog.get().equals(""))
                            {
                                model.addAttribute("cog", cog.get());
                            }
                            
                            return "pages/quotepage";
                        }
                        else
                        {
                            content=content.replaceAll("<_", "<br/><img alt='content image' width='250' height='150' src='/9jaforum/files/dist_img/");
                            content=content.replaceAll("_>", "'/><br/>");
                
                            if(!content.matches("\\s*"))
                            {
                                model.addAttribute("pos", post_id.get());
                                model.addAttribute("cid", comment_id.get());
                                model.addAttribute("t", title.get());
                                model.addAttribute("p", pg.get());
                                model.addAttribute("page", commentPaginate.get());
                            
                                if(!cog.get().equals(""))
                                {
                                    model.addAttribute("cog", cog.get());
                                }    
                                if(content.length() > 1500)
                                {
                                    model.addAttribute("alert", "Comment must be less than 1500 characters [" + pc.getContent().length() +"]");
                                }
                                else
                                {
                                    Optional<CommentClass> ccid = ccr.findById(comment_id.get());
                                    String main_comment = ccid.get().getContent();
                                
                                    if(!utc.checkTag(content_2))
                                    {
                                        model.addAttribute("pos", post_id.get());
                                        model.addAttribute("cid", comment_id.get());
                                        model.addAttribute("t", title.get());
                                        model.addAttribute("p", pg.get());
                                        model.addAttribute("page", commentPaginate.get());
                                        model.addAttribute("alert", "Some tags in textarea 1 are not properly closed [<_ must end with _>]");
                                        
                                        if(!cog.get().equals(""))
                                        {
                                            model.addAttribute("cog", cog.get());
                                        }
                            
                                        return "pages/quotepage";
                                    }
                                    else
                                    {
                                        content_2=content_2.replaceAll("<_", "<br/><img alt='content image' width='250' height='150' src='/9jaforum/files/dist_img/");
                                        content_2=content_2.replaceAll("_>", "'/><br/>");
                                        
                                        if(main_comment.contains(content_2))
                                        {
                                            CommentClass cc = new CommentClass(utc.getUser().getId(), post_id.get(), content, date);
                                            ccr.save(cc);
                                            Optional<CommentClass> quickCom = ccr.getExactPost(utc.getUser().getId(), post_id.get(), date);
                                            QuoteObject qobj = new QuoteObject(ccid.get().getUser_id(), quickCom.get().getId(), content_2, ccid.get().getPostdate());
                                            qobjr.save(qobj);
                                            
                                            /*  
                                            //Notify the owner of the comment here: Very important
                                            Optional<CommentClass> cLass = ccr.findById(comment_id.get());
                                            String postlink = "s_ch?pos="+post_id.get()+"&t="+title.get()+"&p="+pg.get()+"&cid="+comment_id.get()+"#"+comment_id.get();
                                            utc.updateInbox(mobjr, cLass.get().getUser_id(), comment_id.get(), postlink);
                                            */
                                    
                                            if(!cog.get().equals(""))
                                            {
                                                return "redirect:/b_ch?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get()+"&cog="+cog.get()+"&alertx=Posted";
                                            }
                                            else
                                            {
                                                return "redirect:/b_ch?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get()+"&alertx=Posted";
                                            }
                                        }
                                        else
                                        {
                                            //wrong quote
                                            model.addAttribute("alert", "You did not quote @"+ ccid.get().getUsertwo().getUsername() + " properly");
                                        }
                                    }
                                }
                            }
                            else
                            {
                                ra.addFlashAttribute("alertx", "Comment cannot be empty");
                            }
                        }
                    }
                    break;
                }
                ret = "pages/quotepage";
            }
            break;
            
            case "edit_cmt":
            {
                switch(pc.getActionButton())
                {
                    case "add_img":
                    {
                        model.addAttribute("pos", post_id.get());
                        model.addAttribute("cid", comment_id.get());
                        model.addAttribute("t", title.get());
                        model.addAttribute("p", pg.get());
                        model.addAttribute("page", commentPaginate.get());
                
                        if(!cog.get().equals(""))
                        {
                            model.addAttribute("cog", cog.get());
                        }
                        pc.setContent(content);
                
                        MultipartFile contentFile =  pc.getContentFile();
                        if(contentFile.isEmpty())
                        {
                            model.addAttribute("alert", "No image file selected");
                        }
                        else if(contentFile.getSize() > 0 && contentFile.getSize() <= 4000000)
                        {
                            String contentFileName = contentFile.getOriginalFilename();
                            if(contentFileName != null && contentFileName.length() <= 50)
                            {
                                if(contentFileName.endsWith(".jpg") || contentFileName.endsWith(".png") || contentFileName.endsWith(".gif") 
                                || contentFileName.endsWith(".jpeg") || contentFileName.endsWith(".JPG") || contentFileName.endsWith(".PNG") 
                                || contentFileName.endsWith(".GIF") || contentFileName.endsWith(".JPEG") || contentFileName.endsWith(".webp") 
                                || contentFileName.endsWith(".WEBP"))
                                {
                                    try
                                    {
                                        String imageRef="<_" + contentFileName + "_>";
                                        pc.setContent(content + imageRef);
                                
                                        model.addAttribute("alert", "Image added to content");
                                        File pathToFile=new File(path, contentFileName);
                                        contentFile.transferTo(pathToFile);
                                    }
                                    catch (IllegalStateException | IOException ex) 
                                    {
                                        Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                                else
                                {
                                    model.addAttribute("alert", "Invalid image format (suported: jpg, png, gif, webp)");
                                }
                            }
                            else
                            {
                                model.addAttribute("alert", "Content image name too long (less than 50 characters)");
                            }
                        }
                        else if(contentFile.getSize() == 0)
                        {
                            model.addAttribute("alert", "");
                        }
                        else
                        {
                            model.addAttribute("alert", "File size exceeded (4MB or less)");
                        }
                    }
                    break;
            
                    case "post":
                    {
                        pc.setContent(content);
                
                        if(!utc.checkTag(content))
                        {
                            model.addAttribute("pos", post_id.get());
                            model.addAttribute("cid", comment_id.get());
                            model.addAttribute("t", title.get());
                            model.addAttribute("p", pg.get());
                            model.addAttribute("page", commentPaginate.get());
                            model.addAttribute("alert", "Some tags are not properly closed [<_ must end with _>]");
                            
                            if(!cog.get().equals(""))
                            {
                                model.addAttribute("cog", cog.get());
                            }
                        }
                        else
                        {
                            content=content.replaceAll("<_", "<br/><img alt='content image' width='250' height='150' src='/9jaforum/files/dist_img/");
                            content=content.replaceAll("_>", "'/><br/>");
                
                            if(!content.matches("\\s*"))
                            {
                                if(content.length() > 1500)
                                {
                                    model.addAttribute("pos", post_id.get());
                                    model.addAttribute("cid", comment_id.get());
                                    model.addAttribute("t", title.get());
                                    model.addAttribute("p", pg.get());
                                    model.addAttribute("page", commentPaginate.get());
                                    model.addAttribute("alert", "Comment must be less than 1500 characters [" + pc.getContent().length() +"]");
                                
                                    if(!cog.get().equals(""))
                                    {
                                        model.addAttribute("cog", cog.get());
                                    }
                                }
                                else
                                {
                                    Optional<CommentClass> ccObj = ccr.findById(comment_id.get());
                                    ccObj.get().setContent(content);
                                    ccr.save(ccObj.get());
                                
                                    if(!cog.get().equals(""))
                                    {
                                        return "redirect:/b_ch?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get()+"&cog="+cog.get()+"&alertx=Posted";
                                    }
                                    else
                                    {
                                        return "redirect:/b_ch?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get()+"&alertx=Posted";
                                    }
                                }
                            }
                            else
                            {
                                ra.addFlashAttribute("alertx", "Comment cannot be empty");
                            }
                        }
                    }
                    break;
                }
                ret = "pages/editcommentpage";
            }
            break;
            
            case "edit_subcmt":
            {
                switch(pc.getActionButton())
                {
                    case "add_img":
                    {
                        model.addAttribute("pos", post_id.get());
                        model.addAttribute("sid", subcomment_id.get());
                        model.addAttribute("cid", comment_id.get());
                        model.addAttribute("t", title.get());
                        model.addAttribute("p", pg.get());
                        model.addAttribute("page", commentPaginate.get());
                
                        pc.setContent(content);
                
                        MultipartFile contentFile =  pc.getContentFile();
                        if(contentFile.isEmpty())
                        {
                            model.addAttribute("alert", "No image file selected");
                        }
                        else if(contentFile.getSize() > 0 && contentFile.getSize() <= 4000000)
                        {
                            String contentFileName = contentFile.getOriginalFilename();
                            if(contentFileName != null && contentFileName.length() <= 50)
                            {
                                if(contentFileName.endsWith(".jpg") || contentFileName.endsWith(".png") || contentFileName.endsWith(".gif") 
                                || contentFileName.endsWith(".jpeg") || contentFileName.endsWith(".JPG") || contentFileName.endsWith(".PNG") 
                                || contentFileName.endsWith(".GIF") || contentFileName.endsWith(".JPEG") || contentFileName.endsWith(".webp") 
                                || contentFileName.endsWith(".WEBP"))
                                {
                                    try
                                    {
                                        String imageRef="<_" + contentFileName + "_>";
                                        pc.setContent(content + imageRef);
                                
                                        model.addAttribute("alert", "Image added to content");
                                        File pathToFile=new File(path, contentFileName);
                                        contentFile.transferTo(pathToFile);
                                    }
                                    catch (IllegalStateException | IOException ex) 
                                    {
                                        Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                                else
                                {
                                    model.addAttribute("alert", "Invalid image format (suported: jpg, png, gif, webp)");
                                }
                            }
                            else
                            {
                                model.addAttribute("alert", "Content image name too long (less than 50 characters)");
                            }
                        }
                        else if(contentFile.getSize() == 0)
                        {
                            model.addAttribute("alert", "");
                        }
                        else
                        {
                            model.addAttribute("alert", "File size exceeded (4MB or less)");
                        }
                    }
                    break;
            
                    case "post":
                    {
                        pc.setContent(content);
                
                        content=content.replaceAll("<_", "<br/><img alt='content image' width='250' height='150' src='/9jaforum/files/dist_img/");
                        content=content.replaceAll("_>", "'/><br/>");
                
                        if(!content.matches("\\s*"))
                        {
                            if(content.length() > 1500)
                            {
                                model.addAttribute("pos", post_id.get());
                                model.addAttribute("sid", subcomment_id.get());
                                model.addAttribute("cid", comment_id.get());
                                model.addAttribute("t", title.get());
                                model.addAttribute("p", pg.get());
                                model.addAttribute("page", commentPaginate.get());
                        
                                model.addAttribute("alert", "Comment must be less than 1500 characters [" + pc.getContent().length() +"]");
                            }
                            else
                            {
                                Optional<SubCommentClass> sccObj = sccr.findById(subcomment_id.get());
                                sccObj.get().setContent(content);
                                sccr.save(sccObj.get());
                                return "redirect:/b_ch?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get()+"&alertx=Posted";
                            }
                        }
                        else
                        {
                            ra.addFlashAttribute("alertx", "Comment cannot be empty");
                        }
                    }
                    break;
                }
                ret = "pages/editsubcommentpage";
            }
            break;
        }
        return ret;
    }
    
    @GetMapping("/flpost")
    public String getFollowedPost(@RequestParam("pg")Optional<Integer> page, HttpServletRequest req, 
    ModelMap model, RedirectAttributes ra)
    {
        aac.displayAdvert(model);   //This line is for adverts
        
        int init = 0;
        int end = 10;
        
        if(page.get() > 1)
        {
            init = (page.get() - 1) * end;
            end = end * page.get();
        }
        
        String[] hideBlocks = {"secondBlock"};
        utc.dispBlock(model, "firstBlock", hideBlocks);
        utc.userModel(model);
        
        List<PostClass> pcList3 = new LinkedList<>();
        List<PostClass> pcList4 = new LinkedList<>();   //very important
        List<String> readOnly = new LinkedList<>();
        
        List<FollowerObject> followedObj = fobjr.getSelectedFollow(utc.getUser().getId()); //Are you following people? Oya get their ids
        if(!followedObj.isEmpty())  //If you really are following someone
        {
            List<PostClass> followPost = pcr.followersPost(followedObj);    //Get the followed posts
            if(followPost != null)  //If there is really a followed post
            {
                for(int count = 0; count < followPost.size(); count++)
                {
                    boolean delete = fpdor.getDeletedPostObject(followPost.get(count).getId(), utc.getUser().getId());
                    
                    if(!delete)
                    {
                        pcList3.add(followPost.get(count));    //So, pcList3 is the undeleted followed post
                    }
                }
                
                if(pcList3.size() < end)
                {
                    end = pcList3.size();
                }
                
                for(int count = init; count < end; count++)
                {
                    pcList4.add(pcList3.get(count)); //and pcList4 is the sublist of pcList3 based on pagination
                }
                
                boolean read;
                
                for(int count = 0; count < pcList4.size(); count++)
                {
                    read = fpdor.getReadPostObject(pcList4.get(count).getId(), utc.getUser().getId());
                    
                    if(read)
                    {
                        readOnly.add("readFollow");
                    }
                    else
                    {
                        readOnly.add("unreadFollow");
                    }
                }
                
                
                model.addAttribute("followpost", pcList4);
                model.addAttribute("readFollowPost", readOnly);
                
                model.addAttribute("pgn", page.get());
                model.addAttribute("prev", page.get()-1);
                model.addAttribute("next", page.get()+1);
                
                if((page.get()-1) == 0)
                {
                    model.addAttribute("disp1", "none");
                }
                if(pcList4.isEmpty())
                {
                    model.addAttribute("nopostavailable", "No post available");
                    model.addAttribute("disp2", "none");
                    model.addAttribute("theclass", "realcentertinz");
                }
            }
            else    //If there is no followed post
            {
                model.addAttribute("nothing", "realcentertinz");
                model.addAttribute("alertFallback", "No post available");
            }
        }
        else    //If you do not have followers
        {
            model.addAttribute("nothing", "realcentertinz");
            model.addAttribute("alertFallback", "You really are not following anyone at the moment");
        }
        return "pages/followedpost";
    }
    
    @GetMapping("/notf")
    public String getNotification(ModelMap model, @RequestParam("pg")Optional<Integer> page,
    RedirectAttributes ra, HttpServletRequest req)
    {
        aac.displayAdvert(model);   //This line is for adverts
        
        utc.userModel(model);
        int init = 0;
        int end = 5;
        List<MessageObject> mobj = new LinkedList<>();
        
        if(page.get() > 1)
        {
            init = (page.get() - 1) * end;
            end = end * page.get();
        }
        
        List<MessageObject> mo = mobjr.getMyMessage(utc.getUser().getId());   //Get all followed posts
        
        if(!mo.isEmpty())  //If there are messages
        {
            if(mo.size() < end)
            {
                end = mo.size();
            }
            for(int count = init; count < end; count++)
            {
                mobj.add(mo.get(count));
            }
            
            model.addAttribute("messageobj", mobj);
            model.addAttribute("pgn", page.get());  //Key
            model.addAttribute("prev", page.get()-1);
            model.addAttribute("next", page.get()+1);
                
            if(page.get() - 1 == 0)
            {
                model.addAttribute("disp1", "none");
            }
            if(mobj.isEmpty())
            {
                model.addAttribute("nonotifications", "No notifications");
                model.addAttribute("disp2", "none");
                model.addAttribute("theclass", "realcentertinz");
            }
        }
        else    //If there are no messages
        {
            model.addAttribute("nothing", "realcentertinz");
            model.addAttribute("alertFallback", "No notifications");
            
        }
        return "pages/notificationpage";
    }
    
    @GetMapping("/rcd")
    public String getRecord(ModelMap model, @RequestParam("pg")Optional<Integer> pgn, 
    RedirectAttributes ra, HttpServletRequest req)
    {
        aac.displayAdvert(model);   //This line is for adverts
        utc.userModel(model);
        int init = 0;
        int end = 5;
        List<PostClass> tobj = new LinkedList<>();
        
        if(pgn.get() > 1)
        {
            init = (pgn.get() - 1) * end;
            end = end * pgn.get();
        }
        List<PostClass> trend =  pcr.getAllMyPost(utc.getUser().getId());   //Get all my posts
        
        if(!trend.isEmpty())  //If there are messages
        {
            if(trend.size() < end)
            {
                end = trend.size();
            }
            for(int count = init; count < end; count++)
            {
                tobj.add(trend.get(count));
            }
            
            model.addAttribute("trendobj", tobj);
            model.addAttribute("pgn", pgn.get());   //For reading all your posts both approved and dispproved
            
            model.addAttribute("prev", pgn.get()-1);
            model.addAttribute("next", pgn.get()+1);
                
            if(pgn.get() - 1 == 0)
            {
                model.addAttribute("disp1", "none");
            }
            if(tobj.isEmpty())
            {
                model.addAttribute("nopostavailable", "No post available");
                model.addAttribute("disp2", "none");
                model.addAttribute("theclass", "realcentertinz");
            }
        }
        else    //If there are no messages
        {
            model.addAttribute("nothing", "realcentertinz");
            model.addAttribute("alertFallback", "You are yet to make a post");
        }
        
        return "pages/mytrend";
    }
    
    @GetMapping("/dlt_")
    public String deleteRecord(@RequestParam("pos")Optional<Long> post_id, @RequestParam("pgn")Optional<Long> pgn,
    @RequestParam("akt")Optional<String> action, RedirectAttributes ra, HttpServletRequest req, ModelMap model, 
    @RequestParam("cid")Optional<Long> commentid)
    {
        aac.displayAdvert(model);   //This line is for adverts
        
        String ret = null;
        
        switch (action.get())
        {
            case "dlt_pt":
            {
                Optional<PostClass> pc = pcr.findById(post_id.get());
        
                if(utc.getUser().getId().equals(pc.get().getUser_id()))
                {
                    pc.get().setFlag(0);
                    pcr.save(pc.get());
                }
                else
                {
                    ra.addFlashAttribute("alert", "Cannot execute command");
                }
                ret = "redirect:/user/rcd?pg="+pgn.get();
            }
            break;
            
            case "dlt_cmt":
            {
                Optional<MessageObject> mo = mobjr.findByCommentid(commentid.get());
        
                if(utc.getUser().getId().equals(mo.get().getRecipient_id()))
                {
                    mo.get().setFlag(1);
                    mobjr.save(mo.get());
                }
                else
                {
                    ra.addFlashAttribute("alert", "Cannot execute command");
                }
                ret = "redirect:/user/notf?pg="+pgn.get();
            }
            break;
            
            case "dlt_ft":
            {
                Optional<FollowedPostDeleteObject> fpdobj = fpdor.getDeletedPost(post_id.get(), utc.getUser().getId());
                
                if(fpdobj.orElse(null) != null)
                {
                    fpdobj.get().setFlagDelete(1);
                    fpdor.save(fpdobj.get());
                }
                else
                {
                    FollowedPostDeleteObject fpdo = new FollowedPostDeleteObject(post_id.get(), utc.getUser().getId());
                    fpdor.save(fpdo);
                }
                
                ra.addFlashAttribute("alert", "Followed post deleted");
                ret = "redirect:/user/flpost?pg="+pgn.get();
            }
            break;
        }
        
        return ret;
    }
    
    @GetMapping("/prf")
    public String editProfile(HttpServletRequest req, ModelMap model)
    {
        aac.displayAdvert(model);   //This line is for adverts
        
        String[] hideBlocks = {"firstBlock", "thirdBlock", "fourthBlock"};
        utc.dispBlock(model, "secondBlock", hideBlocks);
        utc.userModel(model);
        
        model.addAttribute("postclass", new PostClass());
        return "pages/userpage";
    }
    
    @RequestMapping("/ads")
    public String creatAds(HttpServletRequest req, ModelMap model, @RequestParam("ct_")Optional<String> ct_, 
    @RequestParam("pt_ad")Optional<String> pt_ad, @ModelAttribute("advertObject")Optional<AdvertObject> advertObject, 
    @RequestParam("pg")Optional<Integer> pg, @RequestParam("edit")Optional<String> edit, @RequestParam("pos")Optional<Long> pos, 
    @RequestParam("pt_adx")Optional<String> pt_adx, @RequestParam("edit_")Optional<String> edit_, RedirectAttributes ra, 
    @ModelAttribute("editAdvertObject")Optional<AdvertObject> editAdvertObject)
    {
        aac.displayAdvert(model);   //This line is for adverts
        utc.userModel(model);
        
        if(ct_.orElse(null) != null)
        {
            switch(ct_.get())
            {
                case "nw":
                {
                    String[] hideBlocks = {"firstBlock", "thirdBlock", "fourthBlock"};
                    utc.dispBlock(model, "secondBlock", hideBlocks);
                    model.addAttribute("advertObject", new AdvertObject());
                }
                break;
            
                case "mn":
                {
                    String[] hideBlocks = {"firstBlock", "secondBlock", "fourthBlock"};
                    utc.dispBlock(model, "thirdBlock", hideBlocks);
                    
                    List<AdvertObject> ad = utc.getUser().getAdvertObject();
                    
                    if(!ad.isEmpty())
                    {
                        int init = 0;
                        int end = 5;
                        List<AdvertObject> selectedAd = new LinkedList<>();
                        
                        if(pg.get() > 1)
                        {
                            init = (pg.get() - 1) * end;
                            end = end * pg.get();
                        }
                        
                        if(ad.size() < end)
                        {
                            end = ad.size();
                        }
                        
                        for(int count = init; count < end; count++)
                        {
                            selectedAd.add(ad.get(count));
                        }
                        
                        model.addAttribute("pgn", pg.get());
            
                        model.addAttribute("prev", pg.get()-1);
                        model.addAttribute("next", pg.get()+1);
                        model.addAttribute("unExpiredAdObjList", selectedAd);
                    
                        if(pg.get() - 1 == 0)
                        {
                            model.addAttribute("disp1", "none");
                        }
                        if(selectedAd.isEmpty())
                        {
                            model.addAttribute("disp2", "none");
                            model.addAttribute("noAdAvailable", "No ad available");
                            model.addAttribute("theclass", "realcentertinz");
                        }
                    }
                    else
                    {
                        model.addAttribute("noAdsPostedYet", "realcentertinz");
                        model.addAttribute("alertFallback", "No ads posted yet");
                    }
                }
                break;
            
            }
        }
        else if(pt_ad.orElse(null) != null)
        {
            String[] hideBlocks = {"firstBlock", "thirdBlock", "fourthBlock"};
            utc.dispBlock(model, "secondBlock", hideBlocks);
                
            if(utc.checkCredit())
            {
                String path = utc.getFilePath()+"ad_img";
                String landingPage = advertObject.get().getLandingPage().trim();
                String payOption = advertObject.get().getPayOption();
                MultipartFile file = advertObject.get().getFile();
                String fileName = file.getOriginalFilename();
                long fileSize = file.getSize();
                
                model.addAttribute("advertObject", advertObject.get());
                
                if(!file.isEmpty())
                {
                    if(fileSize < 1000002)
                    {
                        if(fileName != null && fileName.length() < 21)
                        {
                            if(fileName.endsWith(".jpg") || fileName.endsWith(".png") || fileName.endsWith(".gif") 
                            || fileName.endsWith(".jpeg") || fileName.endsWith(".JPG") || fileName.endsWith(".PNG") 
                            || fileName.endsWith(".GIF") || fileName.endsWith(".JPEG") || fileName.endsWith(".webp") 
                            || fileName.endsWith(".WEBP"))
                            {
                                try 
                                {
                                    if(landingPage != null)
                                    {
                                        AdvertObject ao = null;
                                                
                                        switch(payOption)
                                        {
                                            case "CPM":
                                            {
                                                ao = new AdvertObject(utc.getUser().getId(), "CPM", utc.getDate(), fileName, landingPage);
                                            }
                                            break;
                                        
                                            case "CPC":
                                            {
                                                ao = new AdvertObject(utc.getUser().getId(), "CPC", utc.getDate(), fileName, landingPage);
                                            }
                                            break;
                                        }
                                        
                                        aor.save(ao);   //Learnt my lesson from Long and long (reference vs primitive type between object wrapper classes) 
                                        ra.addFlashAttribute("alert", "Ad saved");
                                        File pathToFile=new File(path, fileName);
                                        file.transferTo(pathToFile);
                                        
                                        return "redirect:/user/ads?ct_=mn&pg=1";
                                    }
                                }
                                catch (IOException | IllegalStateException ex)
                                {
                                    Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                            else
                            {
                                model.addAttribute("alert", "Invalid image format (suported: jpg, png, gif, webp)");
                            }
                        }
                        else
                        {
                            model.addAttribute("alert", "Image name is long (should be less than 20 characters)");
                        }
                    }
                    else
                    {
                        model.addAttribute("alert", "Acceptable image size exceeded (should be 1MB of less)");
                    }
                }
            }
            else
            {
                model.addAttribute("alert", "Buy credit");
            }
        }
        else if(edit.orElse(null) != null)
        {
            String[] hideBlocks = {"firstBlock", "secondBlock", "thirdBlock"};
            utc.dispBlock(model, "fourthBlock", hideBlocks);
            
            Optional<AdvertObject> ao = aor.findById(pos.get());
            
            if(ao.get().getUserId().equals(utc.getUser().getId()))
            {
                model.addAttribute("editAdvertObject", ao.get());
                model.addAttribute("pgn", pg.get());
            }
            else
            {
                model.addAttribute("alert", "Access denied");
            }
        }
        else if(pt_adx.orElse(null) != null && edit_.orElse(null) != null && pos.orElse(null) != null)
        {
            Optional<AdvertObject> ao = aor.findById(pos.get());
            
            if(ao.get().getUserId().equals(utc.getUser().getId()))
            {
                String[] hideBlocks = {"firstBlock", "secondBlock", "thirdBlock"};
                utc.dispBlock(model, "fourthBlock", hideBlocks);
                
                model.addAttribute("pos", pos.get());
                model.addAttribute("pgn", pg.get());
                model.addAttribute("editAdvertObject", ao.get());
                
                switch(editAdvertObject.get().getActionButton())
                {
                    case "update":
                    {
                        MultipartFile file = editAdvertObject.get().getFile();
                        String fileName = file.getOriginalFilename();
                        long fileSize = file.getSize();
                        
                        String path = utc.getFilePath()+"ad_img";
                
                        ao.get().setPayOption(editAdvertObject.get().getPayOption());
                        
                        if(editAdvertObject.get().getLandingPage() != null && !editAdvertObject.get().getLandingPage().matches("\\s*"))
                        {
                            ao.get().setLandingPage(editAdvertObject.get().getLandingPage());
                            
                            if(!file.isEmpty())
                            {
                                if(fileSize < 1000002)
                                {
                                    if(fileName != null && fileName.length() < 21)
                                    {
                                        if(fileName.endsWith(".jpg") || fileName.endsWith(".png") || fileName.endsWith(".gif") 
                                        || fileName.endsWith(".jpeg") || fileName.endsWith(".JPG") || fileName.endsWith(".PNG") 
                                        || fileName.endsWith(".GIF") || fileName.endsWith(".JPEG") || fileName.endsWith(".webp") 
                                        || fileName.endsWith(".WEBP"))
                                        {
                                            try 
                                            {
                                                ao.get().setAdsImage(fileName);
                                                ao.get().setApprove(0); //If you change your ad image, approval becomes 0, just so admin cross-examines the new image and approve again
                                                File pathToFile=new File(path, fileName);
                                                file.transferTo(pathToFile);
                                                
                                                aor.save(ao.get());
                                                //model.addAttribute("editAdvert", ao.get());
                                                model.addAttribute("alert", "Ad updated successfully");
                                            }
                                            catch (IOException | IllegalStateException ex)
                                            {
                                                Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, ex);
                                            }
                                        }
                                        else
                                        {
                                            model.addAttribute("alert", "Invalid image format (suported: jpg, png, gif, webp)");
                                        }
                                    }
                                    else
                                    {
                                        model.addAttribute("alert", "Image name is long (should be less than 20 characters)");
                                    }
                                }
                                else
                                {
                                    model.addAttribute("alert", "Acceptable image size exceeded (should be 1MB of less)");
                                }
                            }
                            else
                            {
                                aor.save(ao.get());
                                model.addAttribute("alert", "Ad updated successfully");                
                            }
                        }
                        else
                        {
                            model.addAttribute("alert", "Enter a URL");
                        }
                    }
                    break;
                    
                    case "pause":
                    {
                        if(ao.get().getPause() == 1)
                        {
                            ao.get().setPause(0);
                            model.addAttribute("alert", "Started");
                        }
                        else if(ao.get().getPause() == 0)
                        {
                            ao.get().setPause(1);
                            model.addAttribute("alert", "Paused");
                        }
                        
                        model.addAttribute("editAdvertObject", ao.get());
                        model.addAttribute("pos", pos.get());
                        
                        aor.save(ao.get());
                    }
                    break;
                    
                    case "terminate":
                    {
                        ao.get().setExpired(1);
                        aor.save(ao.get());
                        ra.addFlashAttribute("alert", "Ad terminated");
                        return "redirect:/user/ads?ct_=mn&pg="+pg.get();
                    }                    
                }
            }
            else
            {
                String[] hideBlocks = {"firstBlock", "secondBlock", "thirdBlock", "fourthBlock"};
                utc.dispBlock(model, "", hideBlocks);
                model.addAttribute("noAdsPostedYet", "realcentertinz");
                model.addAttribute("alertFallback", "Access denied");
            }
        }
        else
        {
            String[] hideBlocks = {"secondBlock", "thirdBlock", "fourthBlock"};
            utc.dispBlock(model, "firstBlock", hideBlocks);
        }
        
        return "pages/adspage";
    }
    
    @PostMapping("/prfupd")
    public String updatePix(@ModelAttribute("postclass")PostClass pc, ModelMap model, RedirectAttributes ra)
    {
        aac.displayAdvert(model);   //This line is for adverts
        Optional<UserClass> uc = ucr.findById(utc.getUser().getId());
        String path = utc.getFilePath()+"profile_img";
        MultipartFile coverFile = pc.getCoverFile();
        if(coverFile.getSize() > 0 && coverFile.getSize() <= 4000000)
        {
            String coverFileName = coverFile.getOriginalFilename();
            if(coverFileName!=null && coverFileName.length() <= 50)
            {
                if(coverFileName.endsWith(".jpg") || coverFileName.endsWith(".png") || coverFileName.endsWith(".gif")
                || coverFileName.endsWith(".jpeg") || coverFileName.endsWith(".JPG") || coverFileName.endsWith(".PNG") 
                || coverFileName.endsWith(".GIF") || coverFileName.endsWith(".JPEG") || coverFileName.endsWith(".webp") 
                || coverFileName.endsWith(".WEBP"))
                {
                    try
                    {
                        uc.get().setPix(coverFileName);
                        ucr.save(uc.get());
                        
                        File pathToFile=new File(path, coverFileName);
                        coverFile.transferTo(pathToFile);
                        ra.addFlashAttribute("alert", "Display photo updated");
                    }
                    catch (IOException | IllegalStateException ex)
                    {
                        Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                else
                {
                    ra.addFlashAttribute("alert", "Invalid image format (suported: jpg, png, gif, webp)");
                }
            }
            else
            {
                ra.addFlashAttribute("alert", "Photo name too long (less than 50 characters)");
            }
        }
        else if(coverFile.getSize() == 0)   //why not use coverFile.isEmpty()
        {
            uc.get().setPix("empty.png");
            ucr.save(uc.get());
        }
        return "redirect:/user/prf";
    }
    
    @RequestMapping(value="/src")
    public String search(@RequestParam("uts")String username, ModelMap model)
    {
        aac.displayAdvert(model);   //This line is for adverts
        utc.userModel(model);
        
        String usernameParam = username.trim().toLowerCase();
        Optional<UserClass> uc = ucr.findByUsername(usernameParam);
        
        if(uc.orElse(null) != null)
        {
            if(!usernameParam.equals(utc.getUser().getUsername()))
            {
                model.addAttribute("searchedUser", uc.get());
                boolean following = fobjr.followOrNot(utc.getUser().getId(), uc.get().getId());
                if(following)
                {
                    model.addAttribute("following", "Yes");
                }
                else
                {
                    model.addAttribute("following", "No");
                }
            }
            else
            {
                model.addAttribute("alert", "This is me");
                model.addAttribute("nothing", "realcentertinz");
            }
        }
        else
        {
            model.addAttribute("alert", "Username does not exist");
            model.addAttribute("nothing", "realcentertinz");
        }
        
        return "pages/profilesearch";
    }
    
    @GetMapping(value="/prf_src")
    public String followAndUnfollow(@RequestParam("usr")Optional<Long> followerUserId, @RequestParam("akt")Optional<Integer> action, ModelMap model)
    {
        aac.displayAdvert(model);   //This line is for adverts
        utc.userModel(model);
        
        Optional<FollowerObject> fobj = fobjr.followOrNotObject(utc.getUser().getId(), followerUserId.get());
        Optional<UserClass> uc = ucr.findById(followerUserId.get());
        
        if(fobj.orElse(null) != null)
        {
            switch(action.get())
            {
                case 1:
                {
                    fobj.get().setFlag(1);
                    utc.followUnfollow(followerUserId.get(), "follow");
                }
                break;
            
                case 2:
                {
                    fobj.get().setFlag(0);
                    utc.followUnfollow(followerUserId.get(), "unfollow");
                }
                break;
            }
            
            fobjr.save(fobj.get());
        }
        else
        {
            if(action.get() == 1)   //Not necessary but then, leave am like that
            {
                FollowerObject fo = new FollowerObject(utc.getUser().getId(), followerUserId.get());
                fobjr.save(fo);
                utc.followUnfollow(followerUserId.get(), "follow");
            }
        }
        
        return "redirect:/user/src?uts=" + uc.get().getUsername();
    }
    
    @GetMapping("/mail_us")
    public String mailUs(HttpServletRequest req, ModelMap model)
    {
        aac.displayAdvert(model);   //This line is for adverts
        
        String[] hideBlocks = {"firstBlock", "secondBlock", "thirdBlock"};
        utc.dispBlock(model, "fourthBlock", hideBlocks);
        utc.userModel(model);
        utc.sessionUserDetails(req);    //very important
        model.addAttribute("postclass", new PostClass());
        
        return "pages/userpage";
    }
    
    @PostMapping("/mail_us_postcontrol")
    public String mailUsPost(@ModelAttribute("postclass")PostClass pc, HttpServletRequest req, 
    ModelMap model, RedirectAttributes ra)
    {
        aac.displayAdvert(model);   //This line is for adverts
        String[] hideBlocks = {"firstBlock", "secondBlock", "thirdBlock"};
        utc.dispBlock(model, "fourthBlock", hideBlocks);
        utc.userModel(model);
        utc.sessionUserDetails(req);    //very important
        
        String content = pc.getContent().trim();
        
        if(content.length() < 701)
        {
            if(utc.checkTag2(content))
            {
                InboxObject io = new InboxObject(utc.getUser().getId(), utc.getDate(), content);
                ior.save(io);
                ra.addFlashAttribute("alert", "Sent");
                return "redirect:mail_us";
            }
            else
            {
                pc.setContent(content);
                model.addAttribute("postclass", pc);
                model.addAttribute("alert", "< and > are not allowed here");
                return "pages/userpage";
            }
        }
        else
        {
            model.addAttribute("alert", "Message too lengthy (must be less than 700 characters)");
        }
        
        return "pages/userpage";
    }
    
    @GetMapping("/inbox")
    public String getInbox(ModelMap model, @RequestParam("pg")Optional<Integer> page,
    RedirectAttributes ra, HttpServletRequest req)
    {
        aac.displayAdvert(model);   //This line is for adverts
        utc.userModel(model);
        
        int init = 0;
        int end = 10;
        List<InboxObject> inboxObj = new LinkedList<>();
        
        if(page.get() > 1)
        {
            init = (page.get() - 1) * end;
            end = end * page.get();
        }
        
        List<InboxObject> io = utc.getUser().getInboxObject();
        if(!io.isEmpty())  //If there are messages
        {
            if(io.size() < end)
            {
                end = io.size();
            }
            for(int count = init; count < end; count++)
            {
                inboxObj.add(io.get(count));
            }
            
            model.addAttribute("inboxObj", inboxObj);
            model.addAttribute("pgn", page.get());  //Key
            model.addAttribute("prev", page.get()-1);
            model.addAttribute("next", page.get()+1);
                
            if(page.get() - 1 == 0)
            {
                model.addAttribute("disp1", "none");
            }
            if(inboxObj.isEmpty())
            {
                model.addAttribute("nonotifications", "No messages");
                model.addAttribute("disp2", "none");
                model.addAttribute("theclass", "realcentertinz");
            }
        }
        else    //If there are no messages
        {
            model.addAttribute("nothing", "realcentertinz");
            model.addAttribute("alertFallback", "No messages");
        }
        return "pages/inboxpage";
    }
    
    @RequestMapping("/inbox_rkt_")
    public String actInbox(ModelMap model, @RequestParam("mid")Optional<Long> inboxId,
    @RequestParam("pgn")Optional<Integer> page, @RequestParam("akt")Optional<String> action,
    RedirectAttributes ra, HttpServletRequest req, @RequestParam("rid")Optional<Long> replyId, 
    @ModelAttribute("replyObjectPost")Optional<PostClass> pc)
    {
        aac.displayAdvert(model);   //This line is for adverts
        utc.userModel(model);
        Long myId = utc.getUser().getId();
        
        switch(action.get())
        {
            case "dlt_Mx":
            {
                Optional<InboxObject> inboxObj = ior.findById(inboxId.get());
                if(inboxObj.orElse(null) != null)
                {
                    if(myId.equals(inboxObj.get().getUserInbox().getId()))
                    {
                        inboxObj.get().setDeleteUserFlag(1);
                        ior.save(inboxObj.get());
                    }
                    else
                    {
                        ra.addFlashAttribute("alert", "Cannot execute action");
                    }
                }
                else
                {
                    ra.addFlashAttribute("alert", "Cannot execute action");
                }
            }
            break;
            
            case "dlt_Mz":
            {
                Optional<ReplyObject> replyObj = ror.findById(replyId.get());
                if(replyObj.orElse(null) != null)
                {
                    if(myId.equals(replyObj.get().getInboxreply().getUserInbox().getId()))
                    {
                        replyObj.get().setDeleteUserFlag(1);
                        ror.save(replyObj.get());
                    }
                    else
                    {
                        ra.addFlashAttribute("alert", "Cannot execute action");
                    }
                }
                else
                {
                    ra.addFlashAttribute("alert", "Cannot execute action");    
                }
            }
            break;
            
            case "rep_":
            {
                Optional<ReplyObject> replyObj = ror.findById(replyId.get());
                if(replyObj.orElse(null) != null)
                {
                    if(myId.equals(replyObj.get().getInboxreply().getUserInbox().getId()))
                    {
                        replyObj.get().setUserRead(1);
                        ror.save(replyObj.get());
                        model.addAttribute("modelRID", replyId.get());
                        model.addAttribute("modelPGN", page.get());
                        model.addAttribute("replyObjectPost", new PostClass());
                        utc.userModel(model);
                        return "pages/inboxpage";
                    }
                    else
                    {
                        ra.addFlashAttribute("alert", "Cannot execute action");     
                    }
                }
                else
                {
                    ra.addFlashAttribute("alert", "Cannot execute action"); 
                }
            }
            break;
            
            case "_viewmark_":
            {
                Optional<ReplyObject> replyObj = ror.findById(replyId.get());
                if(replyObj.orElse(null) != null)
                {
                    if(myId.equals(replyObj.get().getInboxreply().getUserInbox().getId()))
                    {
                        replyObj.get().setUserRead(1);
                        ror.save(replyObj.get());
                    }
                    else
                    {
                        ra.addFlashAttribute("alert", "Cannot execute action");     
                    }
                }
                else
                {
                    ra.addFlashAttribute("alert", "Cannot execute action"); 
                }
            }
            break;
                    
            case "rep_post":
            {
                String date = utc.getDate();
                String content = pc.get().getContent();
                InboxObject iObj = new InboxObject(myId, date, content);
                ior.save(iObj);
                
                Optional<InboxObject> inboxObj = ior.findInboxObjectByDateAndContent(date, content);
                QuoteInboxObject qio = new QuoteInboxObject(inboxObj.get().getId(), replyId.get());
                qior.save(qio);
            }
            break;
        }
        
        return "redirect:inbox?pg="+page.get();
    }
    
    @RequestMapping("/userAjaxDynamicFileUpload")
    @ResponseBody
    public boolean userAjaxDynamicFileUpload(@RequestParam("userDynamicUpload")MultipartFile file)
    {
        try
        {
            String path = utc.getFilePath() + "dist_img";
            File pathToFile = new File(path, file.getOriginalFilename());
            file.transferTo(pathToFile);
        }
        catch (IOException | IllegalStateException ex)
        {
            Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return true;
    }
    
    @RequestMapping("/ajaxSubCommentDynamicComment")
    @ResponseBody
    public String subCommentDynamicComment(@RequestParam("sent")String sent)
    {
        Gson gson = new Gson();
        DynamicContent dc = gson.fromJson(sent, DynamicContent.class);
        String content = dc.getContent();
        Long post_id = dc.getPos();
        Long comment_id = dc.getCid();
        String title = dc.getTitle();
        
        content=content.replaceAll("<_", "<br/><img alt='content image' width='250' height='150' src='/9jaforum/files/dist_img/");
        content=content.replaceAll("_>", "'/><br/>");
        
        Optional<CommentClass> cc = ccr.findById(comment_id);  //Comment you are subcommenting on
        SubCommentClass scc;
                                
        if(utc.getUser().getId().equals(cc.get().getUser_id()))  //No need marking your comment as unread na, abi no be you write am??
        {
            scc = new SubCommentClass(utc.getUser().getId(), comment_id, content, utc.getDate(), "read");
        }
        else
        {
            scc = new SubCommentClass(utc.getUser().getId(), comment_id, content, utc.getDate());
        }
                                
        sccr.save(scc);
                                
        //Notify the owner of the comment here: Very important
        String postlink = "s_ch?pos="+post_id+"&t="+title+"&p="+dc.getPg()+"&cid="+comment_id+"#"+comment_id;
        utc.updateInbox(cc.get().getUser_id(), comment_id, postlink);
                                
        return "redirect:/b_ch?pos="+post_id+"&t="+title+"&page="+dc.getPage()+"&p="+dc.getPg()+"&alertx=Posted";
    }
    
    @GetMapping("/mre_cmt")
    public String subCommentExtra(@RequestParam("pos")Optional<Long> post_id, @RequestParam("t")Optional<String> title, 
    @RequestParam("p") Optional<Integer> pg, ModelMap model, @RequestParam("page")Optional<Integer> commentPaginate, 
    @RequestParam("akt")Optional<String> action, HttpServletRequest req, RedirectAttributes ra, 
    @RequestParam("cid")Optional<Long> comment_id, @RequestParam("sid")Optional<Long> subcomment_id, 
    @RequestParam("p2")Optional<Integer> currentPage, @RequestParam("cog")Optional<String> cog)
    {
        aac.displayAdvert(model);   //This line is for adverts
        
        String ret= null;
        utc.sessionUserDetails(req);    //Do not ever remove this
        long id = utc.getUser().getId();
        
        switch(action.get())
        {
            case "lk_x":
            {
                Optional<SubCommentClass> scc = sccr.findById(subcomment_id.get());
                String check = scrcr.likedBefore(subcomment_id.get(), id);  //has this user liked this subcomment before now?
                long likes = scc.get().getLikes();
                
                switch(check)
                {
                    case "":
                    {
                        likes = likes + 1;
                        scc.get().setLikes(likes);
                        utc.alterUserRankingParameters(scc.get().getUser_id(), "save_like");
                    }
                    break;
                    
                    case "liked":
                    {
                        likes = likes - 1;
                        scc.get().setLikes(likes);
                        utc.alterUserRankingParameters(scc.get().getUser_id(), "save_unlike");
                    }
                    break;
                    
                    case "unliked":
                    {
                        likes = likes + 1;
                        scc.get().setLikes(likes);
                        utc.alterUserRankingParameters(scc.get().getUser_id(), "save_like");
                    }
                    break;
                }
                
                sccr.save(scc.get());
                utc.alterSubCommentRankingParameters(scc, sccr);
                
                if(cog.orElse(null) != null)
                {
                    ret = "redirect:/mresu_b?p2="+currentPage.get()+"&pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&pg="+pg.get()+"&cog="+cog.get()+"&cid="+comment_id.get();
                }
                else
                {
                    ret = "redirect:/mresu_b?p2="+currentPage.get()+"&pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&pg="+pg.get()+"&cid="+comment_id.get();
                }
            }
            break;
            
            case "flg_x":
            {
                Optional<SubCommentClass> scc = sccr.findById(subcomment_id.get());
                String check = scrcr.redFlaggedBefore(subcomment_id.get(), id);  //has this user liked this subcomment before now?
                long redflag = scc.get().getRedflag();
                
                switch(check)
                {
                    case "":
                    {
                        redflag = redflag + 1;
                        scc.get().setRedflag(redflag);
                        utc.alterUserRankingParameters(scc.get().getUser_id(), "save_redflag");
                    }
                    break;
                    
                    case "redflagged":
                    {
                        redflag = redflag - 1;
                        scc.get().setRedflag(redflag);
                        utc.alterUserRankingParameters(scc.get().getUser_id(), "save_unredflag");
                    }
                    break;
                    
                    case "notredflagged":
                    {
                        redflag = redflag + 1;
                        scc.get().setRedflag(redflag);
                        utc.alterUserRankingParameters(scc.get().getUser_id(), "save_redflag");
                    }
                    break;
                }
                
                sccr.save(scc.get());
                utc.alterSubCommentRankingParameters(scc, sccr);
                
                if(cog.orElse(null) != null)
                {
                    ret = "redirect:/mresu_b?p2="+currentPage.get()+"&pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&pg="+pg.get()+"&cog="+cog.get()+"&cid="+comment_id.get();
                }
                else
                {
                    ret = "redirect:/mresu_b?p2="+currentPage.get()+"&pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&pg="+pg.get()+"&cid="+comment_id.get();
                }                
            }
            break;
            
            case "str_x":
            {
                Optional<SubCommentClass> scc = sccr.findById(subcomment_id.get());
                String check = scrcr.starredBefore(subcomment_id.get(), id);  //has this user liked this subcomment before now?
                long starred = scc.get().getStar();
                
                switch(check)
                {
                    case "":
                    {
                        starred = starred + 1;
                        scc.get().setStar(starred);
                        utc.alterUserRankingParameters(scc.get().getUser_id(), "save_star");
                    }
                    break;
                    
                    case "starred":
                    {
                        starred = starred - 1;
                        scc.get().setStar(starred);
                        utc.alterUserRankingParameters(scc.get().getUser_id(), "save_unstar");
                    }
                    break;
                    
                    case "notstarred":
                    {
                        starred = starred + 1;
                        scc.get().setStar(starred);
                        utc.alterUserRankingParameters(scc.get().getUser_id(), "save_star");
                    }
                    break;
                }
                
                sccr.save(scc.get());
                utc.alterSubCommentRankingParameters(scc, sccr);
                
                if(cog.orElse(null) != null)
                {
                    ret = "redirect:/mresu_b?p2="+currentPage.get()+"&pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&pg="+pg.get()+"&cog="+cog.get()+"&cid="+comment_id.get();
                }
                else
                {
                    ret = "redirect:/mresu_b?p2="+currentPage.get()+"&pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&pg="+pg.get()+"&cid="+comment_id.get();
                }
            }
            break;
            
            case "edit_x":
            {
                Optional<SubCommentClass> scc = sccr.findById(subcomment_id.get());
                
                if(utc.getUser().getId().equals(scc.get().getUser_id()))
                {
                    String content = scc.get().getContent();
                
                    content=content.replaceAll("<br/><img alt='content image' width='250' height='150' src='/9jaforum/files/dist_img/", "<_");
                    content=content.replaceAll("'/><br/>", "_>");
                    
                    PostClass pClass = new PostClass();
                    pClass.setContent(content);
                    model.addAttribute("postclass", pClass);
                    model.addAttribute("pos", post_id.get());
                    model.addAttribute("sid", subcomment_id.get());
                    model.addAttribute("cid", comment_id.get());
                    model.addAttribute("t", title.get());
                    model.addAttribute("p", pg.get());
                    model.addAttribute("page", commentPaginate.get());
                    model.addAttribute("p2", currentPage.get());
                    
                    if(cog.orElse(null) != null)
                    {
                        model.addAttribute("cog", cog.get());
                    }
                    ret = "pages/editextrasubcommentpage";
                }
                else
                {
                    if(cog.orElse(null) != null)
                    {
                        ret = "redirect:/mresu_b?p2="+currentPage.get()+"&pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&pg="+pg.get()+"&cog="+cog.get()+"&cid="+comment_id.get();
                    }
                    else
                    {
                        ret = "redirect:/mresu_b?p2="+currentPage.get()+"&pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&pg="+pg.get()+"&cid="+comment_id.get();
                    }
                }
            }
            break;
            
            case "dlt_x":
            {
                String alert;
                Optional<SubCommentClass> scc = sccr.findById(subcomment_id.get());
                
                if(utc.getUser().getId().equals(scc.get().getUser_id()))
                {
                    scc.get().setApproved(0);
                    sccr.save(scc.get());
                    alert = "Deleted";
                }
                else
                {
                    alert = "Cannot delete comment";
                }
                
                if(cog.orElse(null) != null)
                {
                    ret = "redirect:/mresu_b?p2="+currentPage.get()+"&pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&pg="+pg.get()+"&cog="+cog.get()+"&cid="+comment_id.get();
                }
                else
                {
                    ret = "redirect:/mresu_b?p2="+currentPage.get()+"&pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&pg="+pg.get()+"&cid="+comment_id.get();
                }
            }
            break;
            
            case "dlt_xMod":
            {
                String alert;
                Optional<SubCommentClass> scc = sccr.findById(subcomment_id.get());
                
                if(utc.getUser().getColorclass().equals("user_mod"))
                {
                    scc.get().setApproved(0);
                    sccr.save(scc.get());
                    alert = "Deleted";
                }
                else
                {
                    alert = "Cannot delete comment";
                }
                
                if(cog.orElse(null) != null)
                {
                    ret = "redirect:/mresu_b?p2="+currentPage.get()+"&pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&pg="+pg.get()+"&cog="+cog.get()+"&cid="+comment_id.get();
                }
                else
                {
                    ret = "redirect:/mresu_b?p2="+currentPage.get()+"&pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&pg="+pg.get()+"&cid="+comment_id.get();
                }
            }
            break;
        }
        return ret;
    }
    
    @PostMapping("mre_sub_cmtpost")
    public String extraCmtPost(@RequestParam("pos")Optional<Long> post_id, @RequestParam("t")Optional<String> title, 
    @RequestParam("p") Optional<Integer> pg, ModelMap model, @RequestParam("page")Optional<Integer> commentPaginate, 
    @ModelAttribute("postclass")PostClass pc, RedirectAttributes ra, @RequestParam("akt")Optional<String> action, 
    @RequestParam("cid")Optional<Long> comment_id, @RequestParam("sid")Optional<Long> subcomment_id, 
    @RequestParam("p2")Optional<Integer> currentPage)
    {
        aac.displayAdvert(model);   //This line is for adverts
        
        if(utc.checkCommentBan())
        {
            //It won't come to this but just leave am there
            //Do not forget to add provisions for those banned etc, they should not be able to write post, comment etc
            return "redirect:/";
        }
        
        String ret = null;
        String path = utc.getFilePath()+"dist_img";
        String date = utc.getDate();
        String content = pc.getContent().trim();
        
        switch(action.get())
        {
            case "edit_subcmt":
            {
                switch(pc.getActionButton())
                {
                    case "add_img":
                    {
                        model.addAttribute("pos", post_id.get());
                        model.addAttribute("sid", subcomment_id.get());
                        model.addAttribute("cid", comment_id.get());
                        model.addAttribute("t", title.get());
                        model.addAttribute("p", pg.get());
                        model.addAttribute("page", commentPaginate.get());
                        model.addAttribute("p2", currentPage.get());
                
                        pc.setContent(content);
                
                        MultipartFile contentFile =  pc.getContentFile();
                        if(contentFile.isEmpty())
                        {
                            model.addAttribute("alert", "No image file selected");
                        }
                        else if(contentFile.getSize() > 0 && contentFile.getSize() <= 4000000)
                        {
                            String contentFileName = contentFile.getOriginalFilename();
                            if(contentFileName != null && contentFileName.length() <= 50)
                            {
                                if(contentFileName.endsWith(".jpg") || contentFileName.endsWith(".png") || contentFileName.endsWith(".gif") 
                                || contentFileName.endsWith(".jpeg") || contentFileName.endsWith(".JPG") || contentFileName.endsWith(".PNG") 
                                || contentFileName.endsWith(".GIF") || contentFileName.endsWith(".JPEG") || contentFileName.endsWith(".webp") 
                                || contentFileName.endsWith(".WEBP"))
                                {
                                    try
                                    {
                                        String imageRef="<_" + contentFileName + "_>";
                                        pc.setContent(content + imageRef);
                                
                                        model.addAttribute("alert", "Image added to content");
                                        File pathToFile=new File(path, contentFileName);
                                        contentFile.transferTo(pathToFile);
                                    }
                                    catch (IllegalStateException | IOException ex) 
                                    {
                                        Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                                else
                                {
                                    model.addAttribute("alert", "Invalid image format (suported: jpg, png, gif, webp)");
                                }
                            }
                            else
                            {
                                model.addAttribute("alert", "Content image name too long (less than 50 characters)");
                            }
                        }
                        else if(contentFile.getSize() == 0)
                        {
                            model.addAttribute("alert", "");
                        }
                        else
                        {
                            model.addAttribute("alert", "File size exceeded (4MB or less)");
                        }
                    }
                    break;
            
                    case "post":
                    {
                        pc.setContent(content);
                
                        content=content.replaceAll("<_", "<br/><img alt='content image' width='250' height='150' src='/9jaforum/files/dist_img/");
                        content=content.replaceAll("_>", "'/><br/>");
                
                        if(!content.matches("\\s*"))
                        {
                            if(content.length() > 801)
                            {
                                model.addAttribute("pos", post_id.get());
                                model.addAttribute("sid", subcomment_id.get());
                                model.addAttribute("cid", comment_id.get());
                                model.addAttribute("t", title.get());
                                model.addAttribute("p", pg.get());
                                model.addAttribute("page", commentPaginate.get());
                                model.addAttribute("p2", currentPage.get());
                        
                                model.addAttribute("alert", "Comment must be less than 800 characters [" + pc.getContent().length() +"]");
                            }
                            else
                            {
                                Optional<SubCommentClass> sccObj = sccr.findById(subcomment_id.get());
                                sccObj.get().setContent(content);
                                sccr.save(sccObj.get());
                                //put alert later
                                return "redirect:/mresu_b?p2="+currentPage.get()+"&pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&pg="+pg.get()+"&cid="+comment_id.get();
                            }
                        }
                        else
                        {
                            ra.addFlashAttribute("alertx", "Comment cannot be empty");
                        }
                    }
                    break;
                }
                ret = "pages/editextrasubcommentpage";
            }
            break;
        }
        return ret;
    }
    
    @RequestMapping("/ajaxSubCommentDynamicComment_")
    @ResponseBody
    public String dynamicQuote(@RequestParam("sent")String sent)
    {
        Gson gson = new Gson();
        DynamicContent dc = gson.fromJson(sent, DynamicContent.class);
        String content = dc.getContent();  //The quoting text
        String content2 = dc.getContent2();  //The quoted text
        Long post_id = dc.getPos();
        Long comment_id = dc.getCid();
        String title = dc.getTitle();
        String date = utc.getDate();
        
        content = content.replaceAll("<_", "<br/><img alt='content image' width='250' height='150' src='/9jaforum/files/dist_img/");
        content = content.replaceAll("_>", "'/><br/>");
        
        content2 = content2.replaceAll("<_", "<br/><img alt='content image' width='250' height='150' src='/9jaforum/files/dist_img/");
        content2 = content2.replaceAll("_>", "'/><br/>");
        
        Optional<CommentClass> ccid = ccr.findById(comment_id);
        
        CommentClass cc = new CommentClass(utc.getUser().getId(), post_id, content, date);
        ccr.save(cc);
        Optional<CommentClass> quickCom = ccr.getExactPost(utc.getUser().getId(), post_id, date);
        QuoteObject qobj = new QuoteObject(ccid.get().getUser_id(), quickCom.get().getId(), content2, ccid.get().getPostdate());
        qobjr.save(qobj);
                                    
        /*
        //Notify the owner of the comment here: Very important
        Optional<CommentClass> cLass = ccr.findById(comment_id.get());
        String postlink = "s_ch?pos="+post_id.get()+"&t="+title.get()+"&p="+pg.get()+"&cid="+comment_id.get()+"#"+comment_id.get();
        utc.updateInbox(mobjr, cLass.get().getUser_id(), comment_id.get(), postlink);
        */
        return "redirect:/b_ch?pos="+post_id+"&t="+title+"&page="+dc.getPage()+"&p="+dc.getPg()+"&alertx=Posted";
    }
    
    @RequestMapping("/getCommentToQuote")
    @ResponseBody
    public String getCommentToQuote(@RequestParam("comment_id")Long comment_id)
    {
        Optional<CommentClass> ccid = ccr.findById(comment_id);
        String content = ccid.get().getContent();
        
        content = content.replaceAll("<br/><img alt='content image' width='250' height='150' src='/9jaforum/files/dist_img/", "<_");
        content = content.replaceAll("'/><br/>", "_>");
        
        return content;
    }
    
    @RequestMapping("/ajaxSubCommentDynamicComment_editcomment")
    @ResponseBody
    public String dynamicEditComment(@RequestParam("sent")String sent)
    {
        Gson gson = new Gson();
        DynamicContent dc = gson.fromJson(sent, DynamicContent.class);
        String content = dc.getContent();
        Long post_id = dc.getPos();
        Long comment_id = dc.getCid();
        String title = dc.getTitle();
        
        content = content.replaceAll("<_", "<br/><img alt='content image' width='250' height='150' src='/9jaforum/files/dist_img/");
        content = content.replaceAll("_>", "'/><br/>");
        
        Optional<CommentClass> ccid = ccr.findById(comment_id);
        ccid.get().setContent(content);
        ccr.save(ccid.get());
                                    
        /*
        //Notify the owner of the comment here: Very important
        Optional<CommentClass> cLass = ccr.findById(comment_id.get());
        String postlink = "s_ch?pos="+post_id.get()+"&t="+title.get()+"&p="+pg.get()+"&cid="+comment_id.get()+"#"+comment_id.get();
        utc.updateInbox(mobjr, cLass.get().getUser_id(), comment_id.get(), postlink);
        */
        return "redirect:/b_ch?pos="+post_id+"&t="+title+"&page="+dc.getPage()+"&p="+dc.getPg()+"&alertx=Posted";
    }
    
            
    @RequestMapping("/getSubCommentToEdit")
    @ResponseBody
    public String getSubCommentToEdit(@RequestParam("subcomment_id")Long subcomment_id)
    {
        Optional<SubCommentClass> scc = sccr.findById(subcomment_id);
        String content = scc.get().getContent();
        
        content = content.replaceAll("<br/><img alt='content image' width='250' height='150' src='/9jaforum/files/dist_img/", "<_");
        content = content.replaceAll("'/><br/>", "_>");
        
        return content;
    }
    
    @RequestMapping("/ajaxDynamik_submit_edited_sub_comment_")
    @ResponseBody
    public String dynamicEditSubComment(@RequestParam("sent")String sent)
    {
        Gson gson = new Gson();
        DynamicContent dc = gson.fromJson(sent, DynamicContent.class);
        String content = dc.getContent();
        Long post_id = dc.getPos();
        Long subcomment_id = dc.getSid();
        String title = dc.getTitle();
        
        content = content.replaceAll("<_", "<br/><img alt='content image' width='250' height='150' src='/9jaforum/files/dist_img/");
        content = content.replaceAll("_>", "'/><br/>");
        
        Optional<SubCommentClass> scc = sccr.findById(subcomment_id);
        scc.get().setContent(content);
        sccr.save(scc.get());
                                    
        /*
        //Notify the owner of the comment here: Very important
        Optional<CommentClass> cLass = ccr.findById(comment_id.get());
        String postlink = "s_ch?pos="+post_id.get()+"&t="+title.get()+"&p="+pg.get()+"&cid="+comment_id.get()+"#"+comment_id.get();
        utc.updateInbox(mobjr, cLass.get().getUser_id(), comment_id.get(), postlink);
        */
        return "redirect:/b_ch?pos="+post_id+"&t="+title+"&page="+dc.getPage()+"&p="+dc.getPg()+"&alertx=Posted";
    }
    
    @RequestMapping("/ajaxDynamik_submit_edited_sub_comment_2")
    @ResponseBody
    public String dynamicEditSubComment2(@RequestParam("sent")String sent)
    {
        Gson gson = new Gson();
        DynamicContent dc = gson.fromJson(sent, DynamicContent.class);
        String content = dc.getContent();
        Long post_id = dc.getPos();
        Long subcomment_id = dc.getSid();
        String title = dc.getTitle();
        
        content = content.replaceAll("<_", "<br/><img alt='content image' width='250' height='150' src='/9jaforum/files/dist_img/");
        content = content.replaceAll("_>", "'/><br/>");
        
        Optional<SubCommentClass> scc = sccr.findById(subcomment_id);
        scc.get().setContent(content);
        sccr.save(scc.get());
        
        return "redirect:/mresu_b?p2="+dc.getP2()+"&pos="+post_id+"&t="+title+"&page="+dc.getPage()+"&pg="+dc.getPg()+"&cid="+dc.getCid();
    }
}