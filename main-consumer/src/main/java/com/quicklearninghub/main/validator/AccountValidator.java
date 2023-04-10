package com.quicklearninghub.main.validator;

import com.quicklearninghub.database.dto.Account;
import com.quicklearninghub.database.entity.AccountEntity;
import com.quicklearninghub.database.entity.ErrorCodeEntity;
import com.quicklearninghub.database.entity.RiskEntity;
import com.quicklearninghub.database.repository.AccountRepository;
import com.quicklearninghub.database.repository.ErrorCodeRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.quicklearninghub.database.enums.ErrorCode.ACCOUNT_ERROR_01;
import static java.util.Objects.nonNull;

@Service
@AllArgsConstructor
@Slf4j
public class AccountValidator {

    private final AccountRepository accountRepository;
    private final ErrorCodeRepository errorCodeRepository;

    public String validate(Account accountDTO) {
        log.info("Validating account {}", accountDTO);
        if(nonNull(accountDTO) && nonNull(accountDTO.getAccountId())) {
            Optional<AccountEntity> accountEntityOptional =
                    accountRepository.findById(Long.valueOf(accountDTO.getAccountId()));

            if (accountEntityOptional.isPresent() && !isAccountRisky(accountEntityOptional.get())) {
                log.info("Successfully validated the account");
                return null;
            }
        }
        return ACCOUNT_ERROR_01.name();
    }

    private boolean isAccountRisky(@NonNull AccountEntity accountEntity) {
        RiskEntity riskEntity = accountEntity.getRiskEntity();
        if(nonNull(riskEntity) && nonNull(riskEntity.getScore())
                                && riskEntity.getScore() > 50 ) {
            return true;
        }
        return false;
    }
}
