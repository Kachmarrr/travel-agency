package com.epam.finaltask.controller;

import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.model.enums.Role;
import com.epam.finaltask.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/auth")
public class AuthenticationController {

    private final UserService userService;

    public AuthenticationController(UserService userService) {
        this.userService = userService;
    }

    // SHOW registration form
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        if (!model.containsAttribute("userDTO")) {
            model.addAttribute("userDTO", new UserDTO());
        }
        return "auth/register"; // thymeleaf template: src/main/resources/templates/auth/register.html
    }

    // PROCESS registration
    @PostMapping("/register")
    public String processRegister(
            @Valid @ModelAttribute("userDTO") UserDTO userDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            // preserve validation errors and input for redirect (optional) or return view directly
            return "auth/register";
        }

        try {
            userService.register(userDTO);
            redirectAttributes.addFlashAttribute("success", "Реєстрація пройшла успішно. Увійдіть, будь ласка.");
            return "redirect:/auth/login";
        } catch (Exception ex) {
            // show error on the form
            bindingResult.reject("registerError", ex.getMessage());
            return "auth/register";
        }
    }

    // SHOW login page (if you use Spring Security formLogin you can map it here)
    @GetMapping("/login")
    public String showLogin(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            Model model) {
        if (error != null) {
            model.addAttribute("error", "Невірний логін або пароль.");
        }
        if (logout != null) {
            model.addAttribute("msg", "Ви успішно вийшли.");
        }
        return "auth/login"; // thymeleaf template: auth/login.html
    }

    // SHOW current user's profile
    @GetMapping("/profile")
    public String showProfile(Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/auth/login";
        }

        String username = principal.getName();
        UserDTO user = userService.getUserByUsername(username);
        // don't expose password in the form
        user.setPassword(null);
        model.addAttribute("user", user);
        return "auth/profile"; // thymeleaf template: auth/profile.html
    }

    // UPDATE current user's profile (here we call updateUser which currently updates password in your service)
    @PostMapping("/profile")
    public String updateProfile(
            Principal principal,
            @Valid @ModelAttribute("user") UserDTO userDTO,
            BindingResult bindingResult,
            RedirectAttributes ra
    ) {
        if (principal == null) {
            return "redirect:/auth/login";
        }

        if (bindingResult.hasErrors()) {
            return "auth/profile";
        }

        try {
            String username = principal.getName();
            // Depending on your DTO, you may avoid sending fields you don't want changed (e.g., role, balance).
            userService.updateUser(username, userDTO);
            ra.addFlashAttribute("success", "Профіль оновлено.");
            return "redirect:/auth/profile";
        } catch (Exception ex) {
            bindingResult.reject("updateError", ex.getMessage());
            return "auth/profile";
        }
    }

    // ADMIN: change role (POST from admin form). Requires security restrictions (ROLE_ADMIN).
    @PostMapping("/change-role")
    public String changeRole(@RequestParam("userId") Long userId,
                             @RequestParam("role") String role,
                             RedirectAttributes ra) {
        try {
            Role newRole = Role.valueOf(role); // ensure frontend sends valid enum name
            userService.changeUserRole(userId, newRole);
            ra.addFlashAttribute("success", "Роль оновлено.");
        } catch (IllegalArgumentException ex) {
            ra.addFlashAttribute("error", "Невірна роль або id: " + ex.getMessage());
        } catch (Exception ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/admin/users"; // або куди у тебе перелік користувачів
    }
}
