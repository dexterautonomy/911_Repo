package com.hingebridge.web.generalcontrollers;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErrorHandlingController implements ErrorController
{
    @Override
    public String getErrorPath()
    {
        return "/error";
    }
    
    @RequestMapping("/error")
    public String errorPath(HttpServletRequest req, ModelMap model)
    {
        model.addAttribute("error", "Page not available");
        
        /*
        Object status = req.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        String ret = null;
        if(status != null)
        {
            Integer statusCode = Integer.valueOf(status.toString());
            
            if(statusCode == HttpStatus.FORBIDDEN.value())
            {
                ret = "errorpages/errorpage1";
            }            
            else if(statusCode == HttpStatus.NOT_FOUND.value())
            {
                ret = "errorpages/errorpage2";
            }
            else
            {
                
            }
        }
        */
        return "errorpage/error";
    }
}