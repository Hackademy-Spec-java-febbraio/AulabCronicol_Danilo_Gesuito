package it.aulab.progetto_finale_danilo_gesuito.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import it.aulab.progetto_finale_danilo_gesuito.repositories.RoleRepository;

@Controller
@RequestMapping("/operations")
public class OperationController {
    
    @Autowired
    private RoleRepository roleRepository;

    
}
