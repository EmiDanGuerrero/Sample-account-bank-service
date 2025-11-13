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
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "bank_accounts", uniqueConstraints = {
		@UniqueConstraint(name = "uk_bank_account_cbu", columnNames = "cbu") })
public class BankAccountEntity {

	@Id
	@GeneratedValue
	@Column(name = "id", columnDefinition = "BINARY(16)")
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

	public BankAccountEntity() {
	}

	// Getters y setters

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getCbu() {
		return cbu;
	}

	public void setCbu(String cbu) {
		this.cbu = cbu;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public String getOwnerDocument() {
		return ownerDocument;
	}

	public void setOwnerDocument(String ownerDocument) {
		this.ownerDocument = ownerDocument;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public AccountStatus getStatus() {
		return status;
	}

	public void setStatus(AccountStatus status) {
		this.status = status;
	}

	public String getBranchCode() {
		return branchCode;
	}

	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
}
