package com.floney.floney.book.service;

import com.floney.floney.user.event.UserSignedOutEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserSignedOutEventHandler {

    private final BookService bookService;

    @EventListener(UserSignedOutEvent.class)
    public void handle(final UserSignedOutEvent event) {
        final long userId = event.getUserId();
        bookService.inActiveOrDelegateOwnedBooks(userId);
        bookService.leaveBooksBy(userId);
    }
}
