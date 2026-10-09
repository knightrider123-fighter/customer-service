package com.bank.customerservice.services.impl;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bank.customerservice.dto.CustomerRegistrationRequest;
import com.bank.customerservice.dto.CustomerRegistrationResponse;
import com.bank.customerservice.exception.DuplicateResourseException;
import com.bank.customerservice.mapper.CustomerMapper;
import com.bank.customerservice.model.Customer;
import com.bank.customerservice.model.KycStatus;
import com.bank.customerservice.repository.CustomerRepository;
import com.bank.customerservice.services.CustomerService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

	private final CustomerRepository customerRepository;
	private final CustomerMapper customerMapper;

	@Override
	@Transactional
	public CustomerRegistrationResponse registerCustomer(CustomerRegistrationRequest request) {

		// 1. Check whether email is already registered
		if (Boolean.TRUE.equals(customerRepository.existsByEmail(request.getEmail()))) {
			throw new DuplicateResourseException("Customer already exists with this email");
		}

		// 2. Check whether external ID is already registered
		if (Boolean.TRUE.equals(customerRepository.existsByExternalId(request.getExternalId()))) {
			throw new DuplicateResourseException("Customer already exists with this external ID");
		}

		// 3. Convert request DTO into Customer entity
		Customer customer = customerMapper.toEntity(request);

		// 4. Set system-controlled fields
		customer.setKycStatus(KycStatus.PENDING);
		customer.setActive(true);

		// 5. Generate a request fingerprint
		customer.setRequestFingerprint(UUID.randomUUID().toString());

		// 6. Set audit timestamps
		LocalDateTime now = LocalDateTime.now();
		customer.setCreatedAt(now);
		customer.setUpdatedAt(now);

		// 7. Save customer in H2 database
		Customer savedCustomer = customerRepository.save(customer);

		// 8. Convert saved entity into response DTO
		return customerMapper.toRegistrationResponse(savedCustomer);
	}
}
