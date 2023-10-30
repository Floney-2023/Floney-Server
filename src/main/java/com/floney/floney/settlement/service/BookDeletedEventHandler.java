package com.floney.floney.settlement.service;

import com.floney.floney.book.event.BookDeletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookDeletedEventHandler {

    private final SettlementService settlementService;

    @EventListener(BookDeletedEvent.class)
    public void handle(final BookDeletedEvent event) {
        final long bookId = event.getBookId();
        settlementService.deleteAllBy(bookId);
    }
}
