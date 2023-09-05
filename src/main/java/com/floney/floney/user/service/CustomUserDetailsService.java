package com.floney.floney.user.service;

import com.floney.floney.common.exception.user.UserFoundException;
import com.floney.floney.common.exception.user.UserNotFoundException;
import com.floney.floney.common.exception.user.UserSignoutException;
import com.floney.floney.user.dto.security.CustomUserDetails;
import com.floney.floney.user.entity.User;
import com.floney.floney.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByEmail(username).orElseThrow(() -> new UserNotFoundException(username));
        if (user.isInactive()) {
            throw new UserSignoutException();
        }
        return CustomUserDetails.of(user);
    }

    public void validateIfNewUser(String email) {
        try {
            User user = ((CustomUserDetails) loadUserByUsername(email)).getUser();
            throw new UserFoundException(user.getEmail(), user.getProvider());
        } catch (UserNotFoundException ignored) {
        }
    }

}
