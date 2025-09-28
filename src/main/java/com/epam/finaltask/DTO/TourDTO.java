package com.epam.finaltask.DTO;

import com.epam.finaltask.model.enums.HotelType;
import com.epam.finaltask.model.enums.TourStatus;
import com.epam.finaltask.model.enums.TourType;
import com.epam.finaltask.model.enums.TransferType;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

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

    private TourType tourType;

    private TransferType transferType;

    private HotelType hotelType;

    private TourStatus status;

    private LocalDate arrivalDate;

    private LocalDate evictionDate;

    private Long userId;

	@JsonProperty("isHot")
    private Boolean isHot;

}
