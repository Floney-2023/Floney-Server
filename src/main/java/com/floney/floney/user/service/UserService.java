package com.floney.floney.user.service;

import com.floney.floney.book.dto.process.MyBookInfo;
import com.floney.floney.book.dto.request.SaveRecentBookKeyRequest;
import com.floney.floney.book.domain.entity.BookUser;
import com.floney.floney.book.repository.BookUserRepository;
import com.floney.floney.common.exception.user.PasswordSameException;
import com.floney.floney.common.exception.user.UserFoundException;
import com.floney.floney.common.exception.user.UserNotFoundException;
import com.floney.floney.user.dto.constant.SignoutType;
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
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.floney.floney.common.constant.Status.ACTIVE;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BookUserRepository bookUserRepository;
    private final SignoutReasonRepository signoutReasonRepository;
    private final SignoutOtherReasonRepository signoutOtherReasonRepository;

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
        validatePasswordNotSame(password, user.getPassword());
        user.updatePassword(password);
        user.encodePassword(passwordEncoder);
        userRepository.save(user);
    }

    @Transactional
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

    @Transactional
    public void saveRecentBookKey(SaveRecentBookKeyRequest request, String username) {
        User user = findUserByEmail(username);
        user.saveRecentBookKey(request.getBookKey());
        userRepository.save(user);
    }

    private void validatePasswordNotSame(String newPassword, String oldPassword) {
        if (passwordEncoder.matches(newPassword, oldPassword)) {
            throw new PasswordSameException();
        }
    }

    private User findUserByEmail(final String request) {
        return userRepository.findByEmail(request)
                .orElseThrow(() -> new UserNotFoundException(request));
    }

    private void validateUserExistByEmail(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            throw new UserFoundException(user.getEmail(), user.getProvider());
        });
    }
}
