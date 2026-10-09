package com.pitospies.pitospieswebservice.services;

import com.pitospies.pitospieswebservice.config.PasswordEncoderConfig;
import com.pitospies.pitospieswebservice.models.Role;
import com.pitospies.pitospieswebservice.models.User;
import com.pitospies.pitospieswebservice.repositories.RoleRepository;
import com.pitospies.pitospieswebservice.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(username);
        System.out.println("test");
        if (user == null) {
            throw new UsernameNotFoundException("Invalid username or password.");
        }
        System.out.println("test");

        user.setRoles(userRepository.findUserRoles(user.getId()));
        System.out.println("test");

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                getAuthorities(user.getRoles())
        );
    }

    public void registerUser(User user) {
        Role role = roleRepository.findRoleByName("user");
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        userRepository.saveWithRole(user, role);
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Collection<String> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
    }
}
