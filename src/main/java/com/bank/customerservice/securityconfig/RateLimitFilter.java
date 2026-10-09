package com.bank.customerservice.securityconfig;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

	private static final long REQUEST_LIMIT = 10;

	private final ConcurrentMap<String, Bucket> buckets = new ConcurrentHashMap<>();

	private Bucket createBucket() {
		return Bucket.builder()
				.addLimit(limit -> limit.capacity(REQUEST_LIMIT).refillGreedy(REQUEST_LIMIT, Duration.ofMinutes(1)))
				.build();
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		// Apply rate limiting only to API endpoints
		if (!request.getRequestURI().startsWith("/api/")) {
			filterChain.doFilter(request, response);
			return;
		}

		// Identify the client by IP address
		String clientIp = request.getRemoteAddr();

		// Find or create the bucket for this IP
		Bucket bucket = buckets.computeIfAbsent(clientIp, key -> createBucket());

		// Consume one token and inspect the result
		ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

		long remainingTokens = probe.getRemainingTokens();

		// Print bucket details
		System.out.println("==================================");
		System.out.println("Client IP: " + clientIp);
		System.out.println("Bucket Capacity: " + REQUEST_LIMIT);
		System.out.println("Remaining Tokens: " + remainingTokens);
		System.out.println("Consumed Tokens: " + (REQUEST_LIMIT - remainingTokens));

		if (probe.isConsumed()) {

			System.out.println("Request Status: ALLOWED");

			// Continue to the controller
			filterChain.doFilter(request, response);

		} else {

			// Calculate time until a token becomes available
			long waitNanos = probe.getNanosToWaitForRefill();
			long waitMillis = (waitNanos + 999_999) / 1_000_000;
			long waitSeconds = (waitMillis + 999) / 1000;

			System.out.println("Request Status: REJECTED");
			System.out.println("Rate limit exceeded for IP: " + clientIp);
			System.out.println("Time until next token: " + waitMillis + " ms");
			System.out.println("Approximate wait: " + waitSeconds + " seconds");
			System.out.println("==================================");

			response.setStatus(429);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.setHeader("Retry-After", String.valueOf(waitSeconds));

			response.getWriter().write("{\"status\":429," + "\"error\":\"Too Many Requests\","
					+ "\"message\":\"Rate limit exceeded. Try again later.\"}");
		}
	}
}