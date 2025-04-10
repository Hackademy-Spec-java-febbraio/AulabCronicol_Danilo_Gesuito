package it.aulab.progetto_finale_danilo_gesuito.controllers;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.aulab.progetto_finale_danilo_gesuito.services.ArticleService;
import it.aulab.progetto_finale_danilo_gesuito.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import it.aulab.progetto_finale_danilo_gesuito.models.User;
import it.aulab.progetto_finale_danilo_gesuito.dtos.ArticleDto;
import it.aulab.progetto_finale_danilo_gesuito.dtos.UserDto;

@Controller
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ArticleService articleService;
    
    // Rotta della Home //
    @GetMapping("/")
    public String home(Model viewModel){
        
        List<ArticleDto> articles = articleService.readAll();
        
        // Ordina gli articoli per data in ordine decrescente
        articles.sort(Comparator.comparing(
        ArticleDto::getPublishDate,
        Comparator.nullsLast(Comparator.naturalOrder()) // Gestisce i null
        ).reversed()); // Inverte l'ordine per avere i più recenti prima
        
        // Prendi solo gli ultimi 3 *dopo* aver ordinato correttamente
        List<ArticleDto> lastThreeArticles = articles.stream().limit(3).collect(Collectors.toList());
        viewModel.addAttribute("articles", lastThreeArticles);
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
        return "redirect:/";
    }
    
    // Rotta per la ricerca degli articoli in base all'utente
    @GetMapping("/search/{id}")
    public String userArticesSearch(@PathVariable("id") Long id, Model viewModel) {
        User user = userService.find(id);
        viewModel.addAttribute("title", "Tutti gli articoli trovati per utente " + user.getUsername());
        
        List<ArticleDto> articles = articleService.searchByAuthor(user);
        viewModel.addAttribute("articles", articles);
        
        return "article/articles";
    }
}
