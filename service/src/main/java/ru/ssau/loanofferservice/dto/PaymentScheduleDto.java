package ru.ssau.loanofferservice.dto;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentScheduleDto implements Serializable {

    private static final long serialVersionUID = 1344813117204650494L;

    private UUID id;
    private LocalDate datePayment;
    private BigDecimal amount;
    private BigDecimal body;
    private BigDecimal interest;

    private LoanOfferDto loanOffer;

    @Override
    public String toString() {
        return "PaymentScheduleDto{" +
                "id=" + id +
                ", datePayment=" + datePayment +
                ", amount=" + amount +
                ", body=" + body +
                ", interest=" + interest +
                ", loanOffer=" + loanOffer.getId().toString() +
                '}';
    }
}
