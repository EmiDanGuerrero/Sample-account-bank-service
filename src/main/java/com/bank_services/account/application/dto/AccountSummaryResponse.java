package com.bank_services.account.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

import com.bank_services.account.domain.model.AccountStatus;
import com.bank_services.account.domain.model.Currency;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AccountSummaryResponse {

	@NotBlank
	@Size(max = 50)
	private UUID id;

	@NotBlank
	@Size(max = 100)
	private String ownerName;

	@NotBlank
	@Size(max = 50)
	private String accountNumber;

	@NotBlank
	@Size(max = 20)
	private String branchCode;
	
	@NotBlank
	private Currency currency;
	
	private BigDecimal balance;
	
	@NotBlank
	private AccountStatus status;
	
	@NotBlank
	private boolean lowBalanceRisk;
}
