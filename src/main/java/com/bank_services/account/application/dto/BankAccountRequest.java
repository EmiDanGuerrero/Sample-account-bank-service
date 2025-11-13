package com.bank_services.account.application.dto;

import java.math.BigDecimal;

import com.bank_services.account.domain.model.Currency;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
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

	private BigDecimal balance;

	@NotBlank
	@Size(max = 20)
	private String branchCode;
}
