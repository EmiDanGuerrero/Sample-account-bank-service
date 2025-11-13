package com.bank_services.account.infrastructure.persistence;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.bank_services.account.domain.model.AccountStatus;
import com.bank_services.account.domain.model.Currency;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "bank_accounts", uniqueConstraints = {
		@UniqueConstraint(name = "uk_bank_account_cbu", columnNames = "cbu") })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankAccountEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id", updatable = false, nullable = false)
	private UUID id;

	@Column(name = "account_number", nullable = false, length = 50)
	private String accountNumber;

	@Column(name = "cbu", nullable = false, length = 50)
	private String cbu;

	@Column(name = "owner_name", nullable = false, length = 100)
	private String ownerName;

	@Column(name = "owner_document", nullable = false, length = 50)
	private String ownerDocument;

	@Enumerated(EnumType.STRING)
	@Column(name = "currency", nullable = false, length = 10)
	private Currency currency;

	@Column(name = "balance", nullable = false, precision = 19, scale = 2)
	private BigDecimal balance;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 20)
	private AccountStatus status;

	@Column(name = "branch_code", nullable = false, length = 20)
	private String branchCode;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;
}
