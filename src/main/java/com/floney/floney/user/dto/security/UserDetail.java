package com.floney.floney.user.dto.security;

import com.floney.floney.user.dto.OAuth2UserDto;
import com.floney.floney.user.dto.UserDto;
import com.floney.floney.user.dto.constant.Role;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Getter
@AllArgsConstructor
public class UserDetail implements UserDetails, OAuth2User {
    private final UserDto userDto;
    private final OAuth2UserDto oAuth2UserDto;
    private final Collection<? extends GrantedAuthority> authorities;

    public static UserDetail of(UserDto userDto) {
        Set<Role> roles = new HashSet<>();
        roles.add(Role.USER);

        if(userDto.getSubscribe() == Role.SUBSCRIBER.getStatus()) {
            roles.add(Role.SUBSCRIBER);
        }

        return new UserDetail(
                userDto,
                null,
                roles.stream()
                        .map(Role::getName)
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toUnmodifiableSet())
        );
    }

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return userDto.getPassword();
    }

    @Override
    public String getUsername() {
        return userDto.getEmail();
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

    @Override
    public String getName() {
        return oAuth2UserDto.getProviderId();
    }
}
