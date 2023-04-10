package com.quicklearninghub.database.repository;

import com.quicklearninghub.database.entity.ErrorCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ErrorCodeRepository extends JpaRepository<ErrorCodeEntity, String> {

    List<ErrorCodeEntity> findByAssociatedEntity(String associatedEntity);
}

