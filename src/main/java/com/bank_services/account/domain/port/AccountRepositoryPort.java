package com.bank_services.account.domain.port;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.bank_services.account.domain.model.BankAccount;

public interface AccountRepositoryPort {

	BankAccount save(BankAccount account);

	Optional<BankAccount> findById(UUID id);

	Optional<BankAccount> findByCbu(String cbu);

	List<BankAccount> findAll();

	void deleteById(UUID id);

	boolean existsById(UUID id);

	boolean existsByCbu(String cbu);
}
