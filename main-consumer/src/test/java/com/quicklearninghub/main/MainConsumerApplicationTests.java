package com.quicklearninghub.main;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quicklearninghub.database.dto.Account;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MainConsumerApplicationTests {

	private final String ACCOUNT_UPD_MSG = "{\n    \"accountId\": \"12345678\"\n}";

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void contextLoads() {
	}

	@Test
	void testMapping() throws JsonProcessingException {
		Account accountObject = new Account();
		accountObject.setAccountId("12345678");
		Account account = objectMapper.readValue(objectMapper.writeValueAsString(accountObject), Account.class);
		Assertions.assertNotNull(account);
		Assertions.assertNotNull(account.getAccountId());
		Assertions.assertEquals("12345678", account.getAccountId());
	}

}
