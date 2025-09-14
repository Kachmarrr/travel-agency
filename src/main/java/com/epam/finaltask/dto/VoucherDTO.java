package com.epam.finaltask.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VoucherDTO {

    private String id;

	@NotBlank(message = "title must be blank")
    private String title;

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

    private UUID userId;

	@JsonProperty("isHot")
    private Boolean isHot;

}
