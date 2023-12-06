package com.floney.floney.analyze.service;

import com.floney.floney.book.domain.entity.Asset;
import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.book.domain.entity.BookLine;
import com.floney.floney.book.dto.process.AssetInfo;
import com.floney.floney.book.dto.process.DatesDuration;
import com.floney.floney.book.dto.request.BookLineRequest;
import com.floney.floney.book.repository.BookLineRepository;
import com.floney.floney.book.repository.analyze.AssetRepository;
import com.floney.floney.book.util.DateFactory;
import com.floney.floney.common.exception.book.NotFoundBookLineException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.floney.floney.book.dto.constant.AssetType.BANK;
import static com.floney.floney.book.dto.constant.CategoryEnum.FLOW;
import static com.floney.floney.common.constant.Status.ACTIVE;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AssetServiceImpl implements AssetService {

    private static final int SAVED_MONTHS = 5 * 12; // 자산 데이터 생성 기간

    private final AssetRepository assetRepository;
    private final BookLineRepository bookLineRepository;

    @Override
    public Map<LocalDate, AssetInfo> getAssetInfo(Book book, String date) {
        LocalDate localDate = LocalDate.parse(date);
        DatesDuration datesDuration = DateFactory.getAssetDuration(localDate);
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

        for (int i = 0; i < 6; i++) {
            initAssets.put(localDate, AssetInfo.init(book.getAsset(), localDate));
            localDate = localDate.minusMonths(1);
        }

        return initAssets;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createAssetBy(final BookLineRequest request, final Book book) {
        // 이체 내역일 경우 자산 포함 X
        if (BANK.getKind().equals(request.getFlow())) {
            return;
        }

        final LocalDate startMonth = DateFactory.getFirstDayOf(request.getLineDate());

        for (int month = 0; month < SAVED_MONTHS; month++) {
            final LocalDate currentMonth = startMonth.plusMonths(month);

            final Optional<Asset> savedAsset = findAssetByDateAndBook(book, currentMonth);
            if (savedAsset.isEmpty()) {
                final Asset newAsset = Asset.of(request, book, currentMonth);
                assetRepository.save(newAsset);
            } else {
                final Asset asset = savedAsset.get();
                asset.update(request.getMoney(), request.getFlow());
            }
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteAsset(final Long bookLineId) {
        final BookLine bookLine = bookLineRepository.findById(bookLineId)
                .orElseThrow(NotFoundBookLineException::new);

        final LocalDate startMonth = DateFactory.getFirstDayOf(bookLine.getLineDate());

        for (int month = 0; month < SAVED_MONTHS; month++) {
            final LocalDate currentMonth = startMonth.plusMonths(month);

            findAssetByDateAndBook(bookLine.getBook(), currentMonth).ifPresent(asset ->
                    asset.delete(bookLine.getMoney(), bookLine.getBookLineCategories().get(FLOW))
            );
        }
    }

    private Optional<Asset> findAssetByDateAndBook(final Book book, final LocalDate targetDate) {
        return assetRepository.findAssetExclusivelyByDateAndBookAndStatus(targetDate, book, ACTIVE);
    }
}

