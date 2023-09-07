package com.example.backend.util.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RoutingDataSource extends AbstractRoutingDataSource {
	@Nullable
	@Override
	protected Object determineCurrentLookupKey() {
		String lookupKey = TransactionSynchronizationManager.isCurrentTransactionReadOnly() ? "replica" : "master";
		log.info("Current DataSource is {}", lookupKey);
		return lookupKey;
	}
}
