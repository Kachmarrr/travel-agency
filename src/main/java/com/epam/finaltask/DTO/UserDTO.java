package com.epam.finaltask.DTO;

import java.util.ArrayList;
import java.util.List;

import com.epam.finaltask.model.enums.Role;
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

	@NotBlank
	@Size(min = 6, message = "Password must be at least 6 characters")
	private String password;

	@NotBlank(message = "email is required")
	@Email(message = "Invalid email format")
	private String email;

	private Role role;

	@Builder.Default
	private Double balance = 0.0;

	// initialization empty list, to avoid NPE when .stream()
	@Builder.Default
	private List<TourDTO> tours = new ArrayList<>();
}
