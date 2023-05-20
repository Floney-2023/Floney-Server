package com.floney.floney.user.dto.security;

import com.floney.floney.user.dto.response.UserResponse;
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
public class UserDetail implements UserDetails {
    private final UserResponse userResponse;
    private final Collection<? extends GrantedAuthority> authorities;

    public static UserDetail of(UserResponse userResponse) {
        Set<Role> roles = new HashSet<>();
        roles.add(Role.USER);

        if(userResponse.isSubscribe()) {
            roles.add(Role.SUBSCRIBER);
        }

        return new UserDetail(
                userResponse,
                roles.stream()
                        .map(Role::getName)
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toUnmodifiableSet())
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return userResponse.getPassword();
    }

    @Override
    public String getUsername() {
        return userResponse.getEmail();
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
