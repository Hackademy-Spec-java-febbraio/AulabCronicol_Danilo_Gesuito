package it.aulab.progetto_finale_danilo_gesuito.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import it.aulab.progetto_finale_danilo_gesuito.dtos.UserDto;
import it.aulab.progetto_finale_danilo_gesuito.models.User;
import it.aulab.progetto_finale_danilo_gesuito.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;




@Controller
public class UserController {
    
    @Autowired
    private UserService userService;
    
    // Rotta della Home //
    @GetMapping("/")
    public String home(){
        return "home";
    }

    // Rotta della registrazione //
    @GetMapping("/register")
    public String register(Model model){
        model.addAttribute("user", new UserDto());
        return "auth/register";
    }
    
    @GetMapping("/login")
    public String login(){
        return "auth/login";
    }

    // Rotta del salvataggio della registrazione //
    @PostMapping("/register/save")
    public String registration(@Valid @ModelAttribute("user") UserDto userDto,
    BindingResult result,
    Model model,
    RedirectAttributes redirectAttributes,
    HttpServletRequest request, HttpServletResponse response) {
        
        User existingUser = userService.findUserByEmail(userDto.getEmail());
        
        if (existingUser != null && existingUser.getEmail() != null && existingUser.getEmail().isEmpty()) {
            result.rejectValue("email", null,"There is already an account registered with the same email");
        }
        
        if (result.hasErrors()) {
            model.addAttribute("user", userDto);
            return "auth/register";
        }

        userService.saveUser(userDto, redirectAttributes, request, response);

        redirectAttributes.addFlashAttribute("successMessage", "Registrazione avvenuta con successo!");
        return "redirect:/register?success";
    }
}
