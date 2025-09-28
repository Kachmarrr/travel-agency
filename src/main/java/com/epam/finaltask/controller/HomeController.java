package com.epam.finaltask.controller;

import com.epam.finaltask.DTO.UserDTO;
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
    private final BookingService bookingService;

    public HomeController(TourService tourService, UserService userService, BookingService bookingService) {
        this.tourService = tourService;
        this.userService = userService;
        this.bookingService = bookingService;
    }


    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("tours", tourService.findAll());
        return "index";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {

        UserDTO userDTO = userService.getUserByUsername(principal.getName());
        model.addAttribute("user", userDTO);

        model.addAttribute("tours", tourService.findAll());

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
