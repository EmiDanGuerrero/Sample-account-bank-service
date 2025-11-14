package com.bank_services.account.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class BankAccountTest {

	@Test
	void close_shouldSetStatusClosedAndUpdateTimestamp() {
		BankAccount account = BankAccount.builder().status(AccountStatus.ACTIVE)
				.updatedAt(LocalDateTime.now().minusHours(1)).build();

		LocalDateTime before = account.getUpdatedAt();

		account.close();

		assertThat(account.getStatus()).isEqualTo(AccountStatus.CLOSED);
		assertThat(account.getUpdatedAt()).isAfter(before);
	}

	@Test
	void updateFrom_shouldCopyFieldsFromSource() {
		BankAccount target = BankAccount.builder().ownerName("Old Name").accountNumber("ACC-OLD").branchCode("001")
				.currency(Currency.ARS).balance(new BigDecimal("100")).status(AccountStatus.ACTIVE).build();

		BankAccount source = BankAccount.builder().ownerName("New Name").accountNumber("ACC-NEW").branchCode("002")
				.currency(Currency.USD).balance(new BigDecimal("999.99")).status(AccountStatus.CLOSED).build();

		target.updateFrom(source);

		assertThat(target.getOwnerName()).isEqualTo("New Name");
		assertThat(target.getAccountNumber()).isEqualTo("ACC-NEW");
		assertThat(target.getBranchCode()).isEqualTo("002");
		assertThat(target.getCurrency()).isEqualTo(Currency.USD);
		assertThat(target.getBalance()).isEqualTo(new BigDecimal("999.99"));
	}
}
