package ru.ssau.loanofferservice.jpa.entity;

import lombok.*;
import org.hibernate.annotations.Where;
import ru.ssau.loanofferservice.common.dao.entity.BaseEntity;
import ru.ssau.loanofferservice.dto.enums.Role;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "deleted_by is null")
@Table(name = "tr_users", schema = "data")
public class User extends BaseEntity {

    private static final long serialVersionUID = -2957871233893384863L;

    private String username;
    private String encryptPassword;

    private String fullname;
    private String email;
    private String phone;
    private String city;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", fullname='" + fullname + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", city='" + city + '\'' +
                ", role=" + role +
                '}';
    }
}
