package ru.ssau.loanofferservice.dto;

import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankDto implements Serializable {

    private static final long serialVersionUID = 2602951220800185246L;

    private UUID id;
    private String bankName;

    private List<UserDto> users = new ArrayList<>();
    private List<CreditDto> credits = new ArrayList<>();

    @Override
    public String toString() {
        return "BankDto{" +
                "id=" + id +
                ", bankName='" + bankName +
                '}';
    }
}
