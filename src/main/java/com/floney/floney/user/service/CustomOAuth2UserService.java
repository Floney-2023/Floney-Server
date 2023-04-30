package com.floney.floney.user.service;

import com.floney.floney.user.dto.KakaoUserDto;
import com.floney.floney.user.dto.OAuth2UserDto;
import com.floney.floney.user.dto.UserDto;
import com.floney.floney.user.dto.constant.Provider;
import com.floney.floney.user.dto.security.UserDetail;
import com.floney.floney.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String provider = userRequest.getClientRegistration().getRegistrationId();

        OAuth2UserDto oAuth2UserDto;
        if (Provider.KAKAO.getName().equals(provider)) {
            oAuth2UserDto = KakaoUserDto.of(oAuth2User.getAttributes());
        } else {
            throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_CLIENT);
        }

        UserDto userDto = UserDto.from(userRepository.findByEmail(oAuth2UserDto.getEmail())
                .orElseGet(() -> userRepository.save(oAuth2UserDto.toUserDto().to())));

        return UserDetail.of(userDto);
    }

}
