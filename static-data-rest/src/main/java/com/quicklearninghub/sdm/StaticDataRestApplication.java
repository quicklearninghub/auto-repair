package com.quicklearninghub.sdm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"com.quicklearninghub.sdm","com.quicklearninghub.autorepair", "com.quicklearninghub.database"})
@EnableJpaRepositories(basePackages = "com.quicklearninghub.database.repository")
@EntityScan(basePackages = "com.quicklearninghub.database.entity")
public class StaticDataRestApplication {

	public static void main(String[] args) {
		SpringApplication.run(StaticDataRestApplication.class, args);
	}

}
