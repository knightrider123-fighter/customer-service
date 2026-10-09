package com.bank.customerservice.contoller;

import com.bank.customerservice.dto.CustomerRegistrationRequest;
import com.bank.customerservice.dto.CustomerRegistrationResponse;
import com.bank.customerservice.services.CustomerService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

	private final CustomerService customerService;

	@PostMapping("/register")
	public ResponseEntity<CustomerRegistrationResponse> registerCustomer(
			@Valid @RequestBody CustomerRegistrationRequest request) {

		CustomerRegistrationResponse response = customerService.registerCustomer(request);

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
}
