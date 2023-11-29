package com.floney.floney.book.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BookDeletedEvent {

    private final long bookId;
}
