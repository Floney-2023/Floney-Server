package com.floney.floney.config;

import com.floney.floney.user.dto.constant.Provider;
import com.floney.floney.user.dto.security.CustomUserDetails;
import com.floney.floney.user.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

//@Service
public class TestUserDetailService implements UserDetailsService {
    private static final CustomUserDetails CUSTOM_USER_DETAILS = CustomUserDetails.of(
            User.builder()
                    .email("floney@naver.com")
                    .password("1234")
                    .provider(Provider.EMAIL.getName())
                    .subscribe(false)
                    .marketingAgree(false)
                    .profileImg("imageUrl")
                    .build());

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return CUSTOM_USER_DETAILS;
    }
}
