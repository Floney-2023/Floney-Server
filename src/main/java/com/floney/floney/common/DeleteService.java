package com.floney.floney.common;

import com.floney.floney.book.entity.BookLine;
import com.floney.floney.book.entity.BookLineCategory;
import com.floney.floney.book.entity.BookUser;
import com.floney.floney.book.entity.CarryOver;
import com.floney.floney.book.repository.BookLineRepository;
import com.floney.floney.book.repository.BookUserRepository;
import com.floney.floney.book.repository.CarryOverCustomRepository;
import com.floney.floney.book.repository.CarryOverRepository;
import com.floney.floney.book.repository.category.BookLineCategoryRepository;
import com.floney.floney.settlement.domain.entity.Settlement;
import com.floney.floney.settlement.repository.SettlementRepository;
import com.floney.floney.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeleteService {
    private final CarryOverRepository carryOverRepository;
    private final UserRepository userRepository;

    private final BookUserRepository bookUserRepository;
    private final BookLineRepository bookLineRepository;
    private final BookLineCategoryRepository bookLineCategoryRepository;
    private final SettlementRepository settlementRepository;

    @Scheduled(cron = "0 3 * * * *") // 매일 오전 3시 삭제
    @Transactional
    public void deleteOldData() {
        //카테고리 삭제
        List<BookLineCategory> bookLineCategories = bookLineRepository.findCategoryHaveToDelete().stream()
            .map(BookLineCategory::deleteForever)
            .toList();
        bookLineCategoryRepository.saveAll(bookLineCategories);

        //가계부 내역 삭제
        List<BookLine> bookLines = bookLineRepository.findLineHaveToDelete().stream()
            .map(BookLine::deleteForever)
            .toList();
        bookLineRepository.saveAll(bookLines);

        //정산 삭제
        List<Settlement> settlements = settlementRepository.findSettlementHaveToDelete().stream()
            .map(Settlement::deleteForever)
            .toList();
        settlementRepository.saveAll(settlements);

        //가계부 유저 삭제
        List<BookUser> bookUsers = bookUserRepository.findBookUserHaveToDelete()
            .stream()
            .map(BookUser::deleteForever)
            .toList();
        bookUserRepository.saveAll(bookUsers);

        //이월 삭제
        List<CarryOver> carryOvers = carryOverRepository.findCarryOverHaveToDelete()
            .stream()
            .map(CarryOver::deleteForever)
            .toList();
        carryOverRepository.saveAll(carryOvers);

        //분석 삭제


        //예산 삭제

        //자산 삭제
    }

    @Scheduled(cron = "0 5 * * * *") // 매일 오전 5시 유저 삭제
    @Transactional
    public void deleteUser() {
        userRepository.deleteUserAfterMonth();
    }

}
