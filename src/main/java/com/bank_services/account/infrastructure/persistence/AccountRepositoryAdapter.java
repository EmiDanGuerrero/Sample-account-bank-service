package com.bank_services.account.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.bank_services.account.domain.model.BankAccount;
import com.bank_services.account.domain.port.AccountRepositoryPort;

@Repository
public class AccountRepositoryAdapter implements AccountRepositoryPort {

	private final SpringDataAccountRepository repository;

	public AccountRepositoryAdapter(SpringDataAccountRepository repository) {
		this.repository = repository;
	}

	@Override
	public BankAccount save(BankAccount account) {
		BankAccountEntity entity = toEntity(account);
		BankAccountEntity saved = repository.save(entity);
		return toDomain(saved);
	}

	@Override
	public Optional<BankAccount> findById(UUID id) {
		return repository.findById(id).map(this::toDomain);
	}

	@Override
	public Optional<BankAccount> findByCbu(String cbu) {
		return repository.findByCbu(cbu).map(this::toDomain);
	}

	@Override
	public List<BankAccount> findAll() {
		return repository.findAll().stream().map(this::toDomain).collect(Collectors.toList());
	}

	@Override
	public void deleteById(UUID id) {
		repository.deleteById(id);
	}

	@Override
	public boolean existsById(UUID id) {
		return repository.existsById(id);
	}

	@Override
	public boolean existsByCbu(String cbu) {
		return repository.existsByCbu(cbu);
	}


	private BankAccount toDomain(BankAccountEntity entity) {
		if (entity == null) {
			return null;
		}

		BankAccount account = new BankAccount();
		account.setId(entity.getId());
		account.setAccountNumber(entity.getAccountNumber());
		account.setCbu(entity.getCbu());
		account.setOwnerName(entity.getOwnerName());
		account.setOwnerDocument(entity.getOwnerDocument());
		account.setCurrency(entity.getCurrency());
		account.setBalance(entity.getBalance());
		account.setStatus(entity.getStatus());
		account.setBranchCode(entity.getBranchCode());
		account.setCreatedAt(entity.getCreatedAt());
		account.setUpdatedAt(entity.getUpdatedAt());
		return account;
	}

	private BankAccountEntity toEntity(BankAccount account) {
		if (account == null) {
			return null;
		}

		BankAccountEntity entity = new BankAccountEntity();
		entity.setId(account.getId());
		entity.setAccountNumber(account.getAccountNumber());
		entity.setCbu(account.getCbu());
		entity.setOwnerName(account.getOwnerName());
		entity.setOwnerDocument(account.getOwnerDocument());
		entity.setCurrency(account.getCurrency());
		entity.setBalance(account.getBalance());
		entity.setStatus(account.getStatus());
		entity.setBranchCode(account.getBranchCode());
		entity.setCreatedAt(account.getCreatedAt());
		entity.setUpdatedAt(account.getUpdatedAt());
		return entity;
	}
}
