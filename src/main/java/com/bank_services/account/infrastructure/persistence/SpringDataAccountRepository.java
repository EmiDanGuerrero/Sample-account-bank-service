package com.bank_services.account.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataAccountRepository extends JpaRepository<BankAccountEntity, UUID> {

	Optional<BankAccountEntity> findByCbu(String cbu);

	boolean existsByCbu(String cbu);
}
