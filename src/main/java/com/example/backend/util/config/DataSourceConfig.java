package com.example.backend.util.config;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.example.backend.util.RoutingDataSource;

@Configuration
public class DataSourceConfig {

	@Value("${spring.datasource.master.url}")
	private String masterDbUrl;

	@Value("${spring.datasource.master.username}")
	private String masterUsername;

	@Value("${spring.datasource.master.password}")
	private String masterPassword;

	@Value("${spring.datasource.replica.url}")
	private String replicaDbUrl;

	@Value("${spring.datasource.replica.username}")
	private String replicaUsername;

	@Value("${spring.datasource.replica.password}")
	private String replicaPassword;

	@Bean
	public DataSource dataSource() {
		RoutingDataSource routingDataSource = new RoutingDataSource();

		// Master DataSource 설정
		DriverManagerDataSource masterDataSource = new DriverManagerDataSource();
		masterDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
		masterDataSource.setUrl(masterDbUrl);
		masterDataSource.setUsername(masterUsername);
		masterDataSource.setPassword(masterPassword);

		// Replica DataSource 설정
		DriverManagerDataSource replicaDataSource = new DriverManagerDataSource();
		replicaDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
		replicaDataSource.setUrl(replicaDbUrl);
		replicaDataSource.setUsername(replicaUsername);
		replicaDataSource.setPassword(replicaPassword);

		// 라우팅을 위한 DataSource 설정
		Map<Object, Object> targetDataSources = new HashMap<>();
		targetDataSources.put("master", masterDataSource);
		targetDataSources.put("replica", replicaDataSource);

		routingDataSource.setTargetDataSources(targetDataSources);
		routingDataSource.setDefaultTargetDataSource(masterDataSource); // 기본은 master를 사용하도록 설정
		routingDataSource.afterPropertiesSet(); // 설정을 적용하자

		return routingDataSource;
	}
}