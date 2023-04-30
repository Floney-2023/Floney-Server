package com.floney.floney.book;

import com.floney.floney.book.entity.AssetCategory;
import com.floney.floney.book.repository.AssetCategoryRepository;
import com.floney.floney.config.TestConfig;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestConfig.class)
public class AssetRepositoryTest {

    @Autowired
    private AssetCategoryRepository assetCategoryRepository;

    @Test
    @DisplayName("자산 종류를 저장하고 조회한다")
    void new_asset() {
        String testAsset = "현금";

        AssetCategory newAsset = AssetCategory.builder()
            .asset(testAsset)
            .build();

        assetCategoryRepository.save(newAsset);
        Optional<AssetCategory> savedAsset = assetCategoryRepository.findAssetCategoryByAsset(testAsset);

        Assertions.assertThat(savedAsset.get().getAsset())
            .isEqualTo(testAsset);
    }
}
