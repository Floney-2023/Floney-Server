package com.floney.floney.user.controller;

import com.floney.floney.common.dto.Token;
import com.floney.floney.user.dto.request.EmailAuthenticationRequest;
import com.floney.floney.user.dto.request.LoginRequest;
import com.floney.floney.user.dto.request.SignupRequest;
import com.floney.floney.user.dto.security.CustomUserDetails;
import com.floney.floney.user.service.CustomUserDetailsService;
import com.floney.floney.user.service.UserService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final CustomUserDetailsService customUserDetailsService;

    /**
     * 회원가입
     * @param request 회원 가입 요청
     * @return 회원가입한 유저의 access token
     */
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody @Valid SignupRequest request) {
        customUserDetailsService.validateIfNewUser(request.getEmail());
        return new ResponseEntity<>(userService.login(userService.signup(request)), HttpStatus.CREATED);
    }

    /**
     * 이메일 인증 메일 전송
     * @param email 이메일 주소
     */
    @GetMapping("/email/mail")
    public ResponseEntity<?> sendEmailAuthMail(@RequestParam String email) {
        customUserDetailsService.validateIfNewUser(email);
        userService.sendEmailAuthMail(email);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 이메일 인증 메일의 코드를 검사
     * @param request 이메일 주소와 인증 코드
     */
    @PostMapping("/email/mail")
    public ResponseEntity<?> authenticateEmail(@RequestBody @Valid EmailAuthenticationRequest request) {
        userService.authenticateEmail(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 로그인
     * @param request 로그인 요청
     * @return 로그인 한 유저의 access token
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest request) {
        return new ResponseEntity<>(userService.login(request), HttpStatus.OK);
    }

    /**
     * 로그아웃
     * @param accessToken 로그아웃 할 유저의 access token
     */
    @GetMapping("/logout")
    public ResponseEntity<?> logout(@RequestParam String accessToken) {
        userService.logout(accessToken);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * token 재발급
     * @param token 기존 토큰
     * @return 새로운 토큰
     */
    @PostMapping("/reissue")
    public ResponseEntity<?> reissueToken(@RequestBody @Valid Token token) {
        return new ResponseEntity<>(userService.reissueToken(token), HttpStatus.CREATED);
    }

    /**
     * 회원 탈퇴
     * @param accessToken 탈퇴할 유저의 access token
     */
    @GetMapping("/signout")
    public ResponseEntity<?> signout(@RequestParam String accessToken) {
        userService.signout(userService.logout(accessToken));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 회원 닉네임 수정
     * @param nickname 수정할 닉네임
     * @param userDetails 접속한 유저 정보
     */
    @GetMapping("/nickname/update")
    public ResponseEntity<?> updateNickname(@RequestParam String nickname, @AuthenticationPrincipal CustomUserDetails userDetails) {
        userService.updateNickname(nickname, userDetails);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 회원 비밀번호 수정
     * @param password 수정할 비밀번호
     * @param userDetails 접속한 유저 정보
     */
    @GetMapping("/password/update")
    public ResponseEntity<?> updatePassword(@RequestParam String password, @AuthenticationPrincipal CustomUserDetails userDetails) {
        userService.updatePassword(password, userDetails.getUser());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 비밀번호 재발급 메일 전송
     * @param email 회원 이메일 주소
     */
    @GetMapping("/password/find")
    public ResponseEntity<?> findPassword(@RequestParam String email) {
        String password = userService.sendPasswordFindEmail(email);
        userService.updatePassword(password, email);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 회원 프로필 이미지 수정
     * @param profileImg 새 프로필 이미지 주소
     * @param userDetails 접속한 유저 정보
     */
    @GetMapping("/profileimg/update")
    public ResponseEntity<?> updateProfileImg(@RequestParam String profileImg, @AuthenticationPrincipal CustomUserDetails userDetails) {
        userService.updateProfileImg(profileImg, userDetails);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 마이페이지 조회
     * @param userDetails 접속한 유저 정보
     * @return 마이페이지 정보
     */
    @GetMapping("/mypage")
    public ResponseEntity<?> getMyPage(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return new ResponseEntity<>(userService.getUserInfo(userDetails), HttpStatus.OK);
    }

}
