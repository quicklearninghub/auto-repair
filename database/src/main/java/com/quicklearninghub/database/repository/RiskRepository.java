package com.quicklearninghub.database.repository;

import com.quicklearninghub.database.entity.RiskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RiskRepository extends JpaRepository<RiskEntity, Long> {

}

