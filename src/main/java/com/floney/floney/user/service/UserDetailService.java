package com.floney.floney.user.service;

import com.floney.floney.common.BaseException;
import com.floney.floney.common.BaseResponseStatus;
import com.floney.floney.user.dto.UserDto;
import com.floney.floney.user.dto.security.UserPrincipal;
import com.floney.floney.user.entity.User;
import com.floney.floney.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByEmail(username);
        if(user == null) {
            throw new UsernameNotFoundException(username);
        }

        return UserPrincipal.from(
                UserDto.from(user)
        );
    }
}
