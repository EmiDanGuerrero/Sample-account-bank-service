package com.bank_services.account.infrastructure.web;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.bank_services.account.application.dto.AccountSummaryResponse;
import com.bank_services.account.application.dto.BankAccountRequest;
import com.bank_services.account.application.dto.BankAccountResponse;
import com.bank_services.account.domain.model.AccountStatus;
import com.bank_services.account.domain.model.BankAccount;
import com.bank_services.account.domain.service.AccountService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

	private final AccountService accountService;
	private final SelfAccountClient selfAccountClient;

	public AccountController(AccountService accountService, SelfAccountClient selfAccountClient) {
		this.accountService = accountService;
		this.selfAccountClient = selfAccountClient;
	}

	@PostMapping
	public ResponseEntity<BankAccountResponse> createAccount(@Valid @RequestBody BankAccountRequest request) {
		BankAccount toCreate = mapToDomainForCreate(request);
		BankAccount created = accountService.create(toCreate);
		BankAccountResponse response = mapToResponse(created);

		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(created.getId())
				.toUri();

		return ResponseEntity.created(location).body(response);
	}

	@GetMapping("/{id}")
	public ResponseEntity<BankAccountResponse> getAccountById(@PathVariable("id") UUID id) {
		BankAccount account = accountService.getById(id);
		return ResponseEntity.ok(mapToResponse(account));
	}

	@GetMapping
	public ResponseEntity<List<BankAccountResponse>> getAllAccounts() {
		List<BankAccount> accounts = accountService.getAll();
		List<BankAccountResponse> responses = accounts.stream().map(this::mapToResponse).collect(Collectors.toList());
		return ResponseEntity.ok(responses);
	}

	@GetMapping("/{id}/summary")
	public ResponseEntity<AccountSummaryResponse> getAccountSummary(@PathVariable("id") UUID id) {

		AccountSummaryResponse summary = selfAccountClient.getAccountSummary(id);

		return ResponseEntity.ok(summary);
	}

	@PutMapping("/{id}")
	public ResponseEntity<BankAccountResponse> updateAccount(@PathVariable("id") UUID id,
			@Valid @RequestBody BankAccountRequest request) {
		BankAccount updatedDomain = mapToDomainForUpdate(request);
		BankAccount updated = accountService.update(id, updatedDomain);
		return ResponseEntity.ok(mapToResponse(updated));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteAccount(@PathVariable("id") UUID id) {
		accountService.delete(id);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	private BankAccount mapToDomainForCreate(BankAccountRequest request) {
		BankAccount account = new BankAccount();
		account.setAccountNumber(request.getAccountNumber());
		account.setCbu(request.getCbu());
		account.setOwnerName(request.getOwnerName());
		account.setOwnerDocument(request.getOwnerDocument());
		account.setCurrency(request.getCurrency());
		account.setBranchCode(request.getBranchCode());

		BigDecimal balance = request.getBalance();
		if (balance == null) {
			balance = BigDecimal.ZERO;
		}
		account.setBalance(balance);
		account.setStatus(AccountStatus.ACTIVE);
		return account;
	}

	private BankAccount mapToDomainForUpdate(BankAccountRequest request) {
		BankAccount account = new BankAccount();
		account.setAccountNumber(request.getAccountNumber());
		account.setCbu(request.getCbu());
		account.setOwnerName(request.getOwnerName());
		account.setOwnerDocument(request.getOwnerDocument());
		account.setCurrency(request.getCurrency());
		account.setBranchCode(request.getBranchCode());

		BigDecimal balance = request.getBalance();
		if (balance == null) {
			balance = BigDecimal.ZERO;
		}
		account.setBalance(balance);
		return account;
	}

	private BankAccountResponse mapToResponse(BankAccount account) {
		BankAccountResponse response = new BankAccountResponse();
		response.setId(account.getId());
		response.setAccountNumber(account.getAccountNumber());
		response.setCbu(account.getCbu());
		response.setOwnerName(account.getOwnerName());
		response.setOwnerDocument(account.getOwnerDocument());
		response.setCurrency(account.getCurrency());
		response.setBalance(account.getBalance());
		response.setStatus(account.getStatus());
		response.setBranchCode(account.getBranchCode());
		response.setCreatedAt(account.getCreatedAt());
		response.setUpdatedAt(account.getUpdatedAt());
		return response;
	}
}
