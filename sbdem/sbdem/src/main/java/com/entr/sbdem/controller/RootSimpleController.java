package com.entr.sbdem.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


@Controller
public class RootSimpleController {


    @GetMapping("/login")
    public String loginPage(){
        return "login";
    }

   /* @RequestMapping("/logout")
    public String logoutPage(HttpServletRequest request, HttpServletResponse response){
        HttpSession session = request.getSession(false);
        SecurityContextHolder.clearContext();
        session= request.getSession(false);
        if(session != null) {
            session.invalidate();
        }
        for(Cookie cookie : request.getCookies()) {
            cookie.setMaxAge(0);
        }
        return "redirect:/login";
    }
*/
    @GetMapping(value = {"/", "/index"})
    public String get(){ return "index"; }

    @GetMapping("/json-to-table")
    public String getJsonToTable(){
        return "json-to-table";
    }


}
