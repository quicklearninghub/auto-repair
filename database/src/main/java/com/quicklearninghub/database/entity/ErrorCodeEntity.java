package com.quicklearninghub.database.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table(name = "ERROR_CODE")
public class ErrorCodeEntity {
    @Id
    @Column(name = "CODE", nullable = false)
    private String code;

    @Column(name = "ERROR_MSG", nullable = false)
    private String errorMessage;

    @Column(name = "ASSOCIATED_ENTITY")
    private String associatedEntity;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ErrorCodeEntity that = (ErrorCodeEntity) o;
        return getCode() != null && Objects.equals(getCode(), that.getCode());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}