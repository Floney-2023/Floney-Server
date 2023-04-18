package com.floney.floney.book.repository;

import com.floney.floney.book.entity.AssetCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AssetCategoryRepository extends JpaRepository<AssetCategory,Long> {
    Optional<AssetCategory> findAssetCategoryByAsset(String asset);
}
