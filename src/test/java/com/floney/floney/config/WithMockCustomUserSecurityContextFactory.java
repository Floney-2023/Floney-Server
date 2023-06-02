package com.floney.floney.config;

import com.floney.floney.common.constant.Status;
import com.floney.floney.user.dto.response.UserResponse;
import com.floney.floney.user.dto.constant.Provider;
import com.floney.floney.user.dto.security.UserDetail;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {
    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser customUser) {
        UserDetail principal = UserDetail.of(UserResponse.builder()
                .email(customUser.username())
                .password(customUser.password())
                .provider(Provider.EMAIL)
                .subscribe(false)
                .marketingAgree(false)
                .profileImg("imageUrl")
                .status(Status.ACTIVE)
                .build());

        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        return context;
    }
}
