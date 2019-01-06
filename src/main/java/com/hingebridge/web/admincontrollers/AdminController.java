package com.hingebridge.web.admincontrollers;

import com.hingebridge.model.AdvertObject;
import com.hingebridge.model.CommentClass;
import com.hingebridge.model.FollowedPostDeleteObject;
import com.hingebridge.model.InboxObject;
import com.hingebridge.model.MessageObject;
import com.hingebridge.model.PagerModel;
import com.hingebridge.model.PostClass;
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
import com.hingebridge.repository.PostReactionClassRepo;
import com.hingebridge.repository.QuoteObjectRepo;
import com.hingebridge.repository.ReplyObjectRepo;
import com.hingebridge.repository.RoleClassRepo;
import com.hingebridge.repository.SubCommentClassRepo;
import com.hingebridge.repository.SubCommentReactionClassRepo;
import com.hingebridge.repository.UserClassRepo;
import com.hingebridge.repository.UserRoleClassRepo;
import com.hingebridge.utility.AdvertAlgorithmClass;
import com.hingebridge.utility.UtilityClass;
import com.hingebridge.web.usercontrollers.UserController;
import java.io.File;
import java.io.IOException;
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
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@PreAuthorize("hasRole('ADMIN')")
@Controller
@RequestMapping("/securedadminLogin")
public class AdminController
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
    
    @GetMapping("/entry")
    public String getAdminPage(HttpServletRequest req, HttpSession session, ModelMap model, @RequestParam("page") Optional<Integer> page_1)
    {
        aac.displayAdvert(model);   //This line is for adverts
        session = req.getSession();
        
        /*
        String[] hideBlocks = {"secondBlock", "thirdBlock"};
        utc.dispBlock(model, "firstBlock", hideBlocks);
        model.addAttribute("postclass", new PostClass());
        */
        utc.adminModel(model);
        utc.sessionUserDetails(req);
        //utc.modelTransfer(model);
        
        final int INITIAL_PAGE = 0;
        final int INITIAL_PAGE_SIZE = 10;
        int page = (page_1.orElse(0) < 1 ? INITIAL_PAGE : page_1.get() - 1);
        
        Page<PostClass> postpage = pcr.getAdminApprovedPost(PageRequest.of(page, INITIAL_PAGE_SIZE));
        PagerModel pgn = new PagerModel(postpage.getTotalPages(), postpage.getNumber());
        
        model.addAttribute("postpage", postpage);
	model.addAttribute("pgn", pgn);
        model.addAttribute("pg", (page+1)); //for going back needed in controller s_ch or getStory() method
        
        if(postpage.getNumber() == 0)
        {
            model.addAttribute("disp1", "none");
        }
        if(postpage.getNumber() + 1 == postpage.getTotalPages())
        {
            model.addAttribute("disp2", "none");
        }
        
    	return "adminpages/adminhome";
    }
    
    @GetMapping("/s_ch_admin_")
    public String getStory(@RequestParam("pos")Optional<Long> id, @RequestParam("p") Optional<Integer> pg, ModelMap model, 
    @RequestParam("page")Optional<Integer> commentPaginate, @RequestParam("t")Optional<String> title)
    {
        aac.displayAdvert(model);   //This line is for adverts
        final int INITIAL_PAGE = 0;
        final int INITIAL_PAGE_SIZE = 15;
        int page = (commentPaginate.orElse(0) < 1 ? INITIAL_PAGE : commentPaginate.get() - 1);    //page is the LIMIT            
        
        Optional<PostClass> pc = pcr.getAdminPostReader(id.get(), title.get());
        
        utc.updateViews(pc);
        utc.alterUserRankingParameters(pc.get().getUser_id(), "save_view");
                
        Page<CommentClass> cc = ccr.getCommentsForAdmin(id.get(), PageRequest.of(page, INITIAL_PAGE_SIZE));
        PagerModel pgn = new PagerModel(cc.getTotalPages(), cc.getNumber());
                
        model.addAttribute("postclass", pc.get());
        model.addAttribute("commentsExt", cc);
        model.addAttribute("pgn", pgn);
        
        if(cc.getNumber() == 0)
        {
            model.addAttribute("disp1", "none");
        }
        if(cc.getNumber() + 1 == cc.getTotalPages())
        {
            model.addAttribute("disp2", "none");
        }
        if(cc.getTotalPages() == 0)
        {
            model.addAttribute("dispAlpha", "none");
        }
                
        model.addAttribute("pos", id.orElse(1l));//important for comment pagination
        model.addAttribute("pg", pg.orElse(1));//for going back gotten from getHome() method
        model.addAttribute("title", title.orElse(""));
        
        return "adminpages/adminreader";
    }
    
    @GetMapping("/_cmt_admin_getedge")
    public String comment(@RequestParam("pos")Optional<Long> post_id, @RequestParam("t")Optional<String> title, 
    @RequestParam("p") Optional<Integer> pg, ModelMap model, @RequestParam("page")Optional<Integer> commentPaginate, 
    @RequestParam("akt")Optional<String> action, HttpServletRequest req, RedirectAttributes ra, 
    @RequestParam("cid")Optional<Long> comment_id, @RequestParam("sid")Optional<Long> subcomment_id)
    {
        aac.displayAdvert(model);   //This line is for adverts
        String ret= null;
        utc.sessionUserDetails(req);    //Do not ever remove this
        Optional<PostClass> pc = pcr.findById(post_id.get());
        
        switch(action.orElse("cmt"))
        {
            case "cmt":
            {
                model.addAttribute("postclass", new PostClass());
                model.addAttribute("pos", post_id.get());
                model.addAttribute("t", title.get());
                model.addAttribute("p", pg.get());
                model.addAttribute("page", commentPaginate.get());
                ret = "adminpages/admincommentpage";
                    
            }
            break;
            
            case "apvornot":
            {
                String alert = null;
                switch(pc.get().getApproved())
                {
                    case 0:
                    {
                        pc.get().setApproved(1);
                        alert = "Approved";
                    }
                    break;
                    
                    case 1:
                    {
                        pc.get().setApproved(0);
                        alert = "Taken down";
                    }
                    break;
                }
                pcr.save(pc.get());
                ret = "redirect:b_ch_admin_backdoor?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get()+"&alertx="+alert;
            }
            break;
            
            case "lk":
            {
                long likes = pc.get().getLikes();
                likes = likes + 1;
                pc.get().setLikes(likes);
                pcr.save(pc.get());
                utc.alterUserRankingParameters(pc.get().getUser_id(), "save_like");
                pcr.save(pc.get());
                ret = "redirect:b_ch_admin_backdoor?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get();
            }
            break;
            
            case "flg":
            {
                long redflag = pc.get().getRedflag();
                redflag = redflag + 1;
                pc.get().setRedflag(redflag);
                utc.alterUserRankingParameters(pc.get().getUser_id(), "save_redflag");
                pcr.save(pc.get());
                ret = "redirect:b_ch_admin_backdoor?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get();
            }
            break;
            
            case "str":
            {
                long star = pc.get().getStar();
                star = star + 1;
                pc.get().setStar(star);
                utc.alterUserRankingParameters(pc.get().getUser_id(), "save_star");
                pcr.save(pc.get());
                ret = "redirect:b_ch_admin_backdoor?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get();
            }
            break;
            
            case "cmt_s":
            {
                model.addAttribute("postclass", new PostClass());
                model.addAttribute("pos", post_id.get());
                model.addAttribute("cid", comment_id.get());
                model.addAttribute("t", title.get());
                model.addAttribute("p", pg.get());
                model.addAttribute("page", commentPaginate.get());
                ret = "adminpages/adminsubcommentpage";
            }
            break;
            
            case "qte_s":
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
                ret = "adminpages/adminquotepage";
            }
            break;
            
            case "lk_s":
            {
                Optional<CommentClass> cc = ccr.findById(comment_id.get());
                long likes = cc.get().getLikes();
                likes = likes + 1;
                cc.get().setLikes(likes);
                utc.alterUserRankingParameters(cc.get().getUser_id(), "save_like");
                ccr.save(cc.get());
                utc.alterCommentRankingParameters(cc);
                
                ret = "redirect:b_ch_admin_backdoor?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get();
            }
            break;
            
            case "flg_s":
            {
                Optional<CommentClass> cc = ccr.findById(comment_id.get());
                long redflag = cc.get().getRedflag();
                redflag = redflag + 1;
                cc.get().setRedflag(redflag);
                utc.alterUserRankingParameters(cc.get().getUser_id(), "save_redflag");
                ccr.save(cc.get());
                utc.alterCommentRankingParameters(cc);
                ret = "redirect:b_ch_admin_backdoor?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get();
            }
            break;
            
            case "str_s":
            {
                Optional<CommentClass> cc = ccr.findById(comment_id.get());
                long star = cc.get().getStar();
                star = star + 1;
                cc.get().setStar(star);
                utc.alterUserRankingParameters(cc.get().getUser_id(), "save_star");
                ccr.save(cc.get());
                utc.alterCommentRankingParameters(cc);
                ret = "redirect:b_ch_admin_backdoor?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get();
            }
            break;
            
            case "shr_s":
            {
                Optional<CommentClass> cc = ccr.findById(comment_id.get());
                long shares = cc.get().getShare();
                shares = shares + 1;
                cc.get().setShare(shares);
                utc.alterUserRankingParameters(cc.get().getUser_id(), "save_share");
                ccr.save(cc.get());
                utc.alterCommentRankingParameters(cc);
                ret = "redirect:b_ch_admin_backdoor?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get();
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
                    ret = "adminpages/admineditcommentpage";
                }
                else
                {
                    String alert = "Cannot edit comment";
                    ret = "redirect:b_ch_admin_backdoor?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get()+"&alertx="+alert;
                }
            }
            break;
            
            case "dlt_s":
            {
                String alert;
                Optional<CommentClass> cc = ccr.findById(comment_id.get());
                cc.get().setApproved(0);
                ccr.save(cc.get());
                alert = "Deleted";
                ret = "redirect:b_ch_admin_backdoor?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get()+"&alertx="+alert;
            }
            break;
            
            case "dlt_Admin":
            {
                String alert;
                Optional<CommentClass> cc = ccr.findById(comment_id.get());
                cc.get().setApproved(0);
                ccr.save(cc.get());
                alert = "Deleted";
                ret = "redirect:b_ch_admin_backdoor?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get()+"&alertx="+alert;
            }
            break;
            
            case "lk_x":
            {
                Optional<SubCommentClass> scc = sccr.findById(subcomment_id.get());
                long likes = scc.get().getLikes();
                likes = likes + 1;
                scc.get().setLikes(likes);
                utc.alterUserRankingParameters(scc.get().getUser_id(), "save_like");
                sccr.save(scc.get());
                utc.alterSubCommentRankingParameters(scc, sccr);
                ret = "redirect:b_ch_admin_backdoor?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get();
            }
            break;
            
            case "flg_x":
            {
                Optional<SubCommentClass> scc = sccr.findById(subcomment_id.get());
                long redflag = scc.get().getRedflag();
                redflag = redflag + 1;
                scc.get().setRedflag(redflag);
                utc.alterUserRankingParameters(scc.get().getUser_id(), "save_redflag");
                sccr.save(scc.get());
                utc.alterSubCommentRankingParameters(scc, sccr);
                ret = "redirect:b_ch_admin_backdoor?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get();
            }
            break;
            
            case "str_x":
            {
                Optional<SubCommentClass> scc = sccr.findById(subcomment_id.get());
                long starred = scc.get().getStar();
                starred = starred + 1;
                scc.get().setStar(starred);
                utc.alterUserRankingParameters(scc.get().getUser_id(), "save_star");
                sccr.save(scc.get());
                utc.alterSubCommentRankingParameters(scc, sccr);
                ret = "redirect:b_ch_admin_backdoor?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get();
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
                    ret = "adminpages/admineditsubcommentpage";
                }
                else
                {
                    String alert = "Cannot edit comment";
                    ret = "redirect:b_ch_admin_backdoor?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get()+"&alertx="+alert;
                }
            }
            break;
            
            case "dlt_x":
            {
                String alert;
                Optional<SubCommentClass> scc = sccr.findById(subcomment_id.get());
                scc.get().setApproved(0);
                sccr.save(scc.get());
                alert = "Deleted";
                ret = "redirect:b_ch_admin_backdoor?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get()+"&alertx="+alert;
            }
            break;
            
            case "dlt_Admin_2":
            {
                String alert;
                Optional<SubCommentClass> scc = sccr.findById(subcomment_id.get());
                scc.get().setApproved(0);
                sccr.save(scc.get());
                alert = "Deleted";
                ret = "redirect:b_ch_admin_backdoor?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get()+"&alertx="+alert;
            }
            break;
        }
        return ret;
    }
    
    @PostMapping("_compost_admin_algebra")
    public String cmtPost(@RequestParam("pos")Optional<Long> post_id, @RequestParam("t")Optional<String> title, 
    @RequestParam("p") Optional<Integer> pg, ModelMap model, @RequestParam("page")Optional<Integer> commentPaginate, 
    @ModelAttribute("postclass")PostClass pc, RedirectAttributes ra, @RequestParam("akt")Optional<String> action, 
    @RequestParam("cid")Optional<Long> comment_id, @RequestParam("sid")Optional<Long> subcomment_id)
    {
        aac.displayAdvert(model);   //This line is for adverts
        
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
                                return "redirect:b_ch_admin_backdoor?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get()+"&alertx=Posted";
                            }
                        }
                        else
                        {
                            ra.addFlashAttribute("alertx", "Comment cannot be empty");
                        }
                    }
                    break;
                }
                ret = "adminpages/admincommentpage";
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
                                Optional<CommentClass> cc = ccr.findById(comment_id.get());
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
                                
                                return "redirect:b_ch_admin_backdoor?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get()+"&alertx=Posted";
                            }
                        }
                        else
                        {
                            ra.addFlashAttribute("alertx", "Comment cannot be empty");
                        }
                    }
                    break;
                }
                ret = "adminpages/adminsubcommentpage";
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
                                    
                                    /*
                                    //Notify the owner of the comment here: Very important
                                    Optional<CommentClass> cLass = ccr.findById(comment_id.get());
                                    String postlink = "s_ch?pos="+post_id.get()+"&t="+title.get()+"&p="+pg.get()+"&cid="+comment_id.get()+"#"+comment_id.get();
                                    utc.updateInbox(mobjr, cLass.get().getUser_id(), comment_id.get(), postlink);
                                    */
                                    
                                    return "redirect:b_ch_admin_backdoor?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get()+"&alertx=Posted";
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
                ret = "adminpages/adminquotepage";
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
                                return "redirect:b_ch_admin_backdoor?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get()+"&alertx=Posted";
                            }
                        }
                        else
                        {
                            ra.addFlashAttribute("alertx", "Comment cannot be empty");
                        }
                    }
                    break;
                }
                ret = "adminpages/admineditcommentpage";
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
                                return "redirect:b_ch_admin_backdoor?pos="+post_id.get()+"&t="+title.get()+"&page="+commentPaginate.get()+"&p="+pg.get()+"&alertx=Posted";
                            }
                        }
                        else
                        {
                            ra.addFlashAttribute("alertx", "Comment cannot be empty");
                        }
                    }
                    break;
                }
                ret = "adminpages/admineditsubcommentpage";
            }
            break;
        }
        return ret;
    }
    
    @GetMapping("/b_ch_admin_backdoor")
    public String backDoor(@RequestParam("pos")Optional<Long> id, @RequestParam("t")Optional<String> title, 
    @RequestParam("p") Optional<Integer> pg, ModelMap model, @RequestParam("page")Optional<Integer> commentPaginate, 
    @RequestParam("alertx")Optional<String> alert)
    {
        aac.displayAdvert(model);   //This line is for adverts
        
        final int INITIAL_PAGE = 0;
        final int INITIAL_PAGE_SIZE = 15;    //pageSize is the offset
        int page = (commentPaginate.orElse(0) < 1 ? INITIAL_PAGE : commentPaginate.get() - 1);    //page is the LIMIT            
        
        Optional<PostClass> pc = pcr.getAdminPostReader(id.get(), title.get());
        Page<CommentClass> cc = ccr.getCommentsForAdmin(id.get(), PageRequest.of(page, INITIAL_PAGE_SIZE));
        PagerModel pgn = new PagerModel(cc.getTotalPages(), cc.getNumber());
        
        model.addAttribute("commentsExt", cc);
	model.addAttribute("pgn", pgn);
        
        if(cc.getNumber() == 0)
        {
            model.addAttribute("disp1", "none");
        }
        if(cc.getNumber() + 1 == cc.getTotalPages())
        {
            model.addAttribute("disp2", "none");
        }
        if(cc.getTotalPages() == 0)
        {
            model.addAttribute("dispAlpha", "none");
        }
        
        model.addAttribute("postclass", pc.get());
        
        model.addAttribute("pos", id.orElse(1L));//important for comment pagination
        model.addAttribute("title", title.orElse(""));//important  for comment pagination
        model.addAttribute("pg", pg.orElse(1));//for going back gotten from getHome() method
        model.addAttribute("alertx", alert.orElse(""));
        
        return "adminpages/adminreader";
    }
    
    //This one takes name
    @RequestMapping(value="/_src_admin_math_chem")
    public String searchUser2(@RequestParam("uts")String username, ModelMap model)
    {
        aac.displayAdvert(model);   //This line is for adverts
        utc.adminModel(model);
        
        //utc.modelTransfer(model);
        String usernameParam = username.trim().toLowerCase();
        Optional<UserClass> uc = ucr.findByUsername(usernameParam);
        
        if(uc.orElse(null) != null)
        {
            if(!usernameParam.equals(utc.getUser().getUsername()))
            {
                model.addAttribute("searchedUser", uc.get());
            }
            else
            {
                model.addAttribute("alert", "System Administrator");
                model.addAttribute("nothing", "realcentertinz");
            }
        }
        else
        {
            model.addAttribute("alert", "Username does not exist");
            model.addAttribute("nothing", "realcentertinz");
        }
        
        return "adminpages/adminprofilesearch";
    }
    
    //This one takes id
    @GetMapping(value="/_src_admin_math_physics")
    public String searchUser1(@RequestParam("usr")Optional<Long> userId, ModelMap model)
    {
        aac.displayAdvert(model);   //This line is for adverts
        //utc.modelTransfer(model);
        utc.adminModel(model);
        Optional<UserClass> uc = ucr.findById(userId.get());
        if(!uc.get().getUsername().equals(utc.getUser().getUsername()))
        {
            model.addAttribute("searchedUser", uc.get());
        }
        else
        {
            model.addAttribute("alert", "System Administrator");
            model.addAttribute("nothing", "realcentertinz");
        }
        
        return "adminpages/adminprofilesearch";
    }
    
    /*
    @GetMapping("/home_of_admin")
    public String userHomePage(HttpServletRequest req, ModelMap model)
    {
        aac.displayAdvert(model);   //This line is for adverts
        
        //String[] hideBlocks = {"secondBlock", "thirdBlock"};
        //utc.dispBlock(model, "firstBlock", hideBlocks);
        utc.adminModel(model);
        utc.sessionUserDetails(req);    //very important
        //utc.modelTransfer(model);
        model.addAttribute("postclass", new PostClass());
        
        if(utc.checkPostBan())
        {
            return "redirect:/user/inbox?pg=1";
        }
        
        return "pages/userpage";
    }
    */
    
    @GetMapping("/_manage_ads_toon")
    public String viewAllAds(HttpServletRequest req, ModelMap model, @RequestParam("pg")Optional<Integer> pg)
    {
        aac.displayAdvert(model);   //This line is for adverts
        utc.adminModel(model);
        utc.sessionUserDetails(req);    //very important
        
        final int INITIAL_PAGE = 0;
        final int INITIAL_PAGE_SIZE = 5;
        int page = (pg.orElse(0) < 1 ? INITIAL_PAGE : pg.get() - 1);
        
        Page<AdvertObject> advertObj = aor.getAdminAdvert(PageRequest.of(page, INITIAL_PAGE_SIZE));
        PagerModel pgn = new PagerModel(advertObj.getTotalPages(), advertObj.getNumber());
        
        model.addAttribute("unExpiredAdObjList", advertObj);
	model.addAttribute("pgn", pgn);
        
        if(advertObj.getNumber() == 0)
        {
            model.addAttribute("disp1", "none");
        }
        if(advertObj.getNumber() + 1 == advertObj.getTotalPages())
        {
            model.addAttribute("disp2", "none");
        }
        
        return "adminpages/adminadspage";
    }
    
    @GetMapping("/_manage_edit_ad")
    public String editAds(HttpServletRequest req, ModelMap model, @RequestParam("pos")Optional<Long> pos,
    @RequestParam("pg")Optional<Integer> pg)
    {
        aac.displayAdvert(model);   //This line is for adverts
        utc.adminModel(model);
        utc.sessionUserDetails(req);    //very important
        
        Optional<AdvertObject> ao = aor.findById(pos.get());
        
        if(ao.orElse(null) != null)
        {
            switch(ao.get().getApprove())
            {
                case 0:
                {
                    ao.get().setApprove(1);
                }
                break;
                
                case 1:
                {
                    ao.get().setApprove(0);
                }
                break;
            }
            aor.save(ao.get());
        }
        
        return "redirect:_manage_ads_toon?pg="+pg.get();
    }
    
    @GetMapping("/_inbox_admin")
    public String getInbox(ModelMap model, @RequestParam("pg")Optional<Integer> page_1,
    RedirectAttributes ra, HttpServletRequest req)
    {
        aac.displayAdvert(model);
        utc.adminModel(model);
        
        final int INITIAL_PAGE = 0;
        final int INITIAL_PAGE_SIZE = 10;
        int page = (page_1.orElse(0) < 1 ? INITIAL_PAGE : page_1.get() - 1);
        
        List<InboxObject> adminInboxSize = ior.getAdminInboxSize();
        Page<InboxObject> adminInboxObj = ior.getAdminInbox(PageRequest.of(page, INITIAL_PAGE_SIZE));
        PagerModel pgn = new PagerModel(adminInboxObj.getTotalPages(), adminInboxObj.getNumber());
        
        if(!adminInboxSize.isEmpty())
        {
            model.addAttribute("adminInboxObj", adminInboxObj);
            model.addAttribute("pgn", pgn);
            model.addAttribute("pg", page_1.get());
        
            if(adminInboxObj.getNumber() == 0)
            {
                model.addAttribute("disp1", "none");
            }
            if(adminInboxObj.getNumber() + 1 == adminInboxObj.getTotalPages())
            {
                model.addAttribute("disp2", "none");
            }
        }
        else
        {
            model.addAttribute("nothing", "realcentertinz");
            model.addAttribute("alertFallback", "No messages");
        }
        
        return "adminpages/admininboxpage";
    }
    
    @RequestMapping("/_admin_inbox_rkt_")
    public String actInbox(ModelMap model, @RequestParam("mid")Optional<Long> inboxId,
    @RequestParam("pgn")Optional<Integer> page, @RequestParam("akt")Optional<String> action,
    RedirectAttributes ra, HttpServletRequest req, @RequestParam("rid")Optional<Long> replyId, 
    @ModelAttribute("replyObjectPost")Optional<PostClass> pc)
    {
        aac.displayAdvert(model);   //This line is for adverts
        
        switch(action.get())
        {
            case "dlt_Mxx":
            {
                Optional<InboxObject> inboxObj = ior.findById(inboxId.get());
                if(inboxObj.orElse(null) != null)
                {
                    inboxObj.get().setDeleteAdminFlag(1);
                    inboxObj.get().setAdminRead(1);
                    ior.save(inboxObj.get());
                }
                else
                {
                    ra.addFlashAttribute("alert", "Cannot execute action");
                }
            }
            break;
            
            case "dlt_Mzz":
            {
                Optional<ReplyObject> replyObj = ror.findById(replyId.get());
                if(replyObj.orElse(null) != null)
                {
                    replyObj.get().setDeleteAdminFlag(1);
                    ror.save(replyObj.get());
                }
                else
                {
                    ra.addFlashAttribute("alert", "Cannot execute action");    
                }
            }
            break;
            
            case "rep_":
            {
                Optional<InboxObject> inboxObj = ior.findById(inboxId.get());
                if(inboxObj.orElse(null) != null)
                {
                    inboxObj.get().setAdminRead(1);
                    ior.save(inboxObj.get());
                    model.addAttribute("modelMID", inboxId.get());
                    model.addAttribute("modelPGN", page.get());
                    model.addAttribute("replyObjectPost", new PostClass());
                    utc.adminModel(model);
                    return "adminpages/admininboxpage";
                }
                else
                {
                    ra.addFlashAttribute("alert", "Cannot execute action"); 
                }
            }
            break;
                    
            case "rep_post":
            {
                ReplyObject replyObject = new ReplyObject(inboxId.get(), utc.getDate(), pc.get().getContent());
                ror.save(replyObject);
            }
            break;
        }
        
        return "redirect:_inbox_admin?pg="+page.get();
    }
    
    @RequestMapping("/_banUser")
    public String banUser(ModelMap model, @RequestParam("usr")Optional<Long> userId,
    @RequestParam("akt")Optional<Integer> action)
    {
        aac.displayAdvert(model);   //This line is for adverts
        utc.adminModel(model);
        
        Optional<UserClass> uc = ucr.findById(userId.get());
        if(uc.orElse(null) != null)
        {
            switch(action.get())
            {
                case 1:
                {
                    switch(uc.get().getCommentban())
                    {
                        case 0:
                        {
                            uc.get().setCommentban(1);
                        }
                        break;
                        
                        case 1:
                        {
                            uc.get().setCommentban(0);
                        }
                        break;
                    }
                }
                break;
            
                case 2:
                {
                    switch(uc.get().getPostban())
                    {
                        case 0:
                        {
                            uc.get().setPostban(1);
                        }
                        break;
                        
                        case 1:
                        {
                            uc.get().setPostban(0);
                        }
                        break;
                    }
                }
                break;
            }
            
            ucr.save(uc.get());
        }
        else
        {
            
        }
        
        return "redirect:_src_admin_math_physics?usr="+userId.get();
    }
}