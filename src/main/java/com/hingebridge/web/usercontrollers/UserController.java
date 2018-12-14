package com.hingebridge.web.usercontrollers;

import com.hingebridge.model.CommentClass;
import com.hingebridge.model.FollowerObject;
import com.hingebridge.model.PagerModel;
import com.hingebridge.model.MessageObject;
import com.hingebridge.model.PostClass;
import com.hingebridge.repository.CommentClassRepo;
import com.hingebridge.repository.FollowerObjectRepo;
import com.hingebridge.repository.MessageObjectRepo;
import com.hingebridge.repository.PostClassRepo;
import com.hingebridge.repository.PostLikeClassRepo;
import com.hingebridge.utility.UtilityClass;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

@PreAuthorize("hasRole('USER')")
@Controller
@RequestMapping("/user")
public class UserController
{
    @Autowired
    private UtilityClass utc;
    @Autowired
    private PostClassRepo pcr;
    @Autowired
    private CommentClassRepo ccr;
    @Autowired
    private PostLikeClassRepo plcr;
    @Autowired
    private FollowerObjectRepo fobjr;
    @Autowired
    private MessageObjectRepo mobjr;
    
    @GetMapping("/login")
    public String userHomePage(Authentication auth, HttpServletRequest req, HttpSession session, ModelMap model)
    {
        session = req.getSession();
        session.setAttribute("username", utc.getUser().getUsername());
        model.addAttribute("postclass", new PostClass());
        return "pages/userpage";
    }
    
    @PostMapping("/postcontrol")
    public String userPostControl(@ModelAttribute("postclass")PostClass pc, ModelMap model, RedirectAttributes ra) throws IOException
    {
        String path = utc.getFilePath()+"dist_img";
        String date = utc.getDate();
        
        String title = utc.toTitleCase(pc.getTitle().trim());
        String content = pc.getContent().trim();
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
    
    @GetMapping("cmt")
    public String comment(@RequestParam("pos")Optional<Long> post_id, @RequestParam("t")Optional<String> title, 
    @RequestParam("p") Optional<Integer> pg, ModelMap model, @RequestParam("page")Optional<Integer> commentPaginate, 
    @RequestParam("akt")Optional<String> action)
    {
        String ret= null;
        
        switch(action.orElse("cmt"))
        {
            case "cmt":
            {
                model.addAttribute("postclass", new PostClass());
                model.addAttribute("pos", post_id.get());
                model.addAttribute("t", title.get());
                model.addAttribute("p", pg.get());
                model.addAttribute("page", commentPaginate.get());
                ret = "pages/commentpage";
            }
            break;
            
            case "lk":
            {
                Optional<PostClass> pc = pcr.findById(post_id.get());   //gets the post object
                String check = plcr.likedBefore(post_id.get(), utc.getUser().getId());  //has this user liked this post before now?
                
                switch(check)
                {
                    case "":
                    {
                        Long likes = pc.get().getLikes();
                        likes = likes + 1;
                        pc.get().setLikes(likes);
                        pcr.save(pc.get());
                        ret = "redirect:/s_ch?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get();
                    }
                    break;
                    
                    case "liked":
                    {
                        Long likes = pc.get().getLikes();
                        likes = likes - 1;
                        pc.get().setLikes(likes);
                        pcr.save(pc.get());
                        ret = "redirect:/s_ch?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get();
                    }
                    break;
                    
                    case "unliked":
                    {
                        Long likes = pc.get().getLikes();
                        likes = likes + 1;
                        pc.get().setLikes(likes);
                        pcr.save(pc.get());
                        ret = "redirect:/s_ch?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get();
                    }
                    break;
                }
            }
            break;
        }
        return ret;
    }
    
    @PostMapping("cmtpost")
    public String cmtPost(@RequestParam("pos")Optional<Long> post_id, @RequestParam("t")Optional<String> title, 
    @RequestParam("p") Optional<Integer> pg, ModelMap model, @RequestParam("page")Optional<Integer> commentPaginate, 
    @ModelAttribute("postclass")PostClass pc, RedirectAttributes ra)
    {
        String path = utc.getFilePath()+"dist_img";
        String date = utc.getDate();
        String content = pc.getContent().trim();
        
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
                        return "redirect:/s_ch?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get()+"&alertx=Posted";
                    }
                }
                else
                {
                    ra.addFlashAttribute("alertx", "Comment cannot be empty");
                }
            }
            break;
        }
        
        return "pages/commentpage";
    }
    
    @GetMapping("/fallback")
    public String getFallbackpage()
    {
        return "pages/userfallbackpage";
    }
    
    
    /*
    @GetMapping("/flpost")
    public String getInbox(HttpServletRequest req, HttpSession session, ModelMap model, @RequestParam("pg")Optional<Integer> page,
    RedirectAttributes ra)
    {
        final int INITIAL_PAGE = 0;
        final int INITIAL_PAGE_SIZE = 1;    //Change to 100 later
        int pagex = (page.orElse(0) < 1 ? INITIAL_PAGE : page.get() - 1);
        
        List<FollowerObject> followedObj = fobjr.getSelectedFollow(utc.getUser().getId()); //Are you following people? Oya get their ids
        if(!followedObj.isEmpty())  //If you really are following someone
        {
            Page<PostClass> followPost = pcr.followersPost(followedObj, PageRequest.of(pagex, INITIAL_PAGE_SIZE));   //Get all followed posts
            
            if(followPost != null)  //If there is really a followed post
            {
                if(followPost.isEmpty())
                {
                    model.addAttribute("clickagain", "No followed post within this block, click next/previous");
                }
                PagerModel pgn = new PagerModel(followPost.getTotalPages(), followPost.getNumber());
                model.addAttribute("followpost", followPost);
                model.addAttribute("pgn", pgn);
                if(followPost.getNumber() == 0)
                {
                    model.addAttribute("disp1", "none");
                }
                if(followPost.getNumber() + 1 == followPost.getTotalPages())
                {
                    model.addAttribute("disp2", "none");
                }
            }
            else    //If there is no followed post
            {
                ra.addFlashAttribute("alert", "Followed post is empty");
                return "redirect:/user/fallback";
            }
        }
        else    //If you do not have followers
        {
            ra.addFlashAttribute("alert", "Empty inbox, follow people to see what they post");
            return "redirect:/user/fallback";
        }
        return "pages/followedpost";
    }
    */
    
    @GetMapping("/flpost")
    public String getInbox(HttpServletRequest req, HttpSession session, ModelMap model, @RequestParam("pg")Optional<Integer> page,
    RedirectAttributes ra)
    {
        int init = 0;
        int end = 5;
        List<PostClass> pcList3 = new LinkedList<>();
        
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
                    if(followPost.get(count) != null)
                    {
                        pcList3.add(followPost.get(count));
                    }
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
                    model.addAttribute("clickagain", "People you followed have no post yet");
                    model.addAttribute("disp2", "none");
                }
            }
            else    //If there is no followed post
            {
                ra.addFlashAttribute("alert", "No followed post available");
                return "redirect:/user/fallback";
            }
        }
        else    //If you do not have followers
        {
            ra.addFlashAttribute("alert", "Empty inbox, follow people to see what they post");
            return "redirect:/user/fallback";
        }
        return "pages/followedpost";
    }
    
    
    @GetMapping("/inbox")
    public String getRecord(HttpServletRequest req, HttpSession session, ModelMap model, @RequestParam("pg")Optional<Integer> page,
    RedirectAttributes ra)
    {
        final int INITIAL_PAGE = 0;
        final int INITIAL_PAGE_SIZE = 1;    //Change to 100 later
        int pagex = (page.orElse(0) < 1 ? INITIAL_PAGE : page.get() - 1);
        
        Page<MessageObject> mo = mobjr.getMyMessage(utc.getUser().getId(), PageRequest.of(pagex, INITIAL_PAGE_SIZE));   //Get all followed posts
        if(mo != null)  //If there are messages
        {
            if(mo.isEmpty())
            {
                model.addAttribute("clickagain", "No notifications within this block, click next/previous");
            }
            PagerModel pgn = new PagerModel(mo.getTotalPages(), mo.getNumber());
            model.addAttribute("messageobj", mo);
            model.addAttribute("pgn", pgn);
            if(mo.getNumber() == 0)
            {
                model.addAttribute("disp1", "none");
            }
            if(mo.getNumber() + 1 == mo.getTotalPages())
            {
                model.addAttribute("disp2", "none");
            }
        }
        else    //If there are no messages
        {
            ra.addFlashAttribute("clickagain", "No notifications");
            return "redirect:/user/fallback";
        }
        return "pages/messagepage";
    }
    
    /*
    @GetMapping("/rcd")
    public String getRecord(HttpServletRequest req, HttpSession session, ModelMap model)
    {
        session = req.getSession();
        final String[] blocks = {"firstblock", "secondblock", "fourthblock", "fifthblock", "sixthblock"};
        utc.dispBlock(session, "thirdblock", blocks);
        
        model.addAttribute("postclass", new PostClass());
        return "pages/userpage";
    }
    
    @GetMapping("/prf")
    public String editProfile(HttpServletRequest req, HttpSession session, ModelMap model)
    {
        session = req.getSession();
        final String[] blocks = {"firstblock", "secondblock", "thirdblock", "fifthblock", "sixthblock"};
        utc.dispBlock(session, "fourthblock", blocks);
        
        model.addAttribute("postclass", new PostClass());
        return "pages/userpage";
    }
    
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
    
}