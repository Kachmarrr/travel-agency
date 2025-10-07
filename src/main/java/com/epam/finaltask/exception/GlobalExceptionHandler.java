package com.epam.finaltask.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    // Для 404 — показуємо сторінку помилки з кодом 404
    @ExceptionHandler(NotFoundException.class)
    public String handleNotFound(NotFoundException exception,
                                 HttpServletResponse response,
                                 org.springframework.ui.Model model) {
        response.setStatus(HttpStatus.NOT_FOUND.value());
        model.addAttribute("errorMessage", exception.getMessage());

        log.error("NotFoundException: {}", exception.getMessage(), exception);

        return "error/404";
    }

    @ExceptionHandler(BadRequestException.class)
    public String handleBadRequest(BadRequestException exception,
                                   RedirectAttributes redirectAttributes,
                                   HttpServletRequest request) {
        // Add flash-massage
        redirectAttributes.addFlashAttribute("error", exception.getMessage());

        log.warn("BadRequestException: {}", exception.getMessage(), exception);

        String referer = request.getHeader("Referer");
        if (referer != null && !referer.isBlank()) {
            return "redirect:" + referer;
        } else {
            return "redirect:/profile";
        }
    }
}
