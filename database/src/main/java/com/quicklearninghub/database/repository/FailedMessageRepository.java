package com.quicklearninghub.database.repository;

import com.quicklearninghub.database.entity.FailedMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FailedMessageRepository extends JpaRepository<FailedMessageEntity, Long> {

    List<FailedMessageEntity> findByErrorCodeInAndStatus(List<String> errorCodes, String status);
}

