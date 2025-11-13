package com.bank_services.account.infrastructure.web;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.bank_services.account.application.dto.AccountSummaryResponse;
import com.bank_services.account.application.dto.BankAccountResponse;

@Component
public class SelfAccountClient {

	private final RestClient restClient;

	public SelfAccountClient() {
		this.restClient = RestClient.builder().baseUrl("http://localhost:8080").build();
	}

	public AccountSummaryResponse getAccountSummary(UUID id) {
		try {
			BankAccountResponse account = restClient.get().uri("/api/v1/accounts/{id}", id).retrieve()
					.body(BankAccountResponse.class);

			if (account == null) {
				throw new IllegalStateException("Empty response when calling self endpoint");
			}

			return mapToSummary(account);
		} catch (HttpStatusCodeException e) {
			if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
				throw e; // más adelante lo maneja el handler global
			}
			throw e;
		} catch (RestClientException e) {
			throw e;
		}
	}

	private AccountSummaryResponse mapToSummary(BankAccountResponse account) {
		AccountSummaryResponse summary = new AccountSummaryResponse();
		summary.setId(account.getId());
		summary.setOwnerName(account.getOwnerName());
		summary.setAccountNumber(account.getAccountNumber());
		summary.setBranchCode(account.getBranchCode());
		summary.setCurrency(account.getCurrency());
		summary.setBalance(account.getBalance());
		summary.setStatus(account.getStatus());

		BigDecimal balance = account.getBalance() != null ? account.getBalance() : BigDecimal.ZERO;
		summary.setLowBalanceRisk(balance.compareTo(new BigDecimal("1000")) < 0);

		return summary;
	}
}
