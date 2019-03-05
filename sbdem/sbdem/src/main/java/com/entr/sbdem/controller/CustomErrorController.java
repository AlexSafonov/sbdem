package com.entr.sbdem.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.support.SessionFlashMapManager;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller
@Log4j2
public class CustomErrorController implements ErrorController {

    //Here we support post and get requests
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, @ModelAttribute("msg") String message){
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Exception exception = (Exception)request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        if(exception!=null)
            log.warn("ERROR_EXCEPTION " + exception.getMessage().toString());
        if(status!=null){
            log.warn("ERROR_STATUS_CODE " + status.toString());
            Integer errorStatusCode = Integer.valueOf(status.toString());
            if(errorStatusCode == HttpStatus.NOT_FOUND.value()){
                return "404";
            }
            else if(errorStatusCode == HttpStatus.FORBIDDEN.value()) {
                return "403";
            }
        }
        return "error";
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }

    public String get404(){return "404";}

}
