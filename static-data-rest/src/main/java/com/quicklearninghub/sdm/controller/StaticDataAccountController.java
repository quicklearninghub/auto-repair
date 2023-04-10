package com.quicklearninghub.sdm.controller;

import com.quicklearninghub.sdm.service.AccountService;
import com.quicklearninghub.database.entity.AccountEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class StaticDataAccountController {


    @Autowired
    AccountService accountService;


    @PutMapping("/{id}/risk/{riskId}")
    public ResponseEntity<AccountEntity> publishStaticDataUpdate(@PathVariable("id") Long accountId,
                                                          @PathVariable("riskId") Long riskId) {

        return accountService.updateAccountRiskId(accountId, riskId);
    }
}
