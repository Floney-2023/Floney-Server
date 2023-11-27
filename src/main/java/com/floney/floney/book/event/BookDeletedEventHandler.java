package com.floney.floney.book.event;

import com.floney.floney.analyze.service.AssetService;
import com.floney.floney.analyze.service.CarryOverService;
import com.floney.floney.book.repository.category.BookLineCategoryRepository;
import com.floney.floney.settlement.service.SettlementService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookDeletedEventHandler {

    private final SettlementService settlementService;
    private final CarryOverService carryOverService;
    private final AssetService assetService;
    private final BookLineCategoryRepository bookLineCategoryRepository;


    @EventListener(BookDeletedEvent.class)
    public void deleteBook(final BookDeletedEvent event) {
        final long bookId = event.getBookId();
        settlementService.deleteAllBy(bookId);
    }

    @EventListener(BookLineDeletedEvent.class)
    public void deleteBookLine(final BookLineDeletedEvent event) {
        final long bookLineId = event.getBookLineId();
        carryOverService.deleteCarryOver(bookLineId);
        assetService.deleteAsset(bookLineId);

        bookLineCategoryRepository.inactiveAllByBookLineId(bookLineId);
    }

}
