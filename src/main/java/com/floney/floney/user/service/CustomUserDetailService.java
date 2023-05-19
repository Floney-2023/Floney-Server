package com.floney.floney.user.service;

import com.floney.floney.common.exception.UserNotFoundException;
import com.floney.floney.user.dto.UserResponse;
import com.floney.floney.user.dto.security.UserDetail;
import com.floney.floney.user.entity.User;
import com.floney.floney.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByEmail(username).orElseThrow(UserNotFoundException::new);
        return UserDetail.of(UserResponse.from(user));
    }
}
