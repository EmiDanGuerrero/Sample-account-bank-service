package com.bank_services.account.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

import com.bank_services.account.domain.model.AccountStatus;
import com.bank_services.account.domain.model.Currency;

public class AccountSummaryResponse {

	private UUID id;
	private String ownerName;
	private String accountNumber;
	private String branchCode;
	private Currency currency;
	private BigDecimal balance;
	private AccountStatus status;
	private boolean lowBalanceRisk;

	public AccountSummaryResponse() {
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getBranchCode() {
		return branchCode;
	}

	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
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

	public boolean isLowBalanceRisk() {
		return lowBalanceRisk;
	}

	public void setLowBalanceRisk(boolean lowBalanceRisk) {
		this.lowBalanceRisk = lowBalanceRisk;
	}
}
