package com.bank_services.account.domain.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.bank_services.account.domain.exception.DuplicateResourceException;
import com.bank_services.account.domain.exception.ResourceNotFoundException;
import com.bank_services.account.domain.model.AccountStatus;
import com.bank_services.account.domain.model.BankAccount;
import com.bank_services.account.domain.port.AccountRepositoryPort;

@Service
public class AccountService {

	private final AccountRepositoryPort repositoryPort;

	public AccountService(AccountRepositoryPort repositoryPort) {
		this.repositoryPort = repositoryPort;
	}

	public BankAccount create(BankAccount bankAccount) {
		if (repositoryPort.existsByCbu(bankAccount.getCbu())) {
			throw new DuplicateResourceException(
					"BankAccount with CBU %s already exists".formatted(bankAccount.getCbu()));
		}

		LocalDateTime now = LocalDateTime.now();
		if (bankAccount.getCreatedAt() == null) {
			bankAccount.setCreatedAt(now);
		}
		bankAccount.setUpdatedAt(now);

		if (bankAccount.getStatus() == null) {
			bankAccount.setStatus(AccountStatus.ACTIVE);
		}

		return repositoryPort.save(bankAccount);
	}

	public BankAccount getById(UUID id) {
		return repositoryPort.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("BankAccount with id %s not found".formatted(id)));
	}

	public List<BankAccount> getAll() {
		return repositoryPort.findAll();
	}

	public BankAccount update(UUID id, BankAccount updatedAccount) {
		BankAccount existing = getById(id); // lanza ResourceNotFoundException si no existe

		// Validar cambio de CBU
		if (updatedAccount.getCbu() != null && !updatedAccount.getCbu().equals(existing.getCbu())
				&& repositoryPort.existsByCbu(updatedAccount.getCbu())) {
			throw new DuplicateResourceException(
					"Another BankAccount with CBU %s already exists".formatted(updatedAccount.getCbu()));
		}
		
		existing.updateFrom(updatedAccount);
		existing.setUpdatedAt(LocalDateTime.now());
		return repositoryPort.save(existing);
	}

	/**
	 * "Eliminar" cuenta: cierre lógico (status = CLOSED).
	 */
	public void delete(UUID id) {
		BankAccount existing = getById(id);
		existing.close();
		repositoryPort.save(existing);
	}
}
