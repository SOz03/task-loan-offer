package ru.ssau.loanofferservice.jpa.entity;

import lombok.*;
import org.hibernate.annotations.Where;
import ru.ssau.loanofferservice.common.dao.entity.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "deleted_by is null")
@Table(name = "tr_credits", schema = "data")
public class Credit extends BaseEntity {

    private BigDecimal limitation;
    private BigDecimal rate;
    private Long deadline;

    @Override
    public String toString() {
        return "Credit{" +
                "limitation=" + limitation +
                ", rate=" + rate +
                ", deadline=" + deadline +
                '}';
    }
}
