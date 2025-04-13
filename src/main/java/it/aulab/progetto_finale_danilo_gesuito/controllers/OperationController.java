package it.aulab.progetto_finale_danilo_gesuito.controllers;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.aulab.progetto_finale_danilo_gesuito.models.CareerRequest;
import it.aulab.progetto_finale_danilo_gesuito.models.Role;
import it.aulab.progetto_finale_danilo_gesuito.models.User;
import it.aulab.progetto_finale_danilo_gesuito.repositories.RoleRepository;
import it.aulab.progetto_finale_danilo_gesuito.repositories.UserRepository;
import it.aulab.progetto_finale_danilo_gesuito.services.CareerRequestService;



@Controller
@RequestMapping("/operations")
public class OperationController {
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CareerRequestService careerRequestService;
    
    // Rotta per la creazione di una richiesta di collaborazione
    @GetMapping("/career/request")
    public String careerRequestCreate(Model viewModel) {
        viewModel.addAttribute("title", "Fai la tua richiesta");
        viewModel.addAttribute("careerRequest", new CareerRequest());
        
        List<Role> roles = roleRepository.findAll();
        // Elimino la possibilità di scegliere il ruolo user nel form di selezione
        roles.removeIf(e -> e.getName().equals("ROLE_USER"));
        viewModel.addAttribute("roles", roles);
        
        return ("career/requestForm");
    }
    
    @PostMapping("career/request/save")
    public String careerRequestStore(@ModelAttribute("careerRequest") CareerRequest careerRequest, Principal principal, RedirectAttributes redirectAttributes) {
        
        User user = userRepository.findByEmail(principal.getName());
        
        if (careerRequestService.isRoleAlredyAssigned(user, careerRequest)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Il ruolo selezionato e' gia' stato assegnato");
            return "redirect:/";
        }
        
        careerRequestService.save(careerRequest, user);
        
        redirectAttributes.addFlashAttribute("successMessage", "Richiesta inviata con successo!");
        return "redirect:/";
    }
    
    @GetMapping("/career/request/detail/{id}")
    public String careerRequestDetail(@PathVariable("id") Long id, Model viewModel) {
        viewModel.addAttribute("title", "Dettagli richiesta");
        viewModel.addAttribute("request", careerRequestService.find(id));
        return "career/requestDetail";
    }

    @PostMapping("/career/request/accept/{requestId}")
    public String careerRequestAccept(@PathVariable Long requestId, RedirectAttributes redirectAttributes) {
        careerRequestService.careerAccepted(requestId);
        redirectAttributes.addFlashAttribute("successMessage", "Ruolo abilitato per l'utente!");
        
        return "redirect:/admin/dashboard";
    }
}