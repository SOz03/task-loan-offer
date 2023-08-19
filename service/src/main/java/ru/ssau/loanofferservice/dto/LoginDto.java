package ru.ssau.loanofferservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import ru.ssau.loanofferservice.dto.enums.Role;

import java.io.Serializable;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginDto implements Serializable {

    private static final long serialVersionUID = 6142211678507815546L;

    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Role role;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String token;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String fullname;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String email;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String phone;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String city;

    public LoginDto(String username, Role role, String email, String fullname,
                    String phone, String city, String token) {
        this.username = username;
        this.role = role;
        this.email = email;
        this.fullname = fullname;
        this.phone = phone;
        this.city = city;
        this.token = token;
    }

}
