package ru.ssau.loanofferservice.security.config.principal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.ssau.loanofferservice.dto.enums.Role;
import ru.ssau.loanofferservice.jpa.entity.User;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsImpl implements UserDetails {

    @Getter
    private UUID id;
    private String username;
    private String password;

    private String email;
    private String fullname;
    private String phone;
    private String city;
    private List<? extends GrantedAuthority> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(roles.get(0).toString()));
    }

    public static UserDetailsImpl fromUser(User user) {
        return UserDetailsImpl.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getEncryptPassword())
                .email(user.getEmail())
                .city(user.getCity())
                .phone(user.getPhone())
                .fullname(user.getFullname())
                .roles(mapToGrantedAuthorities(user.getRole()))
                .build();
    }

    private static List<GrantedAuthority> mapToGrantedAuthorities(Role userRole) {
        return Collections.singletonList(new SimpleGrantedAuthority(userRole.name()));
    }

    public Role getRole(){
        List<Role> list = this.roles.stream()
                .map(authority -> Role.fromValue(authority.getAuthority()))
                .collect(Collectors.toList());

        return list.get(0);
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getFullname() {
        return fullname;
    }

    public String getPhone() {
        return phone;
    }

    public String getCity() {
        return city;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
