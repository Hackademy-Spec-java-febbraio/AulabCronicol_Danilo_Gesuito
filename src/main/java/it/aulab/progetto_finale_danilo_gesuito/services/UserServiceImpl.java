package it.aulab.progetto_finale_danilo_gesuito.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.aulab.progetto_finale_danilo_gesuito.dtos.UserDto;
import it.aulab.progetto_finale_danilo_gesuito.models.Role;
import it.aulab.progetto_finale_danilo_gesuito.models.User;
import it.aulab.progetto_finale_danilo_gesuito.repositories.RoleRepository;
import it.aulab.progetto_finale_danilo_gesuito.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;
    
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    @Override
    public void saveUser(UserDto userDto, RedirectAttributes redirectAttributes, HttpServletRequest request, HttpServletResponse response) {
        User user = new User();
        user.setUsername(userDto.getFirstName() + " " + userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        Role role = roleRepository.findByName("ROLE_USER");
        user.setRoles(List.of(role));
        
        userRepository.save(user);
    }
}
