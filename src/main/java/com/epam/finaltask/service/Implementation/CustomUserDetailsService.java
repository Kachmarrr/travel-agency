package com.epam.finaltask.service.Implementation;

import com.epam.finaltask.exception.NotFoundException;
import com.epam.finaltask.model.User;
import com.epam.finaltask.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws NotFoundException {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User with username:" + username + " not found!"));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword()) // обов'язково закодований пароль
                .roles(user.getRole().name()) // USER, ADMIN і тд
                .build();
    }
}
