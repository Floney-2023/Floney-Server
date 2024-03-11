package com.floney.floney.analyze.service;

import com.floney.floney.book.domain.category.CategoryType;
import com.floney.floney.book.domain.entity.Asset;
import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.book.domain.entity.BookLine;
import com.floney.floney.book.dto.process.AssetInfo;
import com.floney.floney.book.repository.AssetJdbcRepository;
import com.floney.floney.book.repository.BookLineRepository;
import com.floney.floney.book.repository.analyze.AssetRepository;
import com.floney.floney.book.util.DateUtil;
import com.floney.floney.common.domain.vo.DateDuration;
import com.floney.floney.common.exception.book.NotFoundBookLineException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.floney.floney.common.constant.Status.ACTIVE;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AssetServiceImpl implements AssetService {

    public static final int SAVE_ASSET_DURATION = 60;
    private static final int SHOW_ASSET_DURATION = 5;
    private static final int ONE_MONTH = 1;

    private final AssetRepository assetRepository;
    private final AssetJdbcRepository assetJdbcRepository;
    private final BookLineRepository bookLineRepository;

    @Override
    public Map<LocalDate, AssetInfo> getAssetInfo(Book book, String date) {
        LocalDate localDate = LocalDate.parse(date);

        // 1. 현재 기간으로부터 asset_duration 전의 기간
        DateDuration datesDuration = DateDuration.beforeMonthToCurrent(localDate, SHOW_ASSET_DURATION);

        // 2. 기본 응답값 -> 초기 자산으로 셋팅
        Map<LocalDate, AssetInfo> initAssets = getInitAssetInfo(book, date);

        // 3. 1번에서 추출한 기간을 기준으로 저장된 데이터가 있다면 대체
        List<Asset> assets = assetRepository.findByDateBetweenAndBookAndStatus(datesDuration.getStartDate(), datesDuration.getEndDate(), book, ACTIVE);
        assets.forEach((asset) -> initAssets.replace(asset.getDate(), AssetInfo.of(asset, book)));

        return initAssets;
    }

    private Map<LocalDate, AssetInfo> getInitAssetInfo(Book book, String date) {
        LocalDate localDate = LocalDate.parse(date);
        Map<LocalDate, AssetInfo> initAssets = new LinkedHashMap<>();

        for (int i = 0; i <= SHOW_ASSET_DURATION; i++) {
            initAssets.put(localDate, AssetInfo.init(book.getAsset(), localDate));
            localDate = localDate.minusMonths(ONE_MONTH);
        }

        return initAssets;
    }

    @Override
    @Transactional
    public void addAssetOf(final BookLine bookLine) {
        CategoryType categoryType = bookLine.getCategories().getLineCategory().getName();

        if (!bookLine.isIncludedInAsset()) {
            return;
        }

        List<Asset> assets = new ArrayList<>();
        final LocalDate startMonth = DateUtil.getFirstDayOfMonth(bookLine.getLineDate());

        for (int month = 0; month < SAVE_ASSET_DURATION; month++) {
            final LocalDate currentMonth = startMonth.plusMonths(month);
            assets.add(Asset.of(bookLine, categoryType, currentMonth));
        }

        assetJdbcRepository.saveAll(assets);
    }


    @Override
    @Transactional
    public void subtractAssetOf(final Long bookLineId) {
        final BookLine bookLine = bookLineRepository.findByIdWithCategories(bookLineId)
            .orElseThrow(NotFoundBookLineException::new);

        if (!bookLine.isIncludedInAsset()) {
            return;
        }

        final LocalDate startMonth = DateUtil.getFirstDayOfMonth(bookLine.getLineDate());

        for (int month = 0; month < SAVE_ASSET_DURATION; month++) {
            final LocalDate currentMonth = startMonth.plusMonths(month);
            assetRepository.subtractMoneyByDateAndBook(getMoney(bookLine), currentMonth, bookLine.getBook());
        }
    }

    private double getMoney(final BookLine bookLine) {
        if (bookLine.getCategories().isOutcome()) {
            return (-1) * bookLine.getMoney();
        }
        return bookLine.getMoney();
    }
}

