package com.floney.floney.user.dto.security;

import com.floney.floney.user.dto.UserDto;
import com.floney.floney.user.dto.constant.Role;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@AllArgsConstructor
public class UserPrincipal implements UserDetails {
    private final String email;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    public static UserPrincipal of(String email,
                                   String password,
                                   int subscribe) {
        Set<Role> roles = new HashSet<>();
        roles.add(Role.USER);

        if(subscribe == 1) {
            roles.add(Role.SUBSCRIBER);
        }

        return new UserPrincipal(
                email,
                password,
                roles.stream()
                        .map(Role::getName)
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toUnmodifiableSet())
        );
    }

    public static UserPrincipal from(UserDto dto) {
        return UserPrincipal.of(dto.getEmail(), dto.getPassword(), dto.getSubscribe());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
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
