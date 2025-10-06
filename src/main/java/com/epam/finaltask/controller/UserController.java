package com.epam.finaltask.controller;

import com.epam.finaltask.DTO.UserDTO;
import com.epam.finaltask.model.enums.Role;
import com.epam.finaltask.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@AllArgsConstructor
@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping()
    public String findAllUsers(Model model) {
        List<UserDTO> users = userService.findAllUsers();
        model.addAttribute("users", users);
        return "user/admin";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/make-manager")
    public String makeManager(@PathVariable("id") Long id, RedirectAttributes ra) {
        userService.changeUserRole(id, Role.MANAGER);
        ra.addFlashAttribute("success", "User role updated");
        return "redirect:/users";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/remove-manager")
    public String removeManager(@PathVariable("id") Long id, RedirectAttributes ra) {
        userService.changeUserRole(id, Role.USER);
        ra.addFlashAttribute("success", "User role updated");
        return "redirect:/users";
    }
}
