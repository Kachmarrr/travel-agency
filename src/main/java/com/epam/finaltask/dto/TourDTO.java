package com.epam.finaltask.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TourDTO {

    private Long id;

	@NotBlank(message = "title must be blank")
    private String title;

    @NotBlank(message = "description is must be blank")
    private String description;

	@NotNull(message = "price must be required")
	@PositiveOrZero(message = "price must be zero or positive")
    private Double price;

    private String tourType;

    private String transferType;

    private String hotelType;

    private String status;

    private LocalDate arrivalDate;

    private LocalDate evictionDate;

    private Long userId;

	@JsonProperty("isHot")
    private Boolean isHot;

}
