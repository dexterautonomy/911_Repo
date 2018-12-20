package com.hingebridge.web.usercontrollers;

import com.hingebridge.model.CommentClass;
import com.hingebridge.model.FollowerObject;
import com.hingebridge.model.MessageObject;
import com.hingebridge.model.PostClass;
import com.hingebridge.model.QuoteObject;
import com.hingebridge.model.SubCommentClass;
import com.hingebridge.model.UserClass;
import com.hingebridge.repository.CommentClassRepo;
import com.hingebridge.repository.CommentReactionClassRepo;
import com.hingebridge.repository.FollowerObjectRepo;
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
import org.springframework.security.core.Authentication;
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
import com.hingebridge.repository.QuoteObjectRepo;
import com.hingebridge.repository.SubCommentClassRepo;
import com.hingebridge.repository.SubCommentReactionClassRepo;
import com.hingebridge.repository.UserClassRepo;
@PreAuthorize("hasRole('USER')")
@Controller
@RequestMapping("/user")
public class UserController
{
    @Autowired
    private UtilityClass utc;
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
    
    @GetMapping("/login")
    public String userHomePage(Authentication auth, HttpServletRequest req, ModelMap model)
    {
        String[] hideBlocks = {"secondBlock", "thirdBlock"};
        utc.dispBlock(model, "firstBlock", hideBlocks);
        utc.modelUser(model);
        utc.sessionUserDetails(req);    //very important
        utc.modelTransfer(model);
        model.addAttribute("postclass", new PostClass());
        
        return "pages/userpage";
    }
    
    @PostMapping("/postcontrol")
    public String userPostControl(@ModelAttribute("postclass")PostClass pc, HttpServletRequest req, 
    ModelMap model, RedirectAttributes ra) throws IOException
    {
        utc.modelUser(model);
        String path = utc.getFilePath()+"dist_img";
        String date = utc.getDate();
        
        String title = utc.toTitleCase(pc.getTitle().trim());
        String content = pc.getContent().trim();
        String rankMyPost = pc.getRank();
        String category = pc.getCategory();
        utc.modelTransfer(model);
        
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
                //Long user_id = utc.getUser().getId();
                
                content=content.replaceAll("<_", "<br/><br/><img alt='content image' width='250' height='150' src='/9jaforum/files/dist_img/");
                content=content.replaceAll("_>", "'/><br/><br/>");
                
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
                                    
                                                case "poetic_justice":
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
                                            File pathToFile=new File(path, coverFileName);
                                            coverFile.transferTo(pathToFile);
                                            /*
                                            PostClass postklass = pcr.getOnePost(user_id, date, title);
                                            mor.save(new MessageObject(postklass.getId(), user_id));    //for followers seeing ur posts
                                            */
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
                                    
                                        case "poetic_justice":
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
                                    /*
                                    PostClass postklass = pcr.getOnePost(user_id, date, title);
                                    mor.save(new MessageObject(postklass.getId(), user_id));
                                    */
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
                                    
                                                case "poetic_justice":
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
                                            File pathToFile=new File(path, coverFileName);
                                            coverFile.transferTo(pathToFile);
                                            /*
                                            PostClass postklass = pcr.getOnePost(user_id, date, title);
                                            mor.save(new MessageObject(postklass.getId(), user_id));    //for followers seeing ur posts
                                            */
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
                                    
                                        case "poetic_justice":
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
                                    /*
                                    PostClass postklass = pcr.getOnePost(user_id, date, title);
                                    mor.save(new MessageObject(postklass.getId(), user_id));
                                    */
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
    @RequestParam("cid")Optional<Long> comment_id, @RequestParam("sid")Optional<Long> subcomment_id)
    {
        String ret= null;
        //utc.sessionUserDetails(req);
        Optional<PostClass> pc = pcr.findById(post_id.get());
        
        long id = utc.getUser().getId();
        
        switch(action.orElse("cmt"))
        {
            case "cmt":
            {
                //check if the commenter userrank is equal or greater than the postrank
                //if its true, grank access
                //else, do not grant access
                long userrank = utc.getUser().getUserrank();
                
                if(userrank >= pc.get().getPostrank())
                {
                    model.addAttribute("postclass", new PostClass());
                    model.addAttribute("pos", post_id.get());
                    model.addAttribute("t", title.get());
                    model.addAttribute("p", pg.get());
                    model.addAttribute("page", commentPaginate.get());
                    ret = "pages/commentpage";
                }
                else
                {
                    //ra.addFlashAttribute("alertx", "Your rank is lesser than the post rank");
                    String alert = "Your rank is lesser than the post rank";
                    ret = "redirect:/b_ch?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get()+"&alertx="+alert;
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
                        utc.alterUserRankingParameters(pc.get().getUser_id(), "save_like", ucr);
                    }
                    break;
                    
                    case "liked":
                    {
                        likes = likes - 1;
                        pc.get().setLikes(likes);
                        //decrease the overall likes of the owner of the post by 1
                        //check if his overall likes == 0 first, if its 0, leave it like that
                        utc.alterUserRankingParameters(pc.get().getUser_id(), "save_unlike", ucr);
                    }
                    break;
                    
                    case "unliked":
                    {
                        likes = likes + 1;
                        pc.get().setLikes(likes);
                        //increase the overall likes of the owner of the post by 1
                        utc.alterUserRankingParameters(pc.get().getUser_id(), "save_like", ucr);
                    }
                    break;
                }
                
                pcr.save(pc.get());
                ret = "redirect:/b_ch?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get();
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
                        utc.alterUserRankingParameters(pc.get().getUser_id(), "save_redflag", ucr);
                    }
                    break;
                    
                    case "redflagged":
                    {
                        redflag = redflag - 1;
                        pc.get().setRedflag(redflag);
                        //decrease the overall redflags of the owner of the post by 1
                        //check if his overall redflags == 0 first, if its 0, leave it like that
                        utc.alterUserRankingParameters(pc.get().getUser_id(), "save_unredflag", ucr);
                    }
                    break;
                    
                    case "notredflagged":
                    {
                        redflag = redflag + 1;
                        pc.get().setRedflag(redflag);
                        //increase the overall redflags of the owner of the post by 1
                        utc.alterUserRankingParameters(pc.get().getUser_id(), "save_redflag", ucr);
                    }
                    break;
                }
                
                pcr.save(pc.get());
                ret = "redirect:/b_ch?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get();
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
                        utc.alterUserRankingParameters(pc.get().getUser_id(), "save_star", ucr);
                    }
                    break;
                    
                    case "starred":
                    {
                        star = star - 1;
                        pc.get().setStar(star);
                        //decrease the overall star of the owner of the post by 1
                        //check if his overall star == 0 first, if its 0, leave it like that
                        utc.alterUserRankingParameters(pc.get().getUser_id(), "save_unstar", ucr);
                    }
                    break;
                    
                    case "notstarred":
                    {
                        star = star + 1;
                        pc.get().setStar(star);
                        //increase the overall star of the owner of the post by 1
                        utc.alterUserRankingParameters(pc.get().getUser_id(), "save_star", ucr);
                    }
                    break;
                }
                
                pcr.save(pc.get());
                ret = "redirect:/b_ch?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get();
            }
            break;
            
            case "cmt_s":
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
                    ret = "pages/subcommentpage";
                }
                else
                {
                    //ra.addFlashAttribute("alertx", "Your rank is lesser than the post rank");
                    String alert = "Your rank is lesser than the post rank";
                    ret = "redirect:/b_ch?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get()+"&alertx="+alert;
                }
            }
            break;
            
            case "qte_s":
            {
                long userrank = utc.getUser().getUserrank();
                
                if(userrank >= pc.get().getPostrank())
                {
                    Optional<CommentClass> ccid = ccr.findById(comment_id.get());
                    PostClass poc = new PostClass();
                    String quotedText = ccid.get().getContent();
                    
                    quotedText = quotedText.replaceAll("<br/><br/><img alt='content image' width='250' height='150' src='/9jaforum/files/dist_img/", "<_");
                    quotedText = quotedText.replaceAll("'/><br/><br/>", "_>");
                    
                    poc.setContent_2(quotedText);
                    
                    model.addAttribute("postclass", poc);
                    
                    model.addAttribute("pos", post_id.get());
                    model.addAttribute("cid", comment_id.get());
                    model.addAttribute("t", title.get());
                    model.addAttribute("p", pg.get());
                    model.addAttribute("page", commentPaginate.get());
                    ret = "pages/quotepage";
                }
                else
                {
                    //ra.addFlashAttribute("alertx", "Your rank is lesser than the post rank");
                    String alert = "Your rank is lesser than the post rank";
                    ret = "redirect:/b_ch?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get()+"&alertx="+alert;
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
                        utc.alterUserRankingParameters(cc.get().getUser_id(), "save_like", ucr);
                    }
                    break;
                    
                    case "liked":
                    {
                        likes = likes - 1;
                        cc.get().setLikes(likes);
                        utc.alterUserRankingParameters(cc.get().getUser_id(), "save_unlike", ucr);
                    }
                    break;
                    
                    case "unliked":
                    {
                        likes = likes + 1;
                        cc.get().setLikes(likes);
                        utc.alterUserRankingParameters(cc.get().getUser_id(), "save_like", ucr);
                    }
                    break;
                }
                
                ccr.save(cc.get());
                utc.alterCommentRankingParameters(cc, ccr);
                
                ret = "redirect:/b_ch?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get();
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
                        utc.alterUserRankingParameters(cc.get().getUser_id(), "save_redflag", ucr);
                    }
                    break;
                    
                    case "redflagged":
                    {
                        redflag = redflag - 1;
                        cc.get().setRedflag(redflag);
                        utc.alterUserRankingParameters(cc.get().getUser_id(), "save_unredflag", ucr);
                    }
                    break;
                    
                    case "notredflagged":
                    {
                        redflag = redflag + 1;
                        cc.get().setRedflag(redflag);
                        utc.alterUserRankingParameters(cc.get().getUser_id(), "save_redflag", ucr);
                    }
                    break;
                }
                
                ccr.save(cc.get());
                utc.alterCommentRankingParameters(cc, ccr);
                ret = "redirect:/b_ch?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get();
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
                        utc.alterUserRankingParameters(cc.get().getUser_id(), "save_star", ucr);
                    }
                    break;
                    
                    case "starred":
                    {
                        star = star - 1;
                        cc.get().setStar(star);
                        utc.alterUserRankingParameters(cc.get().getUser_id(), "save_unstar", ucr);
                    }
                    break;
                    
                    case "notstarred":
                    {
                        star = star + 1;
                        cc.get().setStar(star);
                        utc.alterUserRankingParameters(cc.get().getUser_id(), "save_star", ucr);
                    }
                    break;
                }
                
                ccr.save(cc.get());
                utc.alterCommentRankingParameters(cc, ccr);
                ret = "redirect:/b_ch?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get();
            }
            break;
            
            case "shr_s":
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
                        utc.alterUserRankingParameters(cc.get().getUser_id(), "save_share", ucr);
                    }
                    break;
                    
                    case "shared":
                    {
                        shares = shares - 1;
                        cc.get().setShare(shares);
                        utc.alterUserRankingParameters(cc.get().getUser_id(), "save_unshare", ucr);
                    }
                    break;
                    
                    case "unshared":
                    {
                        shares = shares + 1;
                        cc.get().setShare(shares);
                        utc.alterUserRankingParameters(cc.get().getUser_id(), "save_share", ucr);
                    }
                    break;
                }
                
                ccr.save(cc.get());
                utc.alterCommentRankingParameters(cc, ccr);
                ret = "redirect:/b_ch?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get();
            }
            break;
            
            case "edit_s":
            {
                Optional<CommentClass> cc = ccr.findById(comment_id.get());
                String content = cc.get().getContent();
                
                content=content.replaceAll("<br/><br/><img alt='content image' width='250' height='150' src='/9jaforum/files/dist_img/", "<_");
                content=content.replaceAll("'/><br/><br/>", "_>");
                
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
                    ret = "pages/editcommentpage";
                }
                else
                {
                    String alert = "Cannot edit comment";
                    ret = "redirect:/b_ch?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get()+"&alertx="+alert;
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
                ret = "redirect:/b_ch?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get()+"&alertx="+alert;
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
                ret = "redirect:/b_ch?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get()+"&alertx="+alert;
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
                        utc.alterUserRankingParameters(scc.get().getUser_id(), "save_like", ucr);
                    }
                    break;
                    
                    case "liked":
                    {
                        likes = likes - 1;
                        scc.get().setLikes(likes);
                        utc.alterUserRankingParameters(scc.get().getUser_id(), "save_unlike", ucr);
                    }
                    break;
                    
                    case "unliked":
                    {
                        likes = likes + 1;
                        scc.get().setLikes(likes);
                        utc.alterUserRankingParameters(scc.get().getUser_id(), "save_like", ucr);
                    }
                    break;
                }
                
                sccr.save(scc.get());
                utc.alterSubCommentRankingParameters(scc, sccr);
                ret = "redirect:/b_ch?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get();
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
                        utc.alterUserRankingParameters(scc.get().getUser_id(), "save_redflag", ucr);
                    }
                    break;
                    
                    case "redflagged":
                    {
                        redflag = redflag - 1;
                        scc.get().setRedflag(redflag);
                        utc.alterUserRankingParameters(scc.get().getUser_id(), "save_unredflag", ucr);
                    }
                    break;
                    
                    case "notredflagged":
                    {
                        redflag = redflag + 1;
                        scc.get().setRedflag(redflag);
                        utc.alterUserRankingParameters(scc.get().getUser_id(), "save_redflag", ucr);
                    }
                    break;
                }
                
                sccr.save(scc.get());
                utc.alterSubCommentRankingParameters(scc, sccr);
                ret = "redirect:/b_ch?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get();
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
                        utc.alterUserRankingParameters(scc.get().getUser_id(), "save_star", ucr);
                    }
                    break;
                    
                    case "starred":
                    {
                        starred = starred - 1;
                        scc.get().setStar(starred);
                        utc.alterUserRankingParameters(scc.get().getUser_id(), "save_unstar", ucr);
                    }
                    break;
                    
                    case "notstarred":
                    {
                        starred = starred + 1;
                        scc.get().setStar(starred);
                        utc.alterUserRankingParameters(scc.get().getUser_id(), "save_star", ucr);
                    }
                    break;
                }
                
                sccr.save(scc.get());
                utc.alterSubCommentRankingParameters(scc, sccr);
                ret = "redirect:/b_ch?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get();
            }
            break;
            
            case "edit_x":
            {
                Optional<SubCommentClass> scc = sccr.findById(subcomment_id.get());
                String content = scc.get().getContent();
                
                content=content.replaceAll("<br/><br/><img alt='content image' width='250' height='150' src='/9jaforum/files/dist_img/", "<_");
                content=content.replaceAll("'/><br/><br/>", "_>");
                
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
                    ret = "pages/editsubcommentpage";
                }
                else
                {
                    String alert = "Cannot edit comment";
                    ret = "redirect:/b_ch?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get()+"&alertx="+alert;
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
                ret = "redirect:/b_ch?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get()+"&alertx="+alert;
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
                ret = "redirect:/b_ch?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get()+"&alertx="+alert;
            }
            break;
        }
        return ret;
    }
    
    @PostMapping("cmtpost")
    public String cmtPost(@RequestParam("pos")Optional<Long> post_id, @RequestParam("t")Optional<String> title, 
    @RequestParam("p") Optional<Integer> pg, ModelMap model, @RequestParam("page")Optional<Integer> commentPaginate, 
    @ModelAttribute("postclass")PostClass pc, RedirectAttributes ra, @RequestParam("akt")Optional<String> action, 
    @RequestParam("cid")Optional<Long> comment_id, @RequestParam("sid")Optional<Long> subcomment_id)
    {
        String ret = null;
        String[] hideBlocks = {"secondBlock", "thirdBlock"};
        utc.dispBlock(model, "firstBlock", hideBlocks);
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
                
                        content=content.replaceAll("<_", "<br/><br/><img alt='content image' width='250' height='150' src='/9jaforum/files/dist_img/");
                        content=content.replaceAll("_>", "'/><br/><br/>");
                
                        if(!content.matches("\\s*"))
                        {
                            if(content.length() > 1500)
                            {
                                model.addAttribute("pos", post_id.get());
                                model.addAttribute("t", title.get());
                                model.addAttribute("p", pg.get());
                                model.addAttribute("page", commentPaginate.get());
                        
                                model.addAttribute("alert", "Comment must be less than 1500 characters [" + pc.getContent().length() +"]");
                            }
                            else
                            {
                                CommentClass cc = new CommentClass(utc.getUser().getId(), post_id.get(), content, date);
                                ccr.save(cc);
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
                
                        content=content.replaceAll("<_", "<br/><br/><img alt='content image' width='250' height='150' src='/9jaforum/files/dist_img/");
                        content=content.replaceAll("_>", "'/><br/><br/>");
                
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
                            }
                            else
                            {
                                SubCommentClass scc = new SubCommentClass(utc.getUser().getId(), comment_id.get(), content, date);
                                sccr.save(scc);
                                //Notify the owner of the comment here: Very important
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
                
                        content=content.replaceAll("<_", "<br/><br/><img alt='content image' width='250' height='150' src='/9jaforum/files/dist_img/");
                        content=content.replaceAll("_>", "'/><br/><br/>");
                        
                        content_2=content_2.replaceAll("<_", "<br/><br/><img alt='content image' width='250' height='150' src='/9jaforum/files/dist_img/");
                        content_2=content_2.replaceAll("_>", "'/><br/><br/>");
                
                        if(!content.matches("\\s*"))
                        {
                            model.addAttribute("pos", post_id.get());
                            model.addAttribute("cid", comment_id.get());
                            model.addAttribute("t", title.get());
                            model.addAttribute("p", pg.get());
                            model.addAttribute("page", commentPaginate.get());
                                
                            if(content.length() > 1500)
                            {
                                model.addAttribute("alert", "Comment must be less than 1500 characters [" + pc.getContent().length() +"]");
                            }
                            else
                            {
                                Optional<CommentClass> ccid = ccr.findById(comment_id.get());
                                String main_comment = ccid.get().getContent();
                                 
                                if(main_comment.contains(content_2))
                                {
                                    CommentClass cc = new CommentClass(utc.getUser().getId(), post_id.get(), content, date);
                                    ccr.save(cc);
                                    Optional<CommentClass> quickCom = ccr.getExactPost(utc.getUser().getId(), post_id.get(), date);
                                    QuoteObject qobj = new QuoteObject(ccid.get().getUser_id(), quickCom.get().getId(), content_2, ccid.get().getPostdate());
                                    qobjr.save(qobj);
                                    //Notify the owner of the comment here: Very important
                                    return "redirect:/b_ch?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get()+"&alertx=Posted";
                                }
                                else
                                {
                                    //wrong quote
                                    model.addAttribute("alert", "You did not quote @"+ ccid.get().getUsertwo().getUsername() + " properly");
                                }
                            }
                        }
                        else
                        {
                            ra.addFlashAttribute("alertx", "Comment cannot be empty");
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
                
                        content=content.replaceAll("<_", "<br/><br/><img alt='content image' width='250' height='150' src='/9jaforum/files/dist_img/");
                        content=content.replaceAll("_>", "'/><br/><br/>");
                
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
                            }
                            else
                            {
                                Optional<CommentClass> ccObj = ccr.findById(comment_id.get());
                                ccObj.get().setContent(content);
                                ccr.save(ccObj.get());
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
                
                        content=content.replaceAll("<_", "<br/><br/><img alt='content image' width='250' height='150' src='/9jaforum/files/dist_img/");
                        content=content.replaceAll("_>", "'/><br/><br/>");
                
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
                ret = "pages/editcommentpage";
            }
            break;
        }
        return ret;
    }
    
    @GetMapping("/fallback")
    public String getFallbackpage(ModelMap model, HttpServletRequest req)
    {
        utc.modelUser(model);
        utc.modelTransfer(model);
        return "pages/userfallbackpage";
    }
    
    @GetMapping("/flpost")
    public String getFollowedPost(@RequestParam("pg")Optional<Integer> page, HttpServletRequest req, 
    ModelMap model, RedirectAttributes ra)
    {
        utc.modelUser(model);
        int init = 0;
        int end = 1;
        List<PostClass> pcList3 = new LinkedList<>();
        utc.modelTransfer(model);
        
        if(page.get() > 1)
        {
            init = (page.get() - 1) * end;
            end = end * page.get();
        }
        
        List<FollowerObject> followedObj = fobjr.getSelectedFollow(utc.getUser().getId()); //Are you following people? Oya get their ids
        if(!followedObj.isEmpty())  //If you really are following someone
        {
            List<PostClass> followPost = pcr.followersPost(followedObj);
            if(followPost != null)  //If there is really a followed post
            {
                if(followPost.size() < end)
                {
                    end = followPost.size();
                }
                for(int count = init; count < end; count++)
                {
                    pcList3.add(followPost.get(count));
                }
                
                model.addAttribute("followpost", pcList3);
                model.addAttribute("prev", page.get()-1);
                model.addAttribute("next", page.get()+1);
                
                if((page.get()-1) == 0)
                {
                    model.addAttribute("disp1", "none");
                }
                if(pcList3.isEmpty())
                {
                    model.addAttribute("clickagain", "No post available");
                    model.addAttribute("disp2", "none");
                    model.addAttribute("theclass", "realcentertinz");
                }
            }
            else    //If there is no followed post
            {
                ra.addFlashAttribute("alert", "No post available");
                return "redirect:/user/fallback";
            }
        }
        else    //If you do not have followers
        {
            ra.addFlashAttribute("alert", "You really are not following anyone at the moment");
            return "redirect:/user/fallback";
        }
        return "pages/followedpost";
    }
    
    @GetMapping("/inbox")
    public String getInbox(ModelMap model, @RequestParam("pg")Optional<Integer> page,
    RedirectAttributes ra, HttpServletRequest req)
    {
        utc.modelUser(model);
        int init = 0;
        int end = 1;
        List<MessageObject> mobj = new LinkedList<>();
        utc.modelTransfer(model);
        
        if(page.get() > 1)
        {
            init = (page.get() - 1) * end;
            end = end * page.get();
        }
        
        List<MessageObject> mo = mobjr.getMyMessage(utc.getUser().getId());   //Get all followed posts
        utc.modelTransfer(model);
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
            model.addAttribute("prev", page.get()-1);
            model.addAttribute("next", page.get()+1);
                
            if(page.get() - 1 == 0)
            {
                model.addAttribute("disp1", "none");
            }
            if(mobj.isEmpty())
            {
                model.addAttribute("clickagain", "No notifications");
                model.addAttribute("disp2", "none");
                model.addAttribute("theclass", "realcentertinz");
            }
        }
        else    //If there are no messages
        {
            ra.addFlashAttribute("alert", "No notifications");
            return "redirect:/user/fallback";
        }
        return "pages/messagepage";
    }
    
    
    @GetMapping("/rcd")
    public String getRecord(ModelMap model, @RequestParam("pg")Optional<Integer> pgn, 
    RedirectAttributes ra, HttpServletRequest req)
    {
        utc.modelUser(model);
        int init = 0;
        int end = 1;
        List<PostClass> tobj = new LinkedList<>();
        utc.modelTransfer(model);
        
        if(pgn.get() > 1)
        {
            init = (pgn.get() - 1) * end;
            end = end * pgn.get();
        }
        List<PostClass> trend =  pcr.getAllMyPost(utc.getUser().getId());   //Get all my posts
        utc.modelTransfer(model);
        
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
                model.addAttribute("clickagain", "No post available");
                model.addAttribute("disp2", "none");
                model.addAttribute("theclass", "realcentertinz");
            }
        }
        else    //If there are no messages
        {
            ra.addFlashAttribute("alert", "You are yet to make a post");
            return "redirect:/user/fallback";
        }
        
        return "pages/mytrend";
    }
    
    
    @GetMapping("/prf")
    public String editProfile(HttpServletRequest req, ModelMap model)
    {
        String[] hideBlocks = {"firstBlock", "thirdBlock"};
        utc.dispBlock(model, "secondBlock", hideBlocks);
        utc.modelUser(model);
        utc.modelTransfer(model);
        
        model.addAttribute("postclass", new PostClass());
        return "pages/userpage";
    }
    
    /*
    @GetMapping("/ads")
    public String creatAds(HttpServletRequest req, HttpSession session, ModelMap model)
    {
        session = req.getSession();
        final String[] blocks = {"firstblock", "secondblock", "thirdblock", "fourthblock", "sixthblock"};
        utc.dispBlock(session, "fifthblock", blocks);
        
        model.addAttribute("postclass", new PostClass());
        return "pages/userpage";
    }
    
    @GetMapping("/adm")
    public String manageAds(HttpServletRequest req, HttpSession session, ModelMap model)
    {
        session = req.getSession();
        final String[] blocks = {"firstblock", "secondblock", "thirdblock", "fourthblock", "fifthblock"};
        utc.dispBlock(session, "sixthblock", blocks);
        
        model.addAttribute("postclass", new PostClass());
        return "pages/userpage";
    }
    */
    
    @PostMapping("/prfupd")
    public String updatePix(@ModelAttribute("postclass")PostClass pc, ModelMap model, RedirectAttributes ra)
    {
        String ret = null;
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
                        Optional<UserClass> uc = ucr.findById(utc.getUser().getId());
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
            Optional<UserClass> uc = ucr.findById(utc.getUser().getId());
            uc.get().setPix("empty.png");
            ucr.save(uc.get());
        }
        return "redirect:/user/prf";
    }
}