package com.floney.floney.User;

import com.floney.floney.user.dto.UserResponse;
import com.floney.floney.user.dto.constant.Provider;
import com.floney.floney.user.dto.security.UserDetail;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class TestUserDetailService implements UserDetailsService {
    private static final UserDetail userDetail = UserDetail.of(
            UserResponse.builder()
                    .email("floney@naver.com")
                    .password("1234")
                    .provider(Provider.EMAIL.getName())
                    .subscribe(0)
                    .marketingAgree(0)
                    .profileImg("imageUrl")
                    .build());

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userDetail;
    }
}
