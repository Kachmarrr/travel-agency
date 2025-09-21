package com.epam.finaltask.controller;

import com.epam.finaltask.repository.TourRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/tours")
public class TourController {

    private final TourRepository tourRepository;

    public TourController(TourRepository tourRepository) {
        this.tourRepository = tourRepository;
    }

    // тепер буде відповідати GET /tours
    @GetMapping
    public String list(Model model) {
        var tours = tourRepository.findAll();
        System.out.println("DEBUG: tours count = " + tours.size());
        model.addAttribute("tours", tours);
        return "tours/list";
    }

    // деталі туру: GET /tours/{id}
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("tour", tourRepository.findById(id).orElse(null));
        return "tours/detail";
    }
}
