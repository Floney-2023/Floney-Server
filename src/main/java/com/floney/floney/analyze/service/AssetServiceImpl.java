package com.floney.floney.analyze.service;

import com.floney.floney.book.dto.process.AssetInfo;
import com.floney.floney.book.dto.process.DatesDuration;
import com.floney.floney.book.dto.request.ChangeBookLineRequest;
import com.floney.floney.book.entity.Asset;
import com.floney.floney.book.entity.Book;
import com.floney.floney.book.entity.BookLine;
import com.floney.floney.book.repository.analyze.AssetRepository;
import com.floney.floney.book.util.DateFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

import static com.floney.floney.book.dto.constant.AssetType.BANK;
import static com.floney.floney.book.dto.constant.CategoryEnum.FLOW;

@RequiredArgsConstructor
@Component
public class AssetServiceImpl implements AssetService {
    private static final int FIVE_YEARS = 60;
    private final AssetRepository assetRepository;

    @Override
    @Transactional(readOnly = true)
    public Map<LocalDate, AssetInfo> getAssetInfo(Book book, String date) {
        LocalDate localDate = LocalDate.parse(date);
        DatesDuration datesDuration = DateFactory.getAssetDuration(localDate);
        // 기본 응답값 -> 초기 자산으로 셋팅
        Map<LocalDate, AssetInfo> initAssets = getInitAssetInfo(book, date);

        // 날짜를 ky로 하여, 저장된 데이터가 있다면 대체
        List<Asset> assets = assetRepository.findByDateBetweenAndBook(datesDuration.getStartDate(), datesDuration.getEndDate(), book);
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

    // 가계부 내역 수정시 asset 업데이트
    @Override
    @Transactional
    public void updateAsset(ChangeBookLineRequest request, BookLine savedBookLine) {
        deleteAsset(savedBookLine);
        createAssetBy(request, savedBookLine.getBook());
    }

    @Override
    @Transactional
    public void createAssetBy(ChangeBookLineRequest request, Book book) {
        LocalDate targetDate = DateFactory.getFirstDayOf(request.getLineDate());
        List<Asset> assets = new ArrayList<>();

        // 5년(60개월) 동안의 엔티티 생성
        for (int i = 0; i < FIVE_YEARS; i++) {
            Optional<Asset> savedAsset = assetRepository.findAssetByDateAndBook(targetDate, book);

            if (savedAsset.isEmpty() && !Objects.equals(request.getFlow(), BANK.name())) {
                Asset newAsset = Asset.of(request, book, targetDate);
                assets.add(newAsset);
            } else {
                savedAsset.ifPresent(asset -> {
                    asset.update(request.getMoney(), request.getFlow());
                    assets.add(asset);
                });
            }
            targetDate = targetDate.plusMonths(1);
        }

        assetRepository.saveAll(assets);
    }

    @Override
    @Transactional
    public void deleteAsset(BookLine savedBookLine) {
        LocalDate targetDate = DateFactory.getFirstDayOf(savedBookLine.getLineDate());
        List<Asset> assets = new ArrayList<>();

        // 5년(60개월) 동안의 엔티티 생성
        for (int i = 0; i < FIVE_YEARS; i++) {
            Optional<Asset> savedAsset = assetRepository.findAssetByDateAndBook(targetDate, savedBookLine.getBook());
            savedAsset.ifPresent(asset -> {
                asset.delete(savedBookLine.getMoney(), savedBookLine.getBookLineCategories().get(FLOW));
                assets.add(asset);
            });
            targetDate = targetDate.plusMonths(1);
        }

        assetRepository.saveAll(assets);
    }
}
