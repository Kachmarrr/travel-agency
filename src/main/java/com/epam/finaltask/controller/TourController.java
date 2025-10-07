package com.epam.finaltask.controller;

import com.epam.finaltask.DTO.TourDTO;
import com.epam.finaltask.DTO.UserDTO;
import com.epam.finaltask.model.enums.Role;
import com.epam.finaltask.model.enums.TourStatus;
import com.epam.finaltask.service.TourService;
import com.epam.finaltask.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/tours")
public class TourController {

    private final TourService tourService;
    private final UserService userService;

    public TourController(TourService tourService, UserService userService) {
        this.tourService = tourService;
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        TourDTO tourDTO = tourService.findById(id);

        UserDTO userDTO = null;
        if (tourDTO.getUserId() != null) {
            userDTO = userService.getUserById(tourDTO.getUserId());
        }

        model.addAttribute("tour", tourDTO);
        model.addAttribute("user", userDTO);
        return "tours/detail";
    }

    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("tourDTO", new TourDTO());
        return "tours/create";
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/create")
    public String createTour(@Valid @ModelAttribute("tourDTO") TourDTO tourDTO,
                             BindingResult bindingResult) {

        if (bindingResult.hasErrors()) return "tours/create";

        tourService.create(tourDTO);
        return "redirect:/dashboard";
    }

    @PreAuthorize("hasAnyRole('MANAGER')")
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        TourDTO tour = tourService.findById(id);
        model.addAttribute("tour", tour);
        return "tours/edit";
    }

    @PreAuthorize("hasAnyRole('MANAGER')")
    @PostMapping("/update/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("tour") TourDTO tourDTO,
                         BindingResult bindingResult) {

        if (bindingResult.hasErrors()) return "tours/edit";

        tourDTO.setId(id);
        tourService.update(tourDTO);
        return "redirect:/dashboard";
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {

        tourService.delete(id);
        return "redirect:/dashboard";

    }
}
