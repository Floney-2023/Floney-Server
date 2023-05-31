package com.floney.floney.user.service;

import com.floney.floney.book.dto.MyBookInfo;
import com.floney.floney.book.entity.BookUser;
import com.floney.floney.book.repository.BookUserRepository;
import com.floney.floney.common.MailProvider;
import com.floney.floney.common.exception.*;
import com.floney.floney.common.token.JwtProvider;
import com.floney.floney.common.token.RedisProvider;
import com.floney.floney.common.token.dto.Token;
import com.floney.floney.user.dto.request.EmailAuthenticationRequest;
import com.floney.floney.user.dto.request.LoginRequest;
import com.floney.floney.user.dto.request.SignupRequest;
import com.floney.floney.user.dto.response.MyPageResponse;
import com.floney.floney.user.dto.response.UserResponse;
import com.floney.floney.user.entity.User;
import com.floney.floney.user.repository.UserRepository;
import io.jsonwebtoken.MalformedJwtException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder bCryptPasswordEncoder;
    private final RedisProvider redisProvider;
    private final MailProvider mailProvider;
    private final BookUserRepository bookUserRepository;

    public Token login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        return jwtProvider.generateToken(authentication);
    }

    public String logout(String accessToken) {
        jwtProvider.validateToken(accessToken);
        Authentication authentication = jwtProvider.getAuthentication(accessToken);
        String email = authentication.getName();

        if (redisProvider.get(email) != null) {
            redisProvider.delete(email);
        }

        long expiration = jwtProvider.getExpiration(accessToken);
        redisProvider.set(accessToken, "logout", expiration);

        return email;
    }

    @Transactional
    public LoginRequest signup(SignupRequest signupRequest) {
        User user = signupRequest.to();
        user.encodePassword(bCryptPasswordEncoder);
        userRepository.save(user);
        return signupRequest.toLoginRequest();
    }

    @Transactional
    public void signout(String email) {
        User user = loadUserByEmail(email);

        if (!user.isStatus()) {
            throw new UserSignoutException();
        }

        user.signout();
        userRepository.save(user);
    }

    public Token reissueToken(Token token) {
        String accessToken = token.getAccessToken();
        String refreshToken = token.getRefreshToken();

        Authentication authentication = jwtProvider.getAuthentication(accessToken);
        String redisRefreshToken = redisProvider.get(authentication.getName());

        if (!refreshToken.equals(redisRefreshToken)) {
            throw new MalformedJwtException("");
        }

        return jwtProvider.generateToken(authentication);
    }

    public MyPageResponse getUserInfo(String email) {
        User user = loadUserByEmail(email);
        List<MyBookInfo> myBooks = bookUserRepository.findMyBooks(user);
        return MyPageResponse.from(UserResponse.from(user), myBooks);
    }

    @Transactional
    public void updateNickname(String nickname, String email) {
        User user = loadUserByEmail(email);
        user.updateNickname(nickname);
    }

    @Transactional
    public void updatePassword(String email, String password) {
        User user = loadUserByEmail(email);
        user.updatePassword(password);
        user.encodePassword(bCryptPasswordEncoder);
    }

    @Transactional
    public void updateProfileImg(String profileImg, String email) {
        User user = loadUserByEmail(email);
        user.updateProfileImg(profileImg);
        userRepository.save(user);

        BookUser bookUser = bookUserRepository.findByUser(user)
            .orElse(null);
        if (bookUser != null) {
            bookUser.updateProfileImg(profileImg);
            bookUserRepository.save(bookUser);
        }
    }

    public String sendEmailAuthMail(String email) {
        Random random = new Random();
        random.setSeed(System.currentTimeMillis());
        String code = String.format("%06d", random.nextInt(1_000_000) % 1_000_000);

        String mailSubject = "[Floney] 이메일 인증 코드";
        String mailText = String.format("인증 코드: %s\n앱으로 돌아가서 인증을 완료해주세요.\n", code);

        mailProvider.sendMail(email, mailSubject, mailText);
        redisProvider.set(email, code, 1000 * 60 * 5);
        return code;
    }

    public String sendPasswordFindEmail(String email) {
        String newPassword = RandomStringUtils.random(50, true, true);

        String mailSubject = "[Floney] 새 비밀번호 안내";
        String mailText = String.format("새 비밀번호: %s\n바뀐 비밀번호로 로그인 해주세요.\n", newPassword);

        mailProvider.sendMail(email, mailSubject, mailText);
        return newPassword;
    }

    public User loadUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
    }

    public void validateIfNewUser(String email) {
        try {
            User user = loadUserByEmail(email);
            throw new UserFoundException(user.getProvider());
        } catch (UserNotFoundException ignored) {
        }
    }

    public void authenticateEmail(EmailAuthenticationRequest emailAuthenticationRequest) {
        if (!redisProvider.hasKey(emailAuthenticationRequest.getEmail())) {
            throw new EmailNotFoundException();
        } else if (!redisProvider.get(emailAuthenticationRequest.getEmail())
            .equals(emailAuthenticationRequest.getCode())) {
            throw new CodeNotSameException();
        }
    }
}
