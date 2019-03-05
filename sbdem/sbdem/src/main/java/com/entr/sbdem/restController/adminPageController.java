package com.entr.sbdem.restController;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class adminPageController {

    @GetMapping("/admin")
    public String getAdminPage(){
        return "admin";
    }
}
