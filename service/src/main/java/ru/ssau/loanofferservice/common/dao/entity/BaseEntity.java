package ru.ssau.loanofferservice.common.dao.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Setter
@MappedSuperclass
public class BaseEntity implements Serializable {

    private static final long serialVersionUID = -3317855753468826226L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false, updatable = false)
    private Long createdAt = ZonedDateTime.now(Clock.systemUTC()).toInstant().toEpochMilli();

    @Column(nullable = false)
    private Long updatedAt = ZonedDateTime.now(Clock.systemUTC()).toInstant().toEpochMilli();

    @Column
    private UUID deletedBy;

}

