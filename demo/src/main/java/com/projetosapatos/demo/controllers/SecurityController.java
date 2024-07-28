package com.projetosapatos.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SecurityController {
    @GetMapping("/login")
        public String getLoginPage(){
        return "login";
    }
    @GetMapping("/logout")
        public String getLogoutPage(){
        return "logout";
    }
}