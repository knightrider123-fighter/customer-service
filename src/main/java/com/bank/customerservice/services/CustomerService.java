package com.bank.customerservice.services;

import com.bank.customerservice.dto.CustomerRegistrationRequest;
import com.bank.customerservice.dto.CustomerRegistrationResponse;

public interface CustomerService {

	CustomerRegistrationResponse registerCustomer(CustomerRegistrationRequest request);
}
