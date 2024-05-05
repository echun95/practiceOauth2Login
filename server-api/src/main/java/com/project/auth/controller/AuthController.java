package com.project.auth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("")
public class AuthController {

    @GetMapping("/loginForm")
    public String homePage(){
        return "loginForm";
    }

    @GetMapping("/private")
    public String privatePage(){
        return "privatePage";
    }

    @GetMapping("/admin")
    public String adminPage(){
        return "adminPage";
    }

    @GetMapping("/main")
    public String indexPage(){
        return "index";
    }
}
