package com.bank.customerservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRegistrationRequest {

	@NotBlank(message = "First name is required")
	@Size(max = 80, message = "First name cannot exceed 80 characters")
	private String firstName;

	@NotBlank(message = "Last name is required")
	@Size(max = 80, message = "Last name cannot exceed 80 characters")
	private String lastName;

	@NotBlank(message = "Email is required")
	@Email(message = "Please provide a valid email")
	@Size(max = 254)
	private String email;

	@NotBlank(message = "Phone number is required")
	@Pattern(regexp = "^[0-9]{10}$", message = "Phone number must contain exactly 10 digits")
	private String phone;

	@NotBlank(message = "Address is required")
	@Size(max = 512)
	private String address;

	@NotBlank(message = "External ID is required")
	@Size(max = 64)
	private String externalId;
}
