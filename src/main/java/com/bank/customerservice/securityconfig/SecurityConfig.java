package com.bank.customerservice.securityconfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.Header;

@Configuration
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http
				// Disable CSRF for development/testing
				.csrf(csrf -> csrf.disable())

				// Allow all requests without authentication
				.authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
				.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))

				// Disable default login page
				.formLogin(form -> form.disable())

				// Disable HTTP Basic authentication
				.httpBasic(basic -> basic.disable());

		return http.build();
	}
}