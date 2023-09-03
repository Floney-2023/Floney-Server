package com.floney.floney.common;

import com.floney.floney.book.entity.BookLine;
import com.floney.floney.book.entity.BookLineCategory;
import com.floney.floney.book.repository.BookLineRepository;
import com.floney.floney.book.repository.category.BookLineCategoryRepository;
import com.floney.floney.settlement.domain.entity.Settlement;
import com.floney.floney.settlement.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeleteService {

    private final BookLineRepository bookLineRepository;
    private final BookLineCategoryRepository bookLineCategoryRepository;
    private final SettlementRepository settlementRepository;

    @Scheduled(cron = "* 3 * * * *") // 매일 오전 3시 삭제
    @Transactional
    public void deleteOldData() {
        //카테고리 삭제
        bookLineRepository.findCategoryHaveToDelete().stream()
            .map(BookLineCategory::deleteForever)
            .forEach(bookLineCategoryRepository::delete);

        //가계부 내역 삭제
        bookLineRepository.findLineHaveToDelete().stream()
            .map(BookLine::deleteForever)
            .forEach(bookLineRepository::delete);

        //정산 삭제
        bookLineRepository.findSettlementHaveToDelete().stream()
            .map(Settlement::deleteForever)
            .forEach(settlementRepository::delete);

        //TODO : 이월 내역 삭제

    }

}
