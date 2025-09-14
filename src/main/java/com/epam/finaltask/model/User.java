package com.epam.finaltask.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "users")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "id", updatable = false, nullable = false, columnDefinition = "uuid")
    private UUID id;

	@Column(nullable = false)
    private String username;

	@Column(nullable = false)
    private String password;

	@Enumerated(EnumType.STRING)
	@Column(length = 50)
    private Role role;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Voucher> vouchers;

	@Column(name = "phone_number")
    private String phoneNumber;

	@Column(precision = 12, scale = 2)
    private BigDecimal balance;

	@Column(name = "active", nullable = false)
    private boolean active;
}