package com.bank.customerservice.mapper;

import org.springframework.stereotype.Component;

import com.bank.customerservice.dto.CustomerRegistrationRequest;
import com.bank.customerservice.dto.CustomerRegistrationResponse;
import com.bank.customerservice.model.Customer;

@Component
public class CustomerMapper {

	// Convert registration request DTO to Customer entity
	public Customer toEntity(CustomerRegistrationRequest request) {

		Customer customer = new Customer();

		customer.setFirstName(request.getFirstName());
		customer.setLastName(request.getLastName());
		customer.setEmail(request.getEmail());
		customer.setPhone(request.getPhone());
		customer.setAddress(request.getAddress());
		customer.setExternalId(request.getExternalId());

		return customer;
	}

	// Convert Customer entity to registration response DTO
	public CustomerRegistrationResponse toRegistrationResponse(Customer customer) {

		return CustomerRegistrationResponse.builder().id(customer.getId()).externalId(customer.getExternalId())
				.message("Customer registered successfully").kycStatus(customer.getKycStatus().name()).build();
	}
}
