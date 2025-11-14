package com.bank_services.account.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

	public static BankAccount createNew(String accountNumber, String cbu, String ownerName, String ownerDocument,
			Currency currency, String branchCode) {
		LocalDateTime now = LocalDateTime.now();
		return BankAccount.builder().id(null).accountNumber(accountNumber).cbu(cbu).ownerName(ownerName)
				.ownerDocument(ownerDocument).currency(currency).balance(BigDecimal.ZERO).status(AccountStatus.ACTIVE)
				.branchCode(branchCode).createdAt(now).updatedAt(now).build();
	}

	public void updateFrom(BankAccount source) {
		if (source == null)
			return;
		this.ownerName = source.ownerName;
		this.ownerDocument = source.ownerDocument;
		this.accountNumber = source.accountNumber;
		this.branchCode = source.branchCode;
		this.currency = source.currency;
		this.balance = source.balance;
		if (source.status != null) {
			this.status = source.status;
		}
		this.updatedAt = LocalDateTime.now();
	}

	public void close() {
		this.status = AccountStatus.CLOSED;
		this.updatedAt = LocalDateTime.now();
	}
}
