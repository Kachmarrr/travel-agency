package com.epam.finaltask.controller;

import com.epam.finaltask.repository.TourRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final TourRepository tourRepository;

    public HomeController(TourRepository tourRepository) {
        this.tourRepository = tourRepository;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("tours", tourRepository.findAll());
        return "index"; // thymeleaf шаблон index.html
    }
}
