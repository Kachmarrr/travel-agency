package com.epam.finaltask.controller;

import com.epam.finaltask.DTO.UserDTO;
import com.epam.finaltask.repository.UserRepository;
import com.epam.finaltask.service.BookingService;
import com.epam.finaltask.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/booking")
public class BookingController {

    private final BookingService bookingService;
    private final UserService userService; // або UserService, щоб знайти id по username

    public BookingController(BookingService bookingService, UserService userService) {
        this.bookingService = bookingService;
        this.userService = userService;
    }

    @PostMapping("/create/{tourId}")
    public String createBooking(@PathVariable("tourId") Long tourId,
                                Principal principal,
                                RedirectAttributes redirectAttributes) {

        String username = principal.getName();

        // знайти userId по username
        UserDTO userDTO = userService.getUserByUsername(username);

        bookingService.book(tourId, userDTO.getId());
        redirectAttributes.addFlashAttribute("success", "Тур успішно заброньовано!");

        return "redirect:/tours/" + tourId;
    }

    @PostMapping("/cancel/{tourId}")
    public String cancelBooking(@PathVariable("tourId") Long tourId, RedirectAttributes redirectAttributes) {

        bookingService.cancelBooking(tourId);

        redirectAttributes.addFlashAttribute("success", "Бронювання скасовано!");

        return "redirect:/profile";
    }
}
