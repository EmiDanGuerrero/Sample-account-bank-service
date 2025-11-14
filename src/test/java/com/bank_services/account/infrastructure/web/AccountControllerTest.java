package com.bank_services.account.infrastructure.web;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.bank_services.account.application.dto.AccountSummaryResponse;
import com.bank_services.account.application.dto.BankAccountRequest;
import com.bank_services.account.domain.exception.DuplicateResourceException;
import com.bank_services.account.domain.exception.ResourceNotFoundException;
import com.bank_services.account.domain.model.AccountStatus;
import com.bank_services.account.domain.model.BankAccount;
import com.bank_services.account.domain.model.Currency;
import com.bank_services.account.domain.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private AccountService accountService;

	@MockitoBean
	private SelfAccountClient selfAccountClient;

	@Test
	void create_shouldReturn201AndBody_whenOk() throws Exception {
		BankAccountRequest request = new BankAccountRequest();
		request.setAccountNumber("ACC-001");
		request.setCbu("1230000100000000000011");
		request.setOwnerName("Juan Perez");
		request.setOwnerDocument("30123456");
		request.setCurrency(Currency.ARS);
		request.setBalance(new BigDecimal("500.00"));
		request.setBranchCode("001");

		BankAccount domainAccount = BankAccount.builder().id(UUID.randomUUID()).accountNumber("ACC-001")
				.cbu("1230000100000000000011").ownerName("Juan Perez").ownerDocument("30123456").currency(Currency.ARS)
				.balance(new BigDecimal("500.00")).status(AccountStatus.ACTIVE).branchCode("001").build();

		Mockito.when(accountService.create(any(BankAccount.class))).thenReturn(domainAccount);

		mockMvc.perform(post("/api/v1/accounts").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))).andExpect(status().isCreated())
				.andExpect(jsonPath("$.accountNumber").value("ACC-001"))
				.andExpect(jsonPath("$.ownerName").value("Juan Perez")).andExpect(jsonPath("$.currency").value("ARS"));
	}

	@Test
	void create_shouldReturn409_whenDuplicateCbu() throws Exception {
		BankAccountRequest request = new BankAccountRequest();
		request.setAccountNumber("ACC-001");
		request.setCbu("1230000100000000000011");
		request.setOwnerName("Juan Perez");
		request.setOwnerDocument("30123456");
		request.setCurrency(Currency.ARS);
		request.setBalance(new BigDecimal("500.00"));
		request.setBranchCode("001");

		Mockito.when(accountService.create(any(BankAccount.class)))
				.thenThrow(new DuplicateResourceException("CBU already exists"));

		mockMvc.perform(post("/api/v1/accounts").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))).andExpect(status().isConflict())
				.andExpect(jsonPath("$.status").value(409)).andExpect(jsonPath("$.message", containsString("CBU")));
	}

	@Test
	void getById_shouldReturn200_whenFound() throws Exception {
		UUID id = UUID.randomUUID();

		BankAccount domainAccount = BankAccount.builder().id(id).accountNumber("ACC-001").cbu("1230000100000000000011")
				.ownerName("Juan Perez").ownerDocument("30123456").currency(Currency.ARS)
				.balance(new BigDecimal("500.00")).status(AccountStatus.ACTIVE).branchCode("001").build();

		Mockito.when(accountService.getById(id)).thenReturn(domainAccount);

		mockMvc.perform(get("/api/v1/accounts/{id}", id)).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(id.toString()))
				.andExpect(jsonPath("$.accountNumber").value("ACC-001"))
				.andExpect(jsonPath("$.ownerName").value("Juan Perez"));
	}

	@Test
	void getById_shouldReturn404_whenNotFound() throws Exception {
		UUID id = UUID.randomUUID();
		Mockito.when(accountService.getById(id)).thenThrow(new ResourceNotFoundException("not found"));

		mockMvc.perform(get("/api/v1/accounts/{id}", id)).andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value(404));
	}

	@Test
	void getAll_shouldReturnList() throws Exception {
		UUID id = UUID.randomUUID();

		BankAccount account = BankAccount.builder().id(id).accountNumber("ACC-001").cbu("1230000100000000000011")
				.ownerName("Juan Perez").ownerDocument("30123456").currency(Currency.ARS)
				.balance(new BigDecimal("500.00")).status(AccountStatus.ACTIVE).branchCode("001").build();

		Mockito.when(accountService.getAll()).thenReturn(Collections.singletonList(account));

		mockMvc.perform(get("/api/v1/accounts")).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(id.toString()))
				.andExpect(jsonPath("$[0].accountNumber").value("ACC-001"));
	}

	@Test
	void delete_shouldReturn204_whenOk() throws Exception {
		UUID id = UUID.randomUUID();

		mockMvc.perform(delete("/api/v1/accounts/{id}", id)).andExpect(status().isNoContent());
	}

	@Test
	void summary_shouldReturn200_whenOk() throws Exception {
		UUID id = UUID.randomUUID();

		AccountSummaryResponse summary = new AccountSummaryResponse();
		summary.setId(id);
		summary.setOwnerName("Juan Perez");
		summary.setAccountNumber("ACC-001");
		summary.setBranchCode("001");
		summary.setCurrency(Currency.ARS);
		summary.setBalance(new BigDecimal("500.00"));
		summary.setStatus(AccountStatus.ACTIVE);
		summary.setLowBalanceRisk(false);

		Mockito.when(selfAccountClient.getAccountSummary(eq(id))).thenReturn(summary);

		mockMvc.perform(get("/api/v1/accounts/{id}/summary", id)).andExpect(status().isOk())
				.andExpect(jsonPath("$.ownerName").value("Juan Perez"))
				.andExpect(jsonPath("$.lowBalanceRisk").value(false));
	}
}
