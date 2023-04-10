package com.quicklearninghub.main;

import org.h2.tools.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.sql.SQLException;

@SpringBootApplication
@ComponentScan(basePackages = "com.quicklearninghub")
@EnableJpaRepositories(basePackages = "com.quicklearninghub.database.repository")
@EntityScan(basePackages = "com.quicklearninghub.database.entity")
public class MainConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MainConsumerApplication.class, args);
	}

	@Bean(initMethod = "start", destroyMethod = "stop")
	public Server h2Server() throws SQLException {
		return Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9292");
	}
}
