package com.floney.floney.user.service;

import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.book.domain.entity.BookUser;
import com.floney.floney.book.dto.process.MyBookInfo;
import com.floney.floney.book.dto.request.SaveRecentBookKeyRequest;
import com.floney.floney.book.repository.BookRepository;
import com.floney.floney.book.repository.BookUserRepository;
import com.floney.floney.common.exception.user.PasswordSameException;
import com.floney.floney.common.exception.user.UserFoundException;
import com.floney.floney.common.exception.user.UserNotFoundException;
import com.floney.floney.common.util.MailProvider;
import com.floney.floney.user.domain.vo.RegeneratePasswordMail;
import com.floney.floney.user.dto.constant.SignoutType;
import com.floney.floney.user.dto.request.LoginRequest;
import com.floney.floney.user.dto.request.SignoutRequest;
import com.floney.floney.user.dto.request.SignupRequest;
import com.floney.floney.user.dto.response.MyPageResponse;
import com.floney.floney.user.dto.response.ReceiveMarketingResponse;
import com.floney.floney.user.dto.response.SignoutResponse;
import com.floney.floney.user.dto.response.UserResponse;
import com.floney.floney.user.dto.security.CustomUserDetails;
import com.floney.floney.user.entity.SignoutOtherReason;
import com.floney.floney.user.entity.User;
import com.floney.floney.user.repository.SignoutOtherReasonRepository;
import com.floney.floney.user.repository.SignoutReasonRepository;
import com.floney.floney.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.floney.floney.common.constant.Status.ACTIVE;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BookRepository bookRepository;
    private final BookUserRepository bookUserRepository;
    private final SignoutReasonRepository signoutReasonRepository;
    private final SignoutOtherReasonRepository signoutOtherReasonRepository;
    private final MailProvider mailProvider;

    public LoginRequest signup(SignupRequest request) {
        validateUserExistByEmail(request.getEmail());
        final User user = request.to();
        user.encodePassword(passwordEncoder);
        userRepository.save(user);
        return request.toLoginRequest();
    }

    public SignoutResponse signout(final String email, final SignoutRequest request) {
        final User user = findUserByEmail(email);

        final List<String> deletedBookKeys = new ArrayList<>();
        final List<String> notDeletedBookKeys = new ArrayList<>();

        leaveBooks(user, deletedBookKeys, notDeletedBookKeys);

        user.signout();
        addSignoutReason(request);

        return SignoutResponse.of(deletedBookKeys, notDeletedBookKeys);
    }

    @Transactional(readOnly = true)
    public MyPageResponse getUserInfo(CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        List<MyBookInfo> myBooks = bookUserRepository.findMyBookInfos(user);
        return MyPageResponse.from(UserResponse.from(user), myBooks);
    }

    public void updateNickname(String nickname, CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        user.updateNickname(nickname);
        userRepository.save(user);
    }

    public void updatePassword(final String password, final String email) {
        final User user = findUserByEmail(email);
        updatePassword(password, user);
    }

    public void updateRegeneratedPassword(final String email) {
        final User user = findUserByEmail(email);
        user.validateEmailUser();

        final String newPassword = generatePassword(email);
        updatePassword(newPassword, user);
    }

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

    public void saveRecentBookKey(SaveRecentBookKeyRequest request, String username) {
        User user = findUserByEmail(username);
        user.saveRecentBookKey(request.getBookKey());
        userRepository.save(user);
    }

    public void updateReceiveMarketing(final boolean receiveMarketing, final String username) {
        final User user = findUserByEmail(username);
        user.updateReceiveMarketing(receiveMarketing);
    }

    @Transactional(readOnly = true)
    public ReceiveMarketingResponse getReceiveMarketing(final String email) {
        return new ReceiveMarketingResponse(findUserByEmail(email).isReceiveMarketing());
    }

    private void leaveBooks(final User user,
                            final List<String> deletedBookKeys,
                            final List<String> notDeletedBookKeys) {
        final List<Book> involvedBooks = bookRepository.findAllByUserEmail(user.getEmail());
        for (final Book book : involvedBooks) {
            if (!book.isOwner(user.getEmail())) {
                notDeletedBookKeys.add(book.getBookKey());
                continue;
            }

            final Optional<String> newOwner = bookUserRepository.findOldestBookUserEmailExceptOwner(user, book);
            if (newOwner.isPresent()) {
                book.delegateOwner(newOwner.get());
                notDeletedBookKeys.add(book.getBookKey());
                continue;
            }
            deletedBookKeys.add(book.getBookKey());
            book.inactive();
        }
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

    private void updatePassword(String newPassword, User user) {
        validatePasswordNotSame(newPassword, user);
        user.updatePassword(newPassword);
        user.encodePassword(passwordEncoder);
    }

    private String generatePassword(final String email) {
        final String newPassword = RandomStringUtils.random(50, true, true);
        final RegeneratePasswordMail mail = RegeneratePasswordMail.create(email, newPassword);
        mailProvider.sendMail(mail);
        return newPassword;
    }

    private void validatePasswordNotSame(final String newPassword, final User user) {
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            logger.warn("기존 비밀번호로 비밀번호 변경 요청 - 요청자: {}", user.getEmail());
            throw new PasswordSameException();
        }
    }

    private User findUserByEmail(final String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
    }

    private void validateUserExistByEmail(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            throw new UserFoundException(user.getEmail(), user.getProvider());
        });
    }
}
