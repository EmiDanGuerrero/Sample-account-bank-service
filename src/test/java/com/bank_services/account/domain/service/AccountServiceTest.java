package com.bank_services.account.domain.service;

import com.bank_services.account.domain.exception.DuplicateResourceException;
import com.bank_services.account.domain.exception.ResourceNotFoundException;
import com.bank_services.account.domain.model.AccountStatus;
import com.bank_services.account.domain.model.BankAccount;
import com.bank_services.account.domain.model.Currency;
import com.bank_services.account.domain.port.AccountRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

	@Mock
	private AccountRepositoryPort repositoryPort;

	@InjectMocks
	private AccountService accountService;

	private BankAccount sample;

	@BeforeEach
	void setUp() {
		sample = BankAccount.builder().id(null).accountNumber("ACC-001").cbu("1230000100000000000011")
				.ownerName("Test User").ownerDocument("30123456").currency(Currency.ARS)
				.balance(new BigDecimal("1000.00")).status(AccountStatus.ACTIVE).branchCode("001").createdAt(null)
				.updatedAt(null).build();
	}

	@Test
	void create_shouldPersist_whenCbuDoesNotExist() {
		when(repositoryPort.existsByCbu(sample.getCbu())).thenReturn(false);
		when(repositoryPort.save(any(BankAccount.class))).thenAnswer(inv -> inv.getArgument(0));

		BankAccount created = accountService.create(sample);

		assertNotNull(created);
		assertThat(created.getStatus()).isEqualTo(AccountStatus.ACTIVE);
		assertNotNull(created.getCreatedAt());
		assertNotNull(created.getUpdatedAt());

		ArgumentCaptor<BankAccount> captor = ArgumentCaptor.forClass(BankAccount.class);
		verify(repositoryPort).save(captor.capture());
		assertThat(captor.getValue().getCbu()).isEqualTo(sample.getCbu());
	}

	@Test
	void create_shouldThrowDuplicateResourceException_whenCbuExists() {
		when(repositoryPort.existsByCbu(sample.getCbu())).thenReturn(true);

		assertThrows(DuplicateResourceException.class, () -> accountService.create(sample));
		verify(repositoryPort, never()).save(any());
	}

	@Test
	void getById_shouldReturnAccount_whenExists() {
		UUID id = UUID.randomUUID();
		sample.setId(id);
		when(repositoryPort.findById(id)).thenReturn(Optional.of(sample));

		BankAccount result = accountService.getById(id);

		assertNotNull(result);
		assertEquals(id, result.getId());
		assertEquals("Test User", result.getOwnerName());
	}

	@Test
	void getById_shouldThrowResourceNotFound_whenNotExists() {
		UUID id = UUID.randomUUID();
		when(repositoryPort.findById(id)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> accountService.getById(id));
	}

	@Test
	void getAll_shouldReturnListFromRepository() {
		when(repositoryPort.findAll()).thenReturn(List.of(sample));

		List<BankAccount> result = accountService.getAll();

		assertThat(result).hasSize(1).extracting(BankAccount::getAccountNumber).containsExactly("ACC-001");
	}

	@Test
	void update_shouldApplyChangesAndPersist() {
	    UUID id = UUID.randomUUID();
	    BankAccount existing = BankAccount.builder()
	            .id(id)
	            .accountNumber("ACC-OLD")
	            .cbu("1230000100000000000011")
	            .ownerName("Old Name")
	            .ownerDocument("30123456")
	            .currency(Currency.ARS)
	            .balance(new BigDecimal("500.00"))
	            .status(AccountStatus.ACTIVE)
	            .branchCode("001")
	            .createdAt(LocalDateTime.now().minusDays(1))
	            .updatedAt(LocalDateTime.now().minusDays(1))
	            .build();

	    when(repositoryPort.findById(id)).thenReturn(Optional.of(existing));
	    when(repositoryPort.save(any(BankAccount.class))).thenAnswer(inv -> inv.getArgument(0));

	    LocalDateTime oldUpdatedAt = existing.getUpdatedAt();
	    BankAccount updated = accountService.update(id, sample);

	    assertThat(updated.getOwnerName()).isEqualTo("Test User");
	    assertThat(updated.getAccountNumber()).isEqualTo("ACC-001");
	    assertThat(updated.getBalance()).isEqualTo(new BigDecimal("1000.00"));

	    assertThat(updated.getUpdatedAt()).isAfter(oldUpdatedAt);
	}

	@Test
	void update_shouldThrowNotFound_whenIdDoesNotExist() {
		UUID id = UUID.randomUUID();
		when(repositoryPort.findById(id)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> accountService.update(id, sample));
	}

	@Test
	void update_shouldThrowDuplicateResource_whenChangingToExistingCbu() {
		UUID id = UUID.randomUUID();
		BankAccount existing = BankAccount.builder().id(id).cbu("OLD-CBU").build();

		sample.setCbu("NEW-CBU");

		when(repositoryPort.findById(id)).thenReturn(Optional.of(existing));
		when(repositoryPort.existsByCbu("NEW-CBU")).thenReturn(true);

		assertThrows(DuplicateResourceException.class, () -> accountService.update(id, sample));
	}

	@Test
	void delete_shouldCloseAccount_whenExists() {
		UUID id = UUID.randomUUID();
		BankAccount existing = BankAccount.builder().id(id).status(AccountStatus.ACTIVE).build();

		when(repositoryPort.findById(id)).thenReturn(Optional.of(existing));
		when(repositoryPort.save(any(BankAccount.class))).thenAnswer(inv -> inv.getArgument(0));

		accountService.delete(id);

		assertThat(existing.getStatus()).isEqualTo(AccountStatus.CLOSED);
		verify(repositoryPort).save(existing);
	}

	@Test
	void delete_shouldThrowNotFound_whenAccountDoesNotExist() {
		UUID id = UUID.randomUUID();
		when(repositoryPort.findById(id)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> accountService.delete(id));
		verify(repositoryPort, never()).save(any());
	}
}
