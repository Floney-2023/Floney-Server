package com.floney.floney.user.service;

import static com.floney.floney.common.constant.Status.ACTIVE;

import com.floney.floney.book.dto.process.MyBookInfo;
import com.floney.floney.book.dto.request.SaveRecentBookKeyRequest;
import com.floney.floney.book.entity.BookUser;
import com.floney.floney.book.repository.BookUserRepository;
import com.floney.floney.common.dto.Token;
import com.floney.floney.common.exception.user.CodeNotFoundException;
import com.floney.floney.common.exception.user.CodeNotSameException;
import com.floney.floney.common.exception.user.PasswordSameException;
import com.floney.floney.common.exception.user.UserFoundException;
import com.floney.floney.common.exception.user.UserNotFoundException;
import com.floney.floney.common.util.JwtProvider;
import com.floney.floney.common.util.MailProvider;
import com.floney.floney.common.util.RedisProvider;
import com.floney.floney.user.dto.constant.SignoutType;
import com.floney.floney.user.dto.request.EmailAuthenticationRequest;
import com.floney.floney.user.dto.request.LoginRequest;
import com.floney.floney.user.dto.request.SignoutRequest;
import com.floney.floney.user.dto.request.SignupRequest;
import com.floney.floney.user.dto.response.MyPageResponse;
import com.floney.floney.user.dto.response.UserResponse;
import com.floney.floney.user.dto.security.CustomUserDetails;
import com.floney.floney.user.entity.SignoutOtherReason;
import com.floney.floney.user.entity.User;
import com.floney.floney.user.repository.SignoutOtherReasonRepository;
import com.floney.floney.user.repository.SignoutReasonRepository;
import com.floney.floney.user.repository.UserRepository;
import io.jsonwebtoken.MalformedJwtException;
import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final RedisProvider redisProvider;
    private final MailProvider mailProvider;
    private final BookUserRepository bookUserRepository;
    private final SignoutReasonRepository signoutReasonRepository;
    private final SignoutOtherReasonRepository signoutOtherReasonRepository;

    @Transactional
    public Token login(final LoginRequest request) {
        try {
            final User user = findUserByEmail(request.getEmail());
            validatePasswordMatches(request, user.getPassword());

            user.login();
            userRepository.save(user);

            return jwtProvider.generateToken(user.getEmail());
        } catch (BadCredentialsException exception) {
            logger.warn("로그인 실패: [{}]", request.getEmail());
            throw exception;
        }
    }

    public String logout(final String accessToken) {
        jwtProvider.validateToken(accessToken);
        final String email = jwtProvider.getUsername(accessToken);

        if (redisProvider.get(email) != null) {
            redisProvider.delete(email);
        }

        final long expiration = jwtProvider.getExpiration(accessToken);
        redisProvider.set(accessToken, "logout", expiration);

        return email;
    }

    @Transactional
    public LoginRequest signup(SignupRequest request) {
        validateUserExistByEmail(request.getEmail());
        User user = request.to();
        user.encodePassword(passwordEncoder);
        userRepository.save(user);
        return request.toLoginRequest();
    }

    @Transactional
    public void signout(final String email, final SignoutRequest request) {
        final User user = findUserByEmail(email);
        user.signout();
        addSignoutReason(request);
    }

    private void addSignoutReason(final SignoutRequest request) {
        final SignoutType requestType = request.getType();

        if (SignoutType.OTHER.equals(requestType)) {
            addSignoutOtherReason(request);
        }

        signoutReasonRepository.increaseCount(requestType);
    }

    private void addSignoutOtherReason(final SignoutRequest request) {
        request.validateReasonNotEmpty();
        final SignoutOtherReason signoutOtherReason = SignoutOtherReason.from(request.getReason());
        signoutOtherReasonRepository.save(signoutOtherReason);
    }

    public Token reissueToken(Token token) {
        final String accessToken = token.getAccessToken();
        final String refreshToken = token.getRefreshToken();

        final String username = jwtProvider.getUsername(accessToken);
        final String redisRefreshToken = redisProvider.get(username);

        if (!refreshToken.equals(redisRefreshToken)) {
            throw new MalformedJwtException("");
        }

        return jwtProvider.generateToken(username);
    }

    public MyPageResponse getUserInfo(CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        List<MyBookInfo> myBooks = bookUserRepository.findMyBookInfos(user);
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
        user.encodePassword(passwordEncoder);
        userRepository.save(user);
    }

    private void validatePassword(String newPassword, String oldPassword) {
        if (passwordEncoder.matches(newPassword, oldPassword)) {
            throw new PasswordSameException();
        }
    }

    public void updatePassword(String password, String email) {
        User user = findUserByEmail(email);
        updatePassword(password, user);
    }

    @Transactional
    public void updateProfileImg(String profileImg, CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        user.updateProfileImg(profileImg);
        userRepository.save(user);

        List<BookUser> bookUsers = bookUserRepository.findByUserAndStatus(user, ACTIVE);
        bookUsers.forEach(bookUser -> {
            bookUser.updateProfileImg(profileImg);
            bookUserRepository.save(bookUser);
        });
    }

    public String sendEmailAuthMail(String email) {
        validateUserExistByEmail(email);

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
            throw new CodeNotFoundException(requestEmail);
        }

        final String code = redisProvider.get(requestEmail);
        if (!code.equals(requestCode)) {
            throw new CodeNotSameException(code, requestCode);
        }
    }

    @Transactional
    public void saveRecentBookKey(SaveRecentBookKeyRequest request, String username) {
        User user = findUserByEmail(username);
        user.saveRecentBookKey(request);
        userRepository.save(user);
    }

    private User findUserByEmail(final String request) {
        return userRepository.findByEmail(request)
                .orElseThrow(() -> new UserNotFoundException(request));
    }

    private void validatePasswordMatches(final LoginRequest request, final String user) {
        if (!passwordEncoder.matches(request.getPassword(), user)) {
            throw new BadCredentialsException(request.getEmail());
        }
    }

    private void validateUserExistByEmail(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            throw new UserFoundException(user.getEmail(), user.getProvider());
        });
    }
}
