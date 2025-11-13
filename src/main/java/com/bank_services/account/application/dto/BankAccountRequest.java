package com.bank_services.account.application.dto;

import java.math.BigDecimal;

import com.bank_services.account.domain.model.Currency;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class BankAccountRequest {

	@NotBlank
	@Size(max = 50)
	private String accountNumber;

	@NotBlank
	@Size(max = 50)
	private String cbu;

	@NotBlank
	@Size(max = 100)
	private String ownerName;

	@NotBlank
	@Size(max = 50)
	private String ownerDocument;

	@NotNull
	private Currency currency;

	// Permite que el cliente inicialice el saldo (opcional)
	private BigDecimal balance;

	@NotBlank
	@Size(max = 20)
	private String branchCode;

	public BankAccountRequest() {
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

	public String getBranchCode() {
		return branchCode;
	}

	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}
}
