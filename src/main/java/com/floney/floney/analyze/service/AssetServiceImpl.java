package com.floney.floney.analyze.service;

import com.floney.floney.book.domain.entity.Asset;
import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.book.domain.entity.BookLine;
import com.floney.floney.book.dto.process.AssetInfo;
import com.floney.floney.book.dto.request.BookLineRequest;
import com.floney.floney.book.repository.BookLineRepository;
import com.floney.floney.book.repository.analyze.AssetRepository;
import com.floney.floney.common.domain.vo.DateDuration;
import com.floney.floney.common.exception.book.NotFoundBookLineException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.floney.floney.book.dto.constant.AssetType.BANK;
import static com.floney.floney.book.dto.constant.AssetType.OUTCOME;
import static com.floney.floney.book.dto.constant.CategoryEnum.FLOW;
import static com.floney.floney.book.dto.constant.DateType.*;
import static com.floney.floney.common.constant.Status.ACTIVE;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AssetServiceImpl implements AssetService {
    private final AssetRepository assetRepository;
    private final BookLineRepository bookLineRepository;

    @Override
    public Map<LocalDate, AssetInfo> getAssetInfo(Book book, String date) {
        LocalDate localDate = LocalDate.parse(date);
        DateDuration datesDuration = DateDuration.getAssetDuration(localDate);
        // 기본 응답값 -> 초기 자산으로 셋팅
        Map<LocalDate, AssetInfo> initAssets = getInitAssetInfo(book, date);

        // 날짜를 key로 하여, 저장된 데이터가 있다면 대체
        List<Asset> assets = assetRepository.findByDateBetweenAndBookAndStatus(datesDuration.getStartDate(), datesDuration.getEndDate(), book, ACTIVE);
        assets.forEach((asset) -> initAssets.replace(asset.getDate(), AssetInfo.of(asset, book)));

        return initAssets;
    }

    private Map<LocalDate, AssetInfo> getInitAssetInfo(Book book, String date) {
        LocalDate localDate = LocalDate.parse(date);
        Map<LocalDate, AssetInfo> initAssets = new LinkedHashMap<>();

        for (int i = 0; i <= FIVE_YEAR.getValue(); i++) {
            initAssets.put(localDate, AssetInfo.init(book.getAsset(), localDate));
            localDate = localDate.minusMonths(ONE_MONTH.getValue());
        }

        return initAssets;
    }

    @Override
    @Transactional
    public void addAssetOf(final BookLineRequest request, final Book book) {
        // 이체 내역일 경우 자산 포함 X
        // TODO: 파라미터에 BookLineRequest을 BookLine으로 대체한 후 검증 로직 추가
        if (BANK.getKind().equals(request.getFlow())) {
            return;
        }

        final LocalDate startMonth = DateDuration.getFirstDayOfMonth(request.getLineDate());

        for (int month = 0; month < FIVE_YEAR_TO_DAY.getValue(); month++) {
            final LocalDate currentMonth = startMonth.plusMonths(month);
            assetRepository.upsertMoneyByDateAndBook(currentMonth, book, getMoney(request));
        }
    }

    @Override
    @Transactional
    public void subtractAssetOf(final Long bookLineId) {
        final BookLine bookLine = bookLineRepository.findByIdWithCategories(bookLineId)
            .orElseThrow(NotFoundBookLineException::new);
        if (!bookLine.includedInAsset()) {
            return;
        }

        final LocalDate startMonth = DateDuration.getFirstDayOfMonth(bookLine.getLineDate());

        for (int month = 0; month < FIVE_YEAR_TO_DAY.getValue(); month++) {
            final LocalDate currentMonth = startMonth.plusMonths(month);
            assetRepository.subtractMoneyByDateAndBook(getMoney(bookLine), currentMonth, bookLine.getBook());
        }
    }

    private double getMoney(final BookLineRequest request) {
        if (OUTCOME.getKind().equals(request.getFlow())) {
            return (-1) * request.getMoney();
        }
        return request.getMoney();
    }

    private double getMoney(final BookLine bookLine) {
        if (bookLine.getTargetCategory(FLOW).equals(OUTCOME.getKind())) {
            return (-1) * bookLine.getMoney();
        }
        return bookLine.getMoney();
    }
}

