package com.entr.sbdem.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/fragments")
public class FragmentController {

    @GetMapping("/header")
    public String getHeader(){
        return "header";
    }
    @GetMapping("/footer")
    public String getFooter(){
        return "footer";
    }
}
