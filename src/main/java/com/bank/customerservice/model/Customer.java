package com.bank.customerservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "customers",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_customers_email",
            columnNames = "email"
        ),
        @UniqueConstraint(
            name = "uk_customers_external_id",
            columnNames = "external_id"
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * Used by Hibernate for optimistic locking.
     * It is automatically incremented when the record is updated.
     */
    @Version
    @Column(name = "version", nullable = false)
    private Integer version;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "address", nullable = false)
    private String address;

    /*
     * External ID provided by an external system.
     * It must be unique.
     */
    @Column(name = "external_id", nullable = false, unique = true)
    private String externalId;

    /*
     * Store enum as String instead of ordinal number.
     *
     * Example:
     * PENDING
     * VERIFIED
     * REJECTED
     */
	@Enumerated(EnumType.STRING)
	@Column(name = "kyc_status", nullable = false)
	private KycStatus kycStatus;

    @Column(name = "active", nullable = false)
    private Boolean active;

    @Column(name = "request_fingerprint", nullable = false)
    private String requestFingerprint;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}