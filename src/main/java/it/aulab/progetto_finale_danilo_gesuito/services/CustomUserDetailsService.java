package it.aulab.progetto_finale_danilo_gesuito.services;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import it.aulab.progetto_finale_danilo_gesuito.models.Role;
import it.aulab.progetto_finale_danilo_gesuito.models.User;
import it.aulab.progetto_finale_danilo_gesuito.repositories.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService{
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        User user = userRepository.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("Credentials not found");
        }
        return new CustomUserDetails(
        user.getId(),
        user.getUsername(),
        user.getEmail(),
        user.getPassword(),
        mapRolesToAuthorities(user.getRoles())
        );
    }
    
    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        Collection<? extends GrantedAuthority> mapRoles = null;
        if (roles.size() != 0) {
            mapRoles = roles.stream()
            .map(role -> new SimpleGrantedAuthority(role.getName()))
            .collect(Collectors.toList());
        } else {
            mapRoles = Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
        }
        return mapRoles;
    }
}      