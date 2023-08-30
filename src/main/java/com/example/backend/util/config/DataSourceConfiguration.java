package com.example.backend.util.config;

import java.util.HashMap;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class DataSourceConfiguration {

	private static final String MASTER_SERVER = "MASTER";
	private static final String REPLICA_SERVER = "REPLICA";

	@Bean
	@Qualifier(MASTER_SERVER)
	@ConfigurationProperties(prefix = "spring.datasource.master")
	public DataSource masterDataSource() {
		return DataSourceBuilder.create()
			.build();
	}

	@Bean
	@Qualifier(REPLICA_SERVER)
	@ConfigurationProperties(prefix = "spring.datasource.replica")
	public DataSource replicaDataSource() {
		return DataSourceBuilder.create()
			.build();
	}

	@Bean
	public DataSource routingDataSource(
		@Qualifier(MASTER_SERVER) DataSource masterDataSource, // (1)
		@Qualifier(REPLICA_SERVER) DataSource replicaDataSource
	) {
		RoutingDataSource routingDataSource = new RoutingDataSource(); // (2)

		HashMap<Object, Object> dataSourceMap = new HashMap<>(); // (3)
		dataSourceMap.put("source", masterDataSource);
		dataSourceMap.put("replica", replicaDataSource);

		routingDataSource.setTargetDataSources(dataSourceMap); // (4)
		routingDataSource.setDefaultTargetDataSource(masterDataSource); // (5)

		return routingDataSource;
	}

	@Bean
	@Primary
	public DataSource dataSource() {
		DataSource determinedDataSource = routingDataSource(masterDataSource(), replicaDataSource());
		return new LazyConnectionDataSourceProxy(determinedDataSource);
	}

}