package com.hingebridge.web.generalcontrollers;

import com.hingebridge.model.CommentClass;
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
import com.hingebridge.model.UserClass;
import com.hingebridge.model.UserRoleClass;
import com.hingebridge.repository.CommentClassRepo;
import com.hingebridge.repository.PostClassRepo;
import com.hingebridge.repository.RoleClassRepo;
import com.hingebridge.repository.UserClassRepo;
import com.hingebridge.repository.UserRoleClassRepo;
import com.hingebridge.utility.UtilityClass;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class HomeController
{
    @Autowired
    private UtilityClass utc;
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
	
    @GetMapping("/")
    public String getHome(HttpServletRequest req, HttpSession session, ModelMap model, @RequestParam("page") Optional<Integer> page_1)
    {
        session = req.getSession();
        final int INITIAL_PAGE = 0;
        final int INITIAL_PAGE_SIZE = 5;    //pageSize is the offset
        int page = (page_1.orElse(0) < 1 ? INITIAL_PAGE : page_1.get() - 1);    //page is the LIMIT            
        
        Page<PostClass> postpage = pcr.getApprovedPost("memelogic", PageRequest.of(page, INITIAL_PAGE_SIZE));
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
	
    @GetMapping("login")
    public String login(@RequestParam(value="error", required=false)String error, ModelMap model)
    {
    	if(error!=null)
	{
            model.addAttribute("error", "Invalid username/password");
	}
	return "pages/loginpage";
    }
	
    @GetMapping("signup")
    public String signUp(ModelMap model)
    {
	model.addAttribute("signupobject", new UserClass());
	return "pages/signup";
    }
	
    @PostMapping("reg_1")
    public String register(@ModelAttribute("signupobject")UserClass uc, HttpServletRequest req, HttpSession session, ModelMap model)
    {
	String confirmemail = new BCryptPasswordEncoder().encode(uc.getUsername());
	session = req.getSession();
	session.setAttribute("confirmemail", confirmemail);
	session.setAttribute("username", uc.getUsername());
	session.setAttribute("password", uc.getPassword());
	session.setAttribute("gender", uc.getGender());
	session.setAttribute("email", uc.getEmail());
		
	/*
		For JAVA MAIL SERVICE:
		Here, the username, password, email and gender are gotten, added as parameters and sent to the provided mail.
		the 'active' parameter is set to 1 here.
		String confirmation= "http://localhost:8090/9jaforum/reg_2/"+uc.getUsername()+"/"+uc.getPassword()+"/"+uc.getEmail()+"/"+"/"+uc.getConfirmemail()+"/"+uc.getGender();
	*/
		
	/*
	try
	{
		String url = "id_tk="+ URLEncoder.encode(uc.getUsername(), "UTF-8") + 
		"&u_tk=" + URLEncoder.encode(uc.getPassword(), "UTF-8") + "&e_tk=" + 
		URLEncoder.encode(uc.getEmail(), "UTF-8") + "&c_tk=" + 
		URLEncoder.encode(confirmemail, "UTF-8") + "&x_tk=" + 
		URLEncoder.encode(uc.getGender(), "UTF-8");
		
		String confirmation = "http://localhost:8090/9jaforum/reg_2/?" + url;
		model.addAttribute("check", confirmation);
	} 
	catch (UnsupportedEncodingException e) 
	{
		e.printStackTrace();
	}
	*/
		
	String url = "id_tk="+uc.getUsername()+"&u_tk="+uc.getPassword()+"&e_tk="+uc.getEmail()+"&c_tk="+confirmemail+"&x_tk="+uc.getGender();
	String confirmation = "http://localhost:8090/9jaforum/reg_2/?" + url;
	model.addAttribute("check", confirmation);
	
	return "pages/home";
    }
	
    @GetMapping("reg_2")
    public String register2(@RequestParam("id_tk")String username, @RequestParam("u_tk")String password, @RequestParam("e_tk")String email, 
    @RequestParam("c_tk")String confirmemail, @RequestParam("x_tk")String gender, HttpServletRequest req, HttpSession session, ModelMap model)
    {
	session = req.getSession();
	String confirmemailx = (String)session.getAttribute("confirmemail");
	String usernamex = (String)session.getAttribute("username");
	String passwordx = (String)session.getAttribute("password");
	String genderx = (String)session.getAttribute("gender");
	String emailx = (String)session.getAttribute("email");
		
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
                            }
                            else
                            {
				model.addAttribute("check", "E-mail was found to be inconsistent");
                            }
			}
			else
			{
                            model.addAttribute("check", "Gender was found to be inconsistent");
			}
                    }
                    else
                    {
                    	model.addAttribute("check", "Data was found to be inconsistent");
                    }
		}
		else
		{
                    model.addAttribute("check", "Username was found to be inconsistent");
		}
            }
            else
            {
		model.addAttribute("check", "Data was found to be inconsistent");
            }
	}
	else
	{
            model.addAttribute("check", "Session has expired");
	}
	return "pages/home";
    }
    
    @GetMapping("/s_ch")
    public String getStory(@RequestParam("pos")Optional<Long> id, @RequestParam("t")Optional<String> title, 
    @RequestParam("p") Optional<Integer> pg, ModelMap model, @RequestParam("page")Optional<Integer> commentPaginate, 
    @RequestParam("alertx")Optional<String> alert, @RequestParam("apvVal")Optional<Integer> trendApproveValue, 
    @RequestParam("pgn") Optional<Integer> pgnx)
    {
        String ret = null;
        
        final int INITIAL_PAGE = 0;
        final int INITIAL_PAGE_SIZE = 15;    //pageSize is the offset
        int page = (commentPaginate.orElse(0) < 1 ? INITIAL_PAGE : commentPaginate.get() - 1);    //page is the LIMIT            
        
        Optional<PostClass> pc;    // = pcr.getPostReader(id.get(), title.get());
        int tid = trendApproveValue.orElse(1);
        
        switch(tid)
        {
            case 0:
            {
                pc = pcr.getPostReader(id.get(), title.get(), 0);
                if(utc.getUser().getId().equals(pc.get().getUser_id()))
                {
                    String[] hideBlocks = {"firstBlock", "secondBlock"};
                    utc.dispBlock(model, "thirdBlock", hideBlocks);
                    utc.modelUser(model);
                    utc.modelTransfer(model);
                    model.addAttribute("postclass", new PostClass());
                    model.addAttribute("unapprovedPost", pc.get());
                    model.addAttribute("pgn", pgnx.orElse(1));
                    
                    ret = "pages/userpage";
                }
                else
                {
                    //cannot do it
                }
            }
            break;
            
            case 1:
            {
                pc = pcr.getPostReader(id.get(), title.get(), 1);
                
                utc.updateViews(pc);
                utc.alterUserRankingParameters(pc.get().getUser_id(), "save_view", ucr);
                
                Page<CommentClass> cc = ccr.getApprovedComments(id.get(), PageRequest.of(page, INITIAL_PAGE_SIZE));
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
        final int INITIAL_PAGE = 0;
        final int INITIAL_PAGE_SIZE = 15;    //pageSize is the offset
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
}