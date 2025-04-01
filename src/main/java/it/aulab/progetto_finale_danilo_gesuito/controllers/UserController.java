package it.aulab.progetto_finale_danilo_gesuito.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import it.aulab.progetto_finale_danilo_gesuito.services.UserService;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class UserController {
    
    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String home(){
        return "home";
    }
}
