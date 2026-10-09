package com.bank.customerservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bank.customerservice.model.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {

	Boolean existsByEmail(String email);

	Boolean existsByExternalId(String externalId);

	Optional<Customer> findByEmail(String email);

}
