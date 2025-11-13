package com.bank_services.account.domain.exception;

public class DuplicateResourceException extends RuntimeException {

	public DuplicateResourceException(String message) {
		super(message);
	}
}
