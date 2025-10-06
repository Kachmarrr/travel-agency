package com.epam.finaltask.controller;

import com.epam.finaltask.DTO.UserDTO;
import com.epam.finaltask.model.enums.Role;
import com.epam.finaltask.model.enums.TourStatus;
import com.epam.finaltask.service.BookingService;
import com.epam.finaltask.service.TourService;
import com.epam.finaltask.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class HomeController {

    private final TourService tourService;
    private final UserService userService;


    public HomeController(TourService tourService, UserService userService) {
        this.tourService = tourService;
        this.userService = userService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("tours", tourService.findAllByStatus(TourStatus.AVAILABLE));
        return "index";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {

        UserDTO userDTO = userService.getUserByUsername(principal.getName());
        model.addAttribute("user", userDTO);

        boolean isManager = userDTO.getRole().equals(Role.MANAGER);

        if (isManager) {
            model.addAttribute("tours", tourService.findAll());
        } else {
            model.addAttribute("tours", tourService.findAllByStatus(TourStatus.AVAILABLE));
        }

        return "dashboard";
    }

    @GetMapping("/profile")
    public String profile(Model model, Principal principal) {
        UserDTO user = userService.getUserByUsername(principal.getName());

        model.addAttribute("user", user);

        // Список турів користувача
        model.addAttribute("userTours", tourService.findAllByUserId(user.getId()));

        return "user/profile";
    }

}
