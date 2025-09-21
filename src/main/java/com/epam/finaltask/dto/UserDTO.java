package com.epam.finaltask.dto;

import java.util.List;

import com.epam.finaltask.model.Tour;

import com.epam.finaltask.model.enums.Role;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {

	private Long id;

	@NotBlank(message = "username must not be blank")
	private String username;

	@NotBlank()
	@Size(min = 6, message = "password must have five(5) chars")
	private String password;

	@NotBlank(message = "email is required")
	@Email(message = "Invalid email format")
	private String email;

	@NotBlank(message = "role is required")
	private Role role;

	@PositiveOrZero(message = "balance must be zero or positive")
	private Double balance;

	private List<TourDTO> tours;

//	@NotNull
//	private boolean active;
}
