package com.quicklearninghub.database.entity;

import jakarta.persistence.*;
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
@Table(name = "FAILED_MESSAGE")
public class FailedMessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "failed_msg_seq")
    @SequenceGenerator(name = "failed_msg_seq", sequenceName = "FAILED_MESSAGE_SEQ", allocationSize = 1)
    private Long id;

    @Column(name = "MSG", nullable = false, length = 3000)
    private String message;

    @Column(name = "ERROR_CODE", nullable = false, length = 1000)
    private String errorCode;

    @Column(name = "STATUS", nullable = false, length = 1000)
    private String status;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        FailedMessageEntity that = (FailedMessageEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

