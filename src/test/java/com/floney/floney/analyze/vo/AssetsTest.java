package com.floney.floney.analyze.vo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.YearMonth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("단위 테스트: Assets")
class AssetsTest {

    @Nested
    @DisplayName("create()를 실행할 때")
    class Describe_Create {

        @Nested
        @DisplayName("초기 자산과 현재 달을 전달하면")
        class Context_With_InitialAssetAndCurrentMonth {

            double initialAsset = 1000.0;
            YearMonth endMonth = YearMonth.now();

            @Test
            @DisplayName("Assets 객체를 생성한다.")
            void it_returns_Assets() {
                final Assets assets = Assets.create(initialAsset, endMonth);

                assertThat(assets.getValues())
                    .hasSize(Assets.MONTHS)
                    .containsValue(initialAsset);
            }
        }
    }

    @Nested
    @DisplayName("update()를 실행할 때")
    class Describe_Update {

        @Nested
        @DisplayName("범위 내의 달과 자산을 전달하면")
        class Context_With_ValidMonthAndAsset {

            Assets assets;

            @BeforeEach
            void init() {
                assets = Assets.create(1000.0, YearMonth.of(2000, 6));
            }

            @Test
            @DisplayName("해당 달의 자산을 업데이트한다.")
            void it_updates_asset() {
                assets.update(YearMonth.of(2000, 1), 500.0);

                assertThat(assets.getValues())
                    .containsEntry(YearMonth.of(2000, 1), 1500.0);
            }
        }

        @Nested
        @DisplayName("범위 바깥의 달을 전달하면")
        class Context_With_InvalidMonth {

            Assets assets;

            @BeforeEach
            void init() {
                assets = Assets.create(1000.0, YearMonth.of(2000, 6));
            }

            @Test
            @DisplayName("예외를 던진다.")
            void it_throws_exception() {
                assertThatThrownBy(() -> assets.update(YearMonth.of(2000, 7), 500.0))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Assets 에서 잘못된 달(2000-07)의 자산을 가져옴");
            }
        }
    }
}