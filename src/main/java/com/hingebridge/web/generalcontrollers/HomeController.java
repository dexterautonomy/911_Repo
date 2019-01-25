package com.hingebridge.web.generalcontrollers;

import com.google.gson.Gson;
import com.hingebridge.model.CommentClass;
import com.hingebridge.model.DynamicContent;
import com.hingebridge.model.FollowedPostDeleteObject;
import com.hingebridge.model.MessageObject;
import com.hingebridge.model.PagerModel;
import com.hingebridge.model.PostClass;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hingebridge.model.Role;
import com.hingebridge.model.SubCommentClass;
import com.hingebridge.model.UserClass;
import com.hingebridge.model.UserRoleClass;
import com.hingebridge.repository.CommentClassRepo;
import com.hingebridge.repository.FollowedPostDeleteObjectRepo;
import com.hingebridge.repository.MessageObjectRepo;
import com.hingebridge.repository.PostClassRepo;
import com.hingebridge.repository.RoleClassRepo;
import com.hingebridge.repository.SubCommentClassRepo;
import com.hingebridge.repository.UserClassRepo;
import com.hingebridge.repository.UserRoleClassRepo;
import com.hingebridge.utility.AdvertAlgorithmClass;
import com.hingebridge.utility.UtilityClass;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class HomeController
{
    @Autowired
    private UtilityClass utc;
    @Autowired
    private AdvertAlgorithmClass aac;
    @Autowired
    private UserClassRepo ucr;
    @Autowired
    private RoleClassRepo rcr;
    @Autowired
    private UserRoleClassRepo urcr;
    @Autowired
    private PostClassRepo pcr;
    @Autowired
    private CommentClassRepo ccr;
    @Autowired
    private MessageObjectRepo mobjr;
    @Autowired
    private SubCommentClassRepo sccr;
    @Autowired
    private FollowedPostDeleteObjectRepo fpdor;
	
    @GetMapping("/")
    public String getHome(HttpServletRequest req, HttpSession session, ModelMap model, @RequestParam("page") Optional<Integer> page_1)
    {
        aac.displayAdvert(model);   //This line is for adverts
        
        session = req.getSession();
        
        final int INITIAL_PAGE = 0;
        final int INITIAL_PAGE_SIZE = 5;    //pageSize is the offset
        int page = (page_1.orElse(0) < 1 ? INITIAL_PAGE : page_1.get() - 1);    //page is the LIMIT            
        
        Page<PostClass> postpage = pcr.getApprovedPost("mypost", PageRequest.of(page, INITIAL_PAGE_SIZE));
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
        
    	return "pages/home";
    }
	
    @GetMapping("/login")
    public String login(@RequestParam(value="error", required=false)String error, HttpServletRequest req, ModelMap model)
    {
        String[] hideBlocks = {"secondBlock"};
        utc.dispBlock(model, "firstBlock", hideBlocks);
        
    	if(error != null)
	{
            model.addAttribute("error", "Invalid username/password");
            
	}
	return "pages/loginpage";
    }
    
    //4-Stage password reset algorithm
    @GetMapping("rtv") //Gets the password reset page
    public String retrieve(ModelMap model)
    {
        String[] hideBlocks = {"firstBlock"};
        utc.dispBlock(model, "secondBlock", hideBlocks);
        
	return "pages/loginpage";
    }
    
    @PostMapping("rtv_")  //Fill in the form by providing your registered email
    public String retrieveDetails(@RequestParam("email")String email, ModelMap model, HttpServletRequest req)
    {
        String[] hideBlocks = {"firstBlock"};
        utc.dispBlock(model, "secondBlock", hideBlocks);
        
        if(utc.emailExists(email)) //If email exists
        {
            String date = utc.getDate();  //Extra check for link
            BCryptPasswordEncoder bCrypt = new BCryptPasswordEncoder(); //Encoder for email and date on link
            //This is where you use the Java mail API again to send the details to the provided mail
            HttpSession session = req.getSession();
            session.setAttribute("lostAccountEmail", email);  //Puts plain email on session
            session.setAttribute("dateApplied", date);  //Puts the plain date on session too, this will be encrypted and compared with the one clicked on link sent to email provided
            
            String encodedEmail = bCrypt.encode(email);
            String encodedDate = bCrypt.encode(date);
            
            String info = "Please click the link below to reset your password.<br/>";
            String action = "Reset password";
            
            String link = utc.getAppContextPath() + "rtv_2?q_=" + encodedEmail + "&_z="+encodedDate;  //rtv_2: second stage retreival endpoint
            
            utc.sendMail(email, link, info, action);  //Sends the message/mail
            model.addAttribute("error", "A message has been sent to your mail");  //Notification
        }
        else
        {
            model.addAttribute("error", "E-mail does not exist");
        }
        
	return "pages/loginpage";
    }
    
    @GetMapping("rtv_2")  //Email confirmation
    public String retrieveDetails2(@RequestParam("q_")String email, @RequestParam("_z")String date, ModelMap model, HttpServletRequest req, 
            RedirectAttributes ra)
    {
        HttpSession session = req.getSession();
        String lostEmail = (String)session.getAttribute("lostAccountEmail");
        String lostDate = (String)session.getAttribute("dateApplied");
        BCryptPasswordEncoder bCrypt = new BCryptPasswordEncoder();
        
        if(lostEmail != null && lostDate != null)
        {
            //if(email.equals(bCrypt.encode(lostEmail)) && date.equals(bCrypt.encode(lostDate)))
            if(bCrypt.matches(lostEmail, email) && bCrypt.matches(lostDate, date))
            {
                Optional<UserClass> uc = ucr.findByEmail(lostEmail);  //find the owner by email provided
                UserClass uClass = new UserClass();  //To allow password resetting by providing the username already
                uClass.setUsername(uc.get().getUsername()); //sets the username on the page already
                uClass.setEmail(uc.get().getEmail()); //sets the username on the page already
                model.addAttribute("userclass", uClass);  //The form backing object
                String[] hideBlock = {"firstBlock", "secondBlock"};  //Hides the firstBlock and secondBlock two blocks
                utc.dispBlock(model, "", hideBlock);  //I used a th:if so no need for thirdBlock
            }
            else
            {
                ra.addFlashAttribute("error", "Broken link");
                return "redirect:/login";
            }
        }
        else
        {
            ra.addFlashAttribute("error", "Expired link");
            return "redirect:/login";
        }
        
	return "pages/loginpage";
    }
	
    @PostMapping("_res_akt")
    public String resetPassowordProper(@ModelAttribute("userclass")UserClass uClass, ModelMap model, 
    RedirectAttributes ra, HttpServletRequest req)
    {
        BCryptPasswordEncoder bCrypt = new BCryptPasswordEncoder();
        model.addAttribute("userclass", uClass);
        String[] hideBlock = {"firstBlock", "secondBlock"};
        utc.dispBlock(model, "", hideBlock);
        
        if(!utc.passwordCheck(uClass.getPassword()))
        {
            HttpSession session = req.getSession();
            String pswd = bCrypt.encode(uClass.getPassword());
            Optional<UserClass> uc = ucr.findByUsername(uClass.getUsername());
            uc.get().setPassword(pswd);
            ucr.save(uc.get());
            ra.addFlashAttribute("error", "Password resetted successfully");
            session.removeAttribute("lostAccountEmail");
            session.removeAttribute("dateApplied");
            return "redirect:/login";
        }
        else
        {
            model.addAttribute("error", "Password must be 8 characters or more");
        }
        
	return "pages/loginpage";
    }
            
            
    @GetMapping("signup")
    public String signUp(ModelMap model, HttpServletRequest req)
    {
        String ret;
        HttpSession session = req.getSession();
        String username = (String)session.getAttribute("username");
        if(username != null)
        {
            ret = "redirect:/user/login";
        }
        else
        {
            model.addAttribute("signupobject", new UserClass());
            ret = "pages/signup";
        }
        
	return ret;
    }
	
    @PostMapping("/reg_1")
    public String register(@ModelAttribute("signupobject")UserClass uc, HttpServletRequest req, HttpSession session, ModelMap model)
    {
        aac.displayAdvert(model);   //This line is for adverts
        session = req.getSession();
        
        String username = uc.getUsername().trim().toLowerCase();
        String password = uc.getPassword();
        String email = uc.getEmail();
        String gender = uc.getGender();
        String encodedPassword = new BCryptPasswordEncoder().encode(password);
        String encodedUsernameAsEmail = new BCryptPasswordEncoder().encode(username);
        
        model.addAttribute("signupobject", uc);
        
        if(!utc.invalidEntry(username))
        {
            if(!utc.usernameExists(username))
            {
                if(!utc.emailExists(email))
                {
                    if(!utc.passwordCheck(password))
                    {
                        if(!utc.invalidEntry(password))
                        {
                            session.setAttribute("usernameReg", username);
                            session.setAttribute("passwordReg", encodedPassword);
                            session.setAttribute("emailReg", email);
                            session.setAttribute("genderReg", gender);
                            session.setAttribute("confirmemailReg", encodedUsernameAsEmail);
		
                            /*
                                For JAVA MAIL SERVICE:
                                Here, the username, password, email and gender are gotten, added as parameters and sent to the provided mail.
                                the 'active' parameter is set to 1 here.
                                String confirmation= "http://localhost:8090/9jaforum/reg_2/"+uc.getUsername()+"/"+uc.getPassword()+"/"+uc.getEmail()+"/"+"/"+uc.getConfirmemail()+"/"+uc.getGender();
                            */
                            String url = "reg_2/?id_tk="+username+"&u_tk="+encodedPassword+"&e_tk="+email+"&c_tk="+encodedUsernameAsEmail+"&x_tk="+gender;
                            String confirmation = utc.getAppContextPath() + url;
                            
                            String info = "Please click the link below to complete your registration.<br/>";
                            String action = "Complete registration";
                            
                            utc.sendMail(email, confirmation, info, action);  //This is the Java mail API
                            model.addAttribute("error", "Please click the link sent to your mail");
                        }
                        else
                        {
                            model.addAttribute("error", "Password contains invalid character(s)");
                        }
                    }
                    else
                    {
                        model.addAttribute("error", "Password must be atleast 8 characters long");
                    }
                }
                else
                {
                    model.addAttribute("error", "E-mail is in use");
                }
            }
            else
            {
                model.addAttribute("error", "Username already exists");
            }
        }
        else
        {
            model.addAttribute("error", "Username contains invalid characters");
        }
        
	
	return "pages/signup";
    }
	
    @GetMapping("reg_2")
    public String register2(@RequestParam("id_tk")String username, @RequestParam("u_tk")String password, @RequestParam("e_tk")String email, 
    @RequestParam("c_tk")String confirmemail, @RequestParam("x_tk")String gender, HttpServletRequest req, HttpSession session, ModelMap model, 
    RedirectAttributes ra)
    {
        aac.displayAdvert(model);   //This line is for adverts
        
	session = req.getSession();
	String confirmemailx = (String)session.getAttribute("confirmemailReg");
	String usernamex = (String)session.getAttribute("usernameReg");
	String passwordx = (String)session.getAttribute("passwordReg");
	String genderx = (String)session.getAttribute("genderReg");
	String emailx = (String)session.getAttribute("emailReg");
		
	if(confirmemailx != null && usernamex != null && passwordx != null && genderx != null && emailx != null)
	{
            if(confirmemailx.equals(confirmemail))
            {
            	if(usernamex.equals(username))
            	{
                    if(passwordx.equals(password))
                    {
			if(genderx.equals(gender))
			{
                            if(emailx.equals(email))
                            {
				ucr.save(new UserClass(email, gender, username, password, confirmemail));
				Role role=rcr.findByRolename("USER");
				Optional<UserClass> uc2=ucr.findByUsername(username);
						
				urcr.save(new UserRoleClass(uc2.get().getId(), role.getId()));
                                ra.addFlashAttribute("error", "Registration complete, you can login now");
                                
                                session.removeAttribute("usernameReg");
                                session.removeAttribute("passwordReg");
                                session.removeAttribute("emailReg");
                                session.removeAttribute("genderReg");
                                session.removeAttribute("confirmemailReg");
                            }
                            else
                            {
				ra.addFlashAttribute("error", "E-mail was found to be inconsistent");
                            }
			}
			else
			{
                            ra.addFlashAttribute("error", "Gender was found to be inconsistent");
			}
                    }
                    else
                    {
                    	ra.addFlashAttribute("error", "Data was found to be inconsistent");
                    }
		}
		else
		{
                    ra.addFlashAttribute("error", "Username was found to be inconsistent");
		}
            }
            else
            {
		ra.addFlashAttribute("error", "Data was found to be inconsistent");
            }
	}
	else
	{
            ra.addFlashAttribute("error", "Expired session");
	}
	return "redirect:/login";
    }
    
    @GetMapping("/s_ch")
    public String getStory(@RequestParam("pos")Optional<Long> id, @RequestParam("t")Optional<String> title, 
    @RequestParam("p") Optional<Integer> pg, ModelMap model, @RequestParam("page")Optional<Integer> commentPaginate, 
    @RequestParam("alertx")Optional<String> alert, @RequestParam("apvVal")Optional<Integer> trendApproveValue, 
    @RequestParam("pgn") Optional<Integer> pgnx, @RequestParam("cid") Optional<Long> commentid, 
    @RequestParam("spt") Optional<String> separateAction, @RequestParam("cog") Optional<String> cog)
    {
        aac.displayAdvert(model);   //This line is for adverts
        
        String ret = null;
        
        final int INITIAL_PAGE = 0;
        final int INITIAL_PAGE_SIZE = 10;    //pageSize is the offset
        int page = (commentPaginate.orElse(0) < 1 ? INITIAL_PAGE : commentPaginate.get() - 1);    //page is the LIMIT            
        
        Optional<PostClass> pc;
        int tid = trendApproveValue.orElse(1);
        
        switch(tid)
        {
            case 0:
            {
                pc = pcr.getPostReader(id.get(), title.get(), 0);
                
                if(separateAction.orElse(null) != null)
                {
                    String[] hideBlocks = {"firstBlock"};
                    utc.dispBlock(model, "secondBlock", hideBlocks);
                    utc.userModel(model);
                    
                    boolean postExist = fpdor.getReadPostObject(id.get(), utc.getUser().getId());
                    if(!postExist)
                    {
                        FollowedPostDeleteObject fpdo = new FollowedPostDeleteObject(id.get(), utc.getUser().getId(), 1);
                        fpdor.save(fpdo);
                    }
                    
                    model.addAttribute("unapprovedPost", pc.get());
                    model.addAttribute("pgn", pgnx.orElse(1));
                    
                    ret = "pages/followedpost";
                }
                else
                {
                    if(utc.getUser().getId().equals(pc.get().getUser_id()))
                    {
                        String[] hideBlocks = {"firstBlock", "secondBlock", "fourthBlock"};
                        utc.dispBlock(model, "thirdBlock", hideBlocks);
                        utc.userModel(model);
                        model.addAttribute("postclass", new PostClass());
                        model.addAttribute("unapprovedPost", pc.get());
                        model.addAttribute("pgn", pgnx.orElse(1));
                    
                        ret = "pages/userpage";
                    }
                }
            }
            break;
            
            case 1:
            {
                if(separateAction.orElse(null) != null)
                {
                    boolean postExist = fpdor.getReadPostObject(id.get(), utc.getUser().getId());
                    if(!postExist)
                    {
                        FollowedPostDeleteObject fpdo = new FollowedPostDeleteObject(id.get(), utc.getUser().getId(), 1);
                        fpdor.save(fpdo);
                    }
                }
                
                if(commentid.orElse(null) != null)
                {
                    Optional<MessageObject> mo = mobjr.findByCommentid(commentid.get());
                    Optional<CommentClass> cc = ccr.findById(commentid.get());
                    List<SubCommentClass> scc = cc.get().getSubcomment();
                    
                    for(SubCommentClass scc1 : scc)
                    {
                        if(!scc1.getUnread().equals("read"))
                        {
                            scc1.setUnread("read");
                            sccr.save(scc1);
                        }
                    }
                    
                    mo.get().setUnread("read");
                    mobjr.save(mo.get());
                }
                
                pc = pcr.getPostReader(id.get(), title.get(), 1);
                
                utc.updateViews(pc);
                utc.alterUserRankingParameters(pc.get().getUser_id(), "save_view");
                
                Page<CommentClass> cc = ccr.getApprovedComments(id.get(), PageRequest.of(page, INITIAL_PAGE_SIZE));
                PagerModel pgn = new PagerModel(cc.getTotalPages(), cc.getNumber());
                
                model.addAttribute("postclass", pc.get());
                model.addAttribute("commentsExt", cc);
        	model.addAttribute("pgn", pgn);
                
                if(cog.orElse(null) != null)
                {
                    model.addAttribute("cog", cog.get());
                }
        
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
                model.addAttribute("title", title.orElse(""));//important  for comment pagination
                model.addAttribute("pg", pg.orElse(1));//for going back gotten from getHome() method
                model.addAttribute("alertx", alert.orElse(""));
                
                ret = "pages/reader";
            }
            break;
        }
        
        return ret;
    }
    
    @GetMapping("/b_ch")
    public String backDoor(@RequestParam("pos")Optional<Long> id, @RequestParam("t")Optional<String> title, 
    @RequestParam("p") Optional<Integer> pg, ModelMap model, @RequestParam("page")Optional<Integer> commentPaginate, 
    @RequestParam("alertx")Optional<String> alert)
    {
        aac.displayAdvert(model);   //This line is for adverts
        
        final int INITIAL_PAGE = 0;
        final int INITIAL_PAGE_SIZE = 10;    //pageSize is the offset
        int page = (commentPaginate.orElse(0) < 1 ? INITIAL_PAGE : commentPaginate.get() - 1);    //page is the LIMIT            
        
        Optional<PostClass> pc = pcr.getPostReader(id.get(), title.get(), 1);
        Page<CommentClass> cc = ccr.getApprovedComments(id.get(), PageRequest.of(page, INITIAL_PAGE_SIZE));
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
        
        return "pages/reader";
    }
    
    //This is very important for CPC adverts
    @GetMapping(value="/rdr")
    public String updateClick(@RequestParam("uk_")long adId)
    {
        return "redirect:"+ aac.perClick(adId);
    }
    
    @GetMapping(value="/prf_src")
    public String searchUserMethod(@RequestParam("usr")Optional<Long> userId, ModelMap model, HttpServletRequest req)
    {
        String ret;
        aac.displayAdvert(model);   //This line is for adverts
        HttpSession session = req.getSession();
        Optional<UserClass> uc = ucr.findById(userId.get());
        String myUsername = (String)session.getAttribute("username");
        
        if(myUsername != null)
        {
            ret = "redirect:/user/src?uts=" + uc.get().getUsername();
        }
        else
        {
            model.addAttribute("searchedUser", uc.get());
            ret = "pages/profilesearch";
        }
        
        return ret;
    }
    
    @GetMapping("/ajaxUsernameCheck")
    @ResponseBody
    public String checkUsername(@RequestParam("username")String username)
    {
        String outcome = "";
        
        if(utc.usernameExists(username.toLowerCase()))
        {
            outcome = "username exists";
        }
        else if(utc.invalidEntry(username))
        {
            outcome = "invalid entry";
        }
        
        return outcome;
    }
    
    @GetMapping("/ajaxEmailCheck")
    @ResponseBody
    public String checkEmail(@RequestParam("email")String email)
    {
        String outcome = "";
        
        if(utc.emailExists(email))
        {
            outcome = "email exists";
        }
        
        return outcome;
    }
    
    @GetMapping("/ajaxPasswordCheck")
    @ResponseBody
    public String checkPassword(@RequestParam("password")String password)
    {
        String outcome = "";
        
        if(utc.passwordCheck(password))
        {
            outcome = "weak password";
        }
        else if(utc.invalidEntry(password))
        {
            outcome = "invalid entry";
        }
        
        return outcome;
    }
    
    @RequestMapping("/ajaxDynamicFileUpload")
    @ResponseBody
    public boolean dynamicUpload(@RequestParam("dynamicUpload")MultipartFile file)
    {
        try
        {
            String path = utc.getFilePath() + "dist_img";
            File pathToFile = new File(path, file.getOriginalFilename());
            file.transferTo(pathToFile);
            
        }
        catch (IOException | IllegalStateException ex)
        {
            Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return true;
    }
    
            
    @RequestMapping("/ajaxDynamicComment")
    public String dynamicComment(@RequestParam("sent")String sent)
    {
        Gson gson = new Gson();
        DynamicContent dc = gson.fromJson(sent, DynamicContent.class);
        String content = dc.getContent();
        Long post_id = dc.getPos();
        
        content=content.replaceAll("<_", "<br/><br/><img alt='content image' width='250' height='150' src='/9jaforum/files/dist_img/");
        content=content.replaceAll("_>", "'/><br/><br/>");
        
        CommentClass cc = new CommentClass(utc.getUser().getId(), post_id, content, utc.getDate());
        ccr.save(cc);
        return "redirect:/b_ch?pos="+post_id+"&t="+dc.getTitle()+"&page="+dc.getPage()+"&p="+dc.getPg()+"&alertx=Posted";
    }
    
    @RequestMapping("/mresu_b")
    public String moreSubComment(@RequestParam("p2")int paginate, @RequestParam("cid")Long cid, @RequestParam("pos")Long post_id,
    @RequestParam("t")String title, @RequestParam("page")int commentPaginate, @RequestParam("pg")int pg, 
    ModelMap model, @RequestParam("cog") Optional<String> cog)
    {
        aac.displayAdvert(model);
        
        int init = 0;
        int end = 10;
        
        if(paginate > 1)
        {
            init = (paginate - 1) * end;
            end = end * paginate;
        }
        
        Optional<CommentClass> cc = ccr.findById(cid);
        List<SubCommentClass> subcommentList = cc.get().getSubcomment();
        List<SubCommentClass> scc = new LinkedList<>();
        
        if(subcommentList.size() < end)
        {
            end = subcommentList.size();
        }
        
        for(int count = init; count < end; count++)
        {
            scc.add(subcommentList.get(count));
        }
        
        if(!scc.isEmpty())
        {
            model.addAttribute("extraSubComments", scc);
        }
        
        model.addAttribute("cid", cid);
        model.addAttribute("pos", post_id);
        model.addAttribute("t", title);
        model.addAttribute("p", pg);
        model.addAttribute("page", commentPaginate);
        model.addAttribute("currentPage", paginate);
        
        model.addAttribute("prev", paginate - 1);
        model.addAttribute("next", paginate + 1);
        
        if(cog.orElse(null) != null)
        {
            model.addAttribute("cog", cog.get());
        }
                
        if((paginate - 1) == 1)
        {
            model.addAttribute("disp1", "none");
        }
        if(scc.isEmpty())
        {
            model.addAttribute("theClass", "realcentertinz");
            model.addAttribute("alert", "No more comments");
            model.addAttribute("disp2", "none");
        }
        
        return "pages/extrasubcommentpage";
    }
    
    @GetMapping("/linkz")
    public String getOpinion(HttpServletRequest req, HttpSession session, ModelMap model, @RequestParam("page") Optional<Integer> page_1, 
    @RequestParam("cat") Optional<String> link_category)
    {
        String tagHeader = "";
        
        aac.displayAdvert(model);
        session = req.getSession();
        
        final int INITIAL_PAGE = 0;
        final int INITIAL_PAGE_SIZE = 5;
        int page = (page_1.orElse(0) < 1 ? INITIAL_PAGE : page_1.get() - 1);
        
        Page<PostClass> postpage = null;
        PagerModel pgn;
        
        switch(link_category.orElse("opinion"))
        {
            case "opinion":
            {
                postpage = pcr.getApprovedPost("opinion", PageRequest.of(page, INITIAL_PAGE_SIZE));
                tagHeader = "Opinion";
            }
            break;
            
            case "memelogic":
            {
                postpage = pcr.getApprovedPost("memelogic", PageRequest.of(page, INITIAL_PAGE_SIZE));
                tagHeader = "Memelogic";
            }
            break;
            
            case "poem_sarc":
            {
                postpage = pcr.getApprovedPost("poem_sarc", PageRequest.of(page, INITIAL_PAGE_SIZE));
                tagHeader = "Poem/Sarcasm";
            }
            break;
            
            case "d_sexes":
            {
                postpage = pcr.getApprovedPost("zex_battle", PageRequest.of(page, INITIAL_PAGE_SIZE));
                tagHeader = "The Sexes";
            }
            break;
        }
        
        if(postpage != null)
        {
            pgn = new PagerModel(postpage.getTotalPages(), postpage.getNumber());
            model.addAttribute("postpage", postpage);
            model.addAttribute("pgn", pgn);
            model.addAttribute("pg", (page+1));
            model.addAttribute("cog", link_category.get());
            model.addAttribute("tagHeader", tagHeader);
        
            if(postpage.getNumber() == 0)
            {
                model.addAttribute("disp1", "none");
            }
            if(postpage.getNumber() + 1 == postpage.getTotalPages())
            {
                model.addAttribute("disp2", "none");
            }
        }
        
        return "pages/cat_page";
    }
}