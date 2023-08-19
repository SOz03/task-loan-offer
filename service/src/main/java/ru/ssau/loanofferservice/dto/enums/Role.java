package ru.ssau.loanofferservice.dto.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Role {

    USER("Клиент банка"),

    ADMIN("Админ");

    private final String value;

    public static Role fromValue(String value) {
        for (Role role : values()) {
            if (role.name().equals(value)) {
                return role;
            }
        }
        return null;
    }
}
