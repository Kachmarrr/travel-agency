package com.epam.finaltask.controller;

import com.epam.finaltask.DTO.UserDTO;
import com.epam.finaltask.exception.NotFoundException;
import com.epam.finaltask.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
public class AuthenticationController {

    private final UserService userService;

    public AuthenticationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String getRegistrationForm(Model model) {
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new UserDTO());
        }
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") UserDTO userDTO,
                           BindingResult result,
                           RedirectAttributes redirectAttributes) {

        // 1. (validation)
        if (result.hasErrors()) {
            return "auth/register";
        }

        // 2. (email.check)
        try {
            userService.getUserByEmail(userDTO.getEmail());
            result.rejectValue("email", "error.user", "Email already in use");
            return "auth/register";
        } catch (NotFoundException ignored) {}

        // 3. (username.check)
        try {
            userService.getUserByUsername(userDTO.getUsername());
            result.rejectValue("username", "error.user", "Username already in use");
            return "auth/register";
        } catch (NotFoundException ignored) {}

        // 4. (save user)
        userService.register(userDTO);

        // message for user after redirect
        redirectAttributes.addFlashAttribute("success", "Registration successful. Please log in.");
        return "redirect:/auth/login";
    }

    @PostMapping("/auth/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/";
    }
}
