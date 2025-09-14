package com.epam.finaltask.dto;

import java.util.List;

import com.epam.finaltask.model.Voucher;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {

	private String id;

	@NotBlank(message = "username must not be blank")
	private String username;

	private String password;

	@NotBlank(message = "role is required")
	private String role;

	private List<Voucher> vouchers;

	private String phoneNumber;

	@PositiveOrZero(message = "balance must be zero or positive")
	private Double balance;

	@NotNull
	private boolean active;
}
