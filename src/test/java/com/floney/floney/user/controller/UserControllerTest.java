package com.floney.floney.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.floney.floney.common.dto.Token;
import com.floney.floney.common.exception.user.MailAddressException;
import com.floney.floney.common.exception.user.UserNotFoundException;
import com.floney.floney.config.WithMockCustomUser;
import com.floney.floney.user.dto.constant.SignoutType;
import com.floney.floney.user.dto.request.LoginRequest;
import com.floney.floney.user.dto.request.PasswordAuthenticateRequest;
import com.floney.floney.user.dto.request.SignoutRequest;
import com.floney.floney.user.dto.request.SignupRequest;
import com.floney.floney.user.dto.security.CustomUserDetails;
import com.floney.floney.user.service.AuthenticationService;
import com.floney.floney.user.service.CustomUserDetailsService;
import com.floney.floney.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mail.MailSendException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
@AutoConfigureMockMvc
class UserControllerTest {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    public UserControllerTest(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @Test
    @DisplayName("회원가입에 성공한다")
    void signup_success() throws Exception {
        // given
        SignupRequest signupRequest = SignupRequest.builder()
                .email("test@email.com")
                .password("password")
                .nickname("nickname")
                .receiveMarketing(true)
                .build();
        LoginRequest loginRequest = LoginRequest.builder()
                .email("success@email.com")
                .password("success")
                .build();
        given(userService.signup(signupRequest)).willReturn(loginRequest);
        given(authenticationService.login(any(LoginRequest.class)))
                .willReturn(new Token("token", "token"));

        // when & then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("회원탈퇴에 성공한다")
    void signout_success() throws Exception {
        // given
        final SignoutRequest signoutRequest = new SignoutRequest(SignoutType.EXPENSIVE, null);

        // when & then
        mockMvc.perform(delete("/users")
                        .queryParam("accessToken", "token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signoutRequest)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("로그인에 성공한다")
    void login_success() throws Exception {
        // given
        LoginRequest requestUser = LoginRequest.builder()
                .email("success@email.com")
                .password("success")
                .build();
        given(authenticationService.login(any(LoginRequest.class)))
                .willReturn(new Token("token", "token"));

        // when & then
        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestUser)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("로그인에 실패한다 - 잘못된 아이디 혹은 비밀번호")
    void login_fail_invalidInput() throws Exception {
        // given
        LoginRequest requestUser = LoginRequest.builder()
                .email("wrong@email.com")
                .password("wrong")
                .build();
        given(authenticationService.login(any(LoginRequest.class))).willThrow(BadCredentialsException.class);

        // when & then
        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestUser)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("인증용 이메일을 보내는 데 성공한다")
    void sendAuthenticateEmail_success() throws Exception {
        // given
        String email = "right@email.com";
        given(customUserDetailsService.loadUserByUsername(email)).willReturn(any(UserDetails.class));
        given(authenticationService.sendEmailAuthMail(email)).willReturn("");

        // when & then
        mockMvc.perform(get("/users/email/mail").queryParam("email", email))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("인증용 이메일을 보내는 데 실패한다 - 잘못된 이메일 형식")
    void sendAuthenticateEmail_fail_invalidEmail() throws Exception {
        // given
        String email = "wrong@email.com";
        given(authenticationService.sendEmailAuthMail(email)).willThrow(new MailAddressException(""));

        // when & then
        mockMvc.perform(get("/users/email/mail").queryParam("email", email))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("인증용 이메일을 보내는 데 실패한다 - 메일 서버 오류")
    void sendAuthenticateEmail_fail_mailServerError() throws Exception {
        // given
        String email = "right@email.com";
        given(authenticationService.sendEmailAuthMail(email)).willThrow(MailSendException.class);

        // when & then
        mockMvc.perform(get("/users/email/mail").queryParam("email", email))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("회원정보 얻기에 실패한다 - 존재하지 않는 회원")
    @WithAnonymousUser
    void getUserInfo_fail_throws_userNotFoundException() throws Exception {
        // given
        given(userService.getUserInfo(any(CustomUserDetails.class))).willThrow(new UserNotFoundException(""));

        // when & then
        mockMvc.perform(get("/users/mypage"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("요청한 비밀번호가 일치하는 데 성공한다")
    @WithMockCustomUser
    void authenticatePassword_success() throws Exception {
        // given
        final PasswordAuthenticateRequest request = new PasswordAuthenticateRequest("password");

        // when & then
        mockMvc.perform(post("/users/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}
