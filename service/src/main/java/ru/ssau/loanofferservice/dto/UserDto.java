package ru.ssau.loanofferservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto implements Serializable {

    private static final long serialVersionUID = 8590128996608039324L;

    private String username;
    private String fullname;
    private String email;
    private String phone;
    private String city;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
}
