package com.floney.floney.book;

import com.floney.floney.book.entity.AssetCategory;
import com.floney.floney.book.repository.AssetCategoryRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AssetRepositoryTest {

    @Autowired
    private AssetCategoryRepository assetCategoryRepository;

    @Test
    @DisplayName("자산 종류를 저장한다")
    void new_asset() {
        AssetCategory newAsset = AssetCategory.builder()
            .asset("지출")
            .build();

        AssetCategory savedAsset = assetCategoryRepository.save(newAsset);
        Assertions.assertThat(savedAsset.getAsset()).isEqualTo(newAsset.getAsset());
    }
}
