package com.bank_services.account.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.logbook.Logbook;

@Configuration
public class LogbookConfig {

	@Bean
	public Logbook logbook() {
		return Logbook.builder().build();
	}
}
