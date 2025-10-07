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

    @NotNull(message = "Price is required")
    @PositiveOrZero(message = "Price must be zero or positive")
    private Double price;

    @NotNull(message = "TourType is required")
    private TourType tourType;

    @NotNull(message = "TransferType is required")
    private TransferType transferType;

    @NotNull(message = "HotelType is required")
    private HotelType hotelType;

    @NotNull(message = "TourStatus is required")
    private TourStatus status;

    @NotNull(message = "Arrival date is required")
    private LocalDate arrivalDate;

    @NotNull(message = "Eviction date is required")
    private LocalDate evictionDate;

    private Long userId;

	@JsonProperty("isHot")
    private Boolean isHot;

}
