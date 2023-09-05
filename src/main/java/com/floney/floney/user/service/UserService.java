package com.floney.floney.user.service;

import com.floney.floney.book.dto.process.MyBookInfo;
import com.floney.floney.book.dto.request.SaveRecentBookKeyRequest;
import com.floney.floney.book.entity.BookUser;
import com.floney.floney.book.repository.BookUserRepository;
import com.floney.floney.common.dto.Token;
import com.floney.floney.common.exception.user.CodeNotSameException;
import com.floney.floney.common.exception.user.EmailNotFoundException;
import com.floney.floney.common.exception.user.PasswordSameException;
import com.floney.floney.common.exception.user.UserNotFoundException;
import com.floney.floney.common.util.JwtProvider;
import com.floney.floney.common.util.MailProvider;
import com.floney.floney.common.util.RedisProvider;
import com.floney.floney.user.dto.request.EmailAuthenticationRequest;
import com.floney.floney.user.dto.request.LoginRequest;
import com.floney.floney.user.dto.request.SignupRequest;
import com.floney.floney.user.dto.request.SubscribeRequest;
import com.floney.floney.user.dto.response.MyPageResponse;
import com.floney.floney.user.dto.response.SubscribeResponse;
import com.floney.floney.user.dto.response.UserResponse;
import com.floney.floney.user.dto.security.CustomUserDetails;
import com.floney.floney.user.entity.Subscribe;
import com.floney.floney.user.entity.User;
import com.floney.floney.user.repository.SubscribeRepository;
import com.floney.floney.user.repository.UserRepository;
import io.jsonwebtoken.MalformedJwtException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
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

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder bCryptPasswordEncoder;
    private final RedisProvider redisProvider;
    private final MailProvider mailProvider;
    private final BookUserRepository bookUserRepository;
    private final CustomUserDetailsService customUserDetailsService;
    private final SubscribeRepository subscribeRepository;

    public Token login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            return jwtProvider.generateToken(authentication);
        } catch (BadCredentialsException exception) {
            logger.warn("카카오 로그인 실패: [{}]", request.getEmail());
            throw exception;
        } catch (AccountStatusException exception) {
            logger.error("카카오 로그인 오류: {}", exception.getMessage());
            throw exception;
        }
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
    public LoginRequest signup(SignupRequest request) {
        User user = request.to();
        user.encodePassword(bCryptPasswordEncoder);
        userRepository.save(user);
        return request.toLoginRequest();
    }

    @Transactional
    public void signout(String email) {
        User user = ((CustomUserDetails) customUserDetailsService.loadUserByUsername(email)).getUser();
        user.delete();
        deleteAllBookAccountsBy(user);
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

    public MyPageResponse getUserInfo(CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        List<MyBookInfo> myBooks = bookUserRepository.findMyBooks(user);
        return MyPageResponse.from(UserResponse.from(user), myBooks);
    }

    @Transactional
    public void updateNickname(String nickname, CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        user.updateNickname(nickname);
        userRepository.save(user);
    }

    @Transactional
    public void updatePassword(String password, User user) {
        validatePassword(password, user.getPassword());
        user.updatePassword(password);
        user.encodePassword(bCryptPasswordEncoder);
        userRepository.save(user);
    }

    private void validatePassword(String newPassword, String oldPassword) {
        if (bCryptPasswordEncoder.matches(newPassword, oldPassword)) {
            throw new PasswordSameException();
        }
    }

    public void updatePassword(String password, String email) {
        User user = ((CustomUserDetails) customUserDetailsService.loadUserByUsername(email)).getUser();
        updatePassword(password, user);
    }

    @Transactional
    public void updateProfileImg(String profileImg, CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        user.updateProfileImg(profileImg);
        userRepository.save(user);

        List<BookUser> bookUsers = bookUserRepository.findByUser(user);
        for (BookUser bookUser : bookUsers) {
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

    public void authenticateEmail(EmailAuthenticationRequest emailAuthenticationRequest) {
        final String requestEmail = emailAuthenticationRequest.getEmail();
        final String requestCode = emailAuthenticationRequest.getCode();

        if (!redisProvider.hasKey(requestEmail)) {
            throw new EmailNotFoundException(requestEmail);
        }
        if (!redisProvider.get(requestEmail).equals(requestCode)) {
            throw new CodeNotSameException(requestEmail, requestCode);
        }
    }

    @Transactional
    public void deleteAllBookAccountsBy(User user) {
        userRepository.save(user);
        List<BookUser> myBookAccounts = bookUserRepository.findByUser(user);
        for (BookUser myAccounts : myBookAccounts) {
            myAccounts.delete();
            bookUserRepository.save(myAccounts);
        }
    }

    @Transactional
    public void saveRecentBookKey(SaveRecentBookKeyRequest request, String username) {
        User user = userRepository.findByEmail(username)
            .orElseThrow(() -> new UserNotFoundException(username));
        user.saveRecentBookKey(request);
        userRepository.save(user);
    }

    @Transactional
    public void saveSubscribe(SubscribeRequest request, User user) {
        user.subscribe();

        Subscribe subscribe = Subscribe.of(user, request);
        subscribeRepository.save(subscribe);
    }

    @Transactional
    public void updateSubscribe(SubscribeRequest request, User user){
        Subscribe subscribe = subscribeRepository.findSubscribeByUser(user);
        subscribe.update(request);
    }

    @Transactional(readOnly = true)
    public SubscribeResponse getSubscribe(User user) {
        Subscribe subscribe = subscribeRepository.findSubscribeByUser(user);
        return SubscribeResponse.of(subscribe);
    }
}
