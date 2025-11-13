package com.bank_services.account.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class BankAccount {

	private UUID id;
	private String accountNumber;
	private String cbu;
	private String ownerName;
	private String ownerDocument;
	private Currency currency;
	private BigDecimal balance;
	private AccountStatus status;
	private String branchCode;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public BankAccount() {
	}

	public BankAccount(UUID id, String accountNumber, String cbu, String ownerName, String ownerDocument,
			Currency currency, BigDecimal balance, AccountStatus status, String branchCode, LocalDateTime createdAt,
			LocalDateTime updatedAt) {
		this.id = id;
		this.accountNumber = accountNumber;
		this.cbu = cbu;
		this.ownerName = ownerName;
		this.ownerDocument = ownerDocument;
		this.currency = currency;
		this.balance = balance;
		this.status = status;
		this.branchCode = branchCode;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	/**
	 * Fábrica estática para crear una nueva cuenta con valores por defecto (balance
	 * = 0, estado = ACTIVE, fechas = ahora).
	 */
	public static BankAccount createNew(String accountNumber, String cbu, String ownerName, String ownerDocument,
			Currency currency, String branchCode) {
		LocalDateTime now = LocalDateTime.now();
		return new BankAccount(UUID.randomUUID(), accountNumber, cbu, ownerName, ownerDocument, currency,
				BigDecimal.ZERO, AccountStatus.ACTIVE, branchCode, now, now);
	}

	/**
	 * Actualiza los campos mutables desde otra instancia. No modifica id ni
	 * createdAt.
	 */
	public void updateFrom(BankAccount source) {
		if (source == null) {
			return;
		}
		this.ownerName = source.getOwnerName();
		this.ownerDocument = source.getOwnerDocument();
		this.accountNumber = source.getAccountNumber();
		this.branchCode = source.getBranchCode();
		this.currency = source.getCurrency();
		this.balance = source.getBalance();
		// IMPORTANTE: no tocamos el status si viene null
		if (source.getStatus() != null) {
			this.status = source.getStatus();
		}
		this.updatedAt = LocalDateTime.now();
	}

	/**
	 * Marca la cuenta como cerrada.
	 */
	public void close() {
		this.status = AccountStatus.CLOSED;
		this.updatedAt = LocalDateTime.now();
	}

	// ====== Getters y Setters ======

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
