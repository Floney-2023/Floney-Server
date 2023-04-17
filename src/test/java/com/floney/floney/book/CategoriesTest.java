package com.floney.floney.book;

import com.floney.floney.book.entity.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CategoriesTest {

    @Test
    @DisplayName("카테고리를 관리하는 wrapper 객체")
    void create() {

        FlowCategory flow = FlowCategory.builder()
            .flow("지출")
            .build();

        AssetCategory asset = AssetCategory.builder()
            .asset("신용카드")
            .build();

        LineCategory line = LineCategory.builder()
            .line("식비")
            .build();

        RepeatCategory repeat = RepeatCategory.builder()
            .kind("1주일")
            .build();

        Categories categories = Categories.to(line, asset, repeat, flow);
        Assertions.assertThat(categories.getAssetCategory().getAsset()).isEqualTo(asset.getAsset());
    }
}
