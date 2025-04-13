package it.aulab.progetto_finale_danilo_gesuito.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.aulab.progetto_finale_danilo_gesuito.models.CareerRequest;
import it.aulab.progetto_finale_danilo_gesuito.models.Role;
import it.aulab.progetto_finale_danilo_gesuito.models.User;
import it.aulab.progetto_finale_danilo_gesuito.repositories.CareerRequestRepository;
import it.aulab.progetto_finale_danilo_gesuito.repositories.RoleRepository;
import it.aulab.progetto_finale_danilo_gesuito.repositories.UserRepository;

@Service
public class CareerRequestServiceImpl implements CareerRequestService {

    @Autowired
    private CareerRequestRepository careerRequestRepository;

    @Autowired
    private EmailServiceImpl emailService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;
    
    @Transactional
    public boolean isRoleAlredyAssigned(User user, CareerRequest careerRequest) {
        List<Long> allUserIds = careerRequestRepository.findAllUserIds();
        
        if (!allUserIds.contains(user.getId())) {
            return false;
        }
        
        List<Long> requests = careerRequestRepository.findByUserId(user.getId());

        return requests.stream().anyMatch(roleId -> roleId.equals(careerRequest.getRole().getId()));
    }

    public void save(CareerRequest careerRequest, User user) {
        careerRequest.setUser(user);
        careerRequest.setIsChecked(false);
        careerRequestRepository.save(careerRequest);

        // Invio mail richiesta del ruolo all'admin
        emailService.sendSimpleEmail("adminAulabpost@admin.com", "Richiesta per ruolo: " + careerRequest.getRole().getName(), "C'è una nuova richiesta di collaborazione da parte di " + user.getUsername());
    }

    @Override
    public void careerAccepted(Long requestId) {
        // recuper la richiesta
        CareerRequest request = careerRequestRepository.findById(requestId).get();

        // dalla richiesta recupero l'utente ela richiesta di ruolo
        User user = request.getUser();
        Role role = request.getRole();

        // recuper tutti i ruoli che l'utente possiede e aggiungo quello nuovo
        List<Role> rolesUser = user.getRoles();
        Role newRole = roleRepository.findByName(role.getName());
        rolesUser.add(newRole);

        // salvo tutte le modifiche
        user.setRoles(rolesUser);
        userRepository.save(user);
        request.setIsChecked(true);
        careerRequestRepository.save(request);

        emailService.sendSimpleEmail(user.getEmail(), "Ruolo abilitato", "Ciao, la tua richiesta di collaborazione è stata accettata dalla nostra amministrazione!");
    }

    @Override
    public CareerRequest find(Long id) {
        return careerRequestRepository.findById(id).get();
    
    }
}
