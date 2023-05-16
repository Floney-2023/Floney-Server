package com.floney.floney.User.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.floney.floney.common.exception.MailAddressException;
import com.floney.floney.common.token.dto.Token;
import com.floney.floney.user.dto.request.LoginRequest;
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
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
@AutoConfigureMockMvc
class UserControllerTest {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    @MockBean private UserService userService;

    @Autowired
    public UserControllerTest(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @Test
    @DisplayName("로그인에 성공한다")
    void login_success() throws Exception {
        // given
        LoginRequest requestUser = LoginRequest.builder()
                .email("success@email.com")
                .password("success")
                .build();
        given(userService.login(any(LoginRequest.class))).willReturn(new Token("", ""));

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
        given(userService.login(any(LoginRequest.class))).willThrow(BadCredentialsException.class);

        // when & then
        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestUser)))
                .andExpect(status().is4xxClientError());
        then(userService).should().login(requestUser);
    }

    @Test
    @DisplayName("인증용 이메일을 보내는 데 성공한다")
    void sendAuthenticateEmail_success() throws Exception {
        // given
        String email = "right@email.com";
        given(userService.sendAuthenticateEmail(email)).willReturn(anyString());

        // when & then
        mockMvc.perform(get("/users/email").queryParam("email", email))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN));
    }

    @Test
    @DisplayName("인증용 이메일을 보내는 데 실패한다 - 잘못된 이메일 형식")
    void sendAuthenticateEmail_fail_invalidEmail() throws Exception {
        // given
        String email = "wrong@email.com";
        given(userService.sendAuthenticateEmail(email)).willThrow(new MailAddressException());

        // when & then
        mockMvc.perform(get("/users/email").queryParam("email", email))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("인증용 이메일을 보내는 데 실패한다 - 메일 서버 오류")
    void sendAuthenticateEmail_fail_mailServerError() throws Exception {
        // given
        String email = "right@email.com";
        given(userService.sendAuthenticateEmail(email)).willThrow(MailSendException.class);

        // when & then
        mockMvc.perform(get("/users/email").queryParam("email", email))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }
}