package com.epam.finaltask.controller;

import com.epam.finaltask.DTO.TourDTO;
import com.epam.finaltask.DTO.UserDTO;
import com.epam.finaltask.model.enums.*;
import com.epam.finaltask.service.TourService;
import com.epam.finaltask.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import java.security.Principal;

@Controller
public class HomeController {

    private final TourService tourService;
    private final UserService userService;

    private static final String DEFAULT_SORT_FIELD = "price";
    private static final String DEFAULT_SORT_DIR = "asc";
    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 9;

    public HomeController(TourService tourService, UserService userService) {
        this.tourService = tourService;
        this.userService = userService;
    }

    @ModelAttribute
    public void addEnumsToModel(Model model) {
        model.addAttribute("tourTypes", TourType.values());
        model.addAttribute("hotelTypes", HotelType.values());
        model.addAttribute("transferTypes", TransferType.values());
    }

    // Один метод для "/" і "/dashboard" — вибір view за шляхом запиту
    @GetMapping({"/", "/dashboard"})
    public String listPages(
            @RequestParam(required = false) TourType tourType,
            @RequestParam(required = false) HotelType hotelType,
            @RequestParam(required = false) TransferType transferType,
            @RequestParam(defaultValue = DEFAULT_SORT_FIELD) String sortField,
            @RequestParam(defaultValue = DEFAULT_SORT_DIR) String sortDir,
            @RequestParam(defaultValue = "" + DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = "" + DEFAULT_SIZE) int size,
            Model model,
            Principal principal,
            HttpServletRequest request) {

        UserDTO user = resolveCurrentUser(principal);
        if (user != null) {
            model.addAttribute("user", user);
        }

        boolean isManager = user != null && user.getRole() == Role.MANAGER;

        Pageable pageable = buildPageable(page, size, sortField, sortDir);
        Page<TourDTO> toursPage = tourService.findAllFilteredAndSorted(tourType, hotelType, transferType, pageable, isManager);

        populateToursModel(model, toursPage, tourType, hotelType, transferType, sortField, sortDir);

        String path = request.getServletPath();
        return "/dashboard".equals(path) ? "dashboard" : "index";
    }

    @GetMapping("/profile")
    public String profile(Model model, Principal principal) {
        UserDTO user = resolveCurrentUser(principal);
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        model.addAttribute("userTours", tourService.findAllByUserId(user.getId()));
        return "user/profile";
    }



    private UserDTO resolveCurrentUser(Principal principal) {
        if (principal == null) return null;
        try {
            return userService.getUserByUsername(principal.getName());
        } catch (Exception e) {
            return null;
        }
    }

    private Pageable buildPageable(int page, int size, String sortField, String sortDir) {
        Sort sort = "asc".equalsIgnoreCase(sortDir) ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        return PageRequest.of(Math.max(0, page), Math.max(1, size), sort);
    }

    private void populateToursModel(Model model,
                                    Page<TourDTO> toursPage,
                                    TourType tourType,
                                    HotelType hotelType,
                                    TransferType transferType,
                                    String sortField,
                                    String sortDir) {

        model.addAttribute("tours", toursPage);
        model.addAttribute("tourType", tourType);
        model.addAttribute("hotelType", hotelType);
        model.addAttribute("transferType", transferType);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("currentPage", toursPage.getNumber());
        model.addAttribute("pageSize", toursPage.getSize());
        model.addAttribute("totalPages", toursPage.getTotalPages());
        model.addAttribute("totalItems", toursPage.getTotalElements());
    }
}
