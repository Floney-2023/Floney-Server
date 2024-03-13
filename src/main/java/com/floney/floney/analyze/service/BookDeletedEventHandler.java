package com.floney.floney.analyze.service;

import com.floney.floney.book.event.BookDeletedEvent;
import com.floney.floney.book.event.BookLineDeletedEvent;
import com.floney.floney.book.service.category.CategoryService;
import com.floney.floney.settlement.service.SettlementService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookDeletedEventHandler {

    private final SettlementService settlementService;
    private final CategoryService categoryService;

    @EventListener(BookDeletedEvent.class)
    public void deleteBook(final BookDeletedEvent event) {
        final long bookId = event.getBookId();
        settlementService.deleteAllBy(bookId);
    }

    @EventListener(BookLineDeletedEvent.class)
    public void deleteBookLine(final BookLineDeletedEvent event) {
        final long bookLineId = event.getBookLineId();
        categoryService.deleteAllBookLineCategory(bookLineId);
    }

}
