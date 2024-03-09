package com.floney.floney.book.service;

import com.floney.floney.analyze.service.AssetServiceImpl;
import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.book.domain.entity.BookLine;
import com.floney.floney.book.domain.entity.BookLineCategory;
import com.floney.floney.book.domain.entity.BookUser;
import com.floney.floney.book.repository.AssetJdbcRepository;
import com.floney.floney.book.repository.analyze.AssetRepository;
import com.floney.floney.fixture.*;
import com.floney.floney.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("단위 테스트 : AssetService")
@ExtendWith(MockitoExtension.class)
public class AssetServiceTest {

    @InjectMocks
    private AssetServiceImpl assetService;

    @Mock
    private AssetRepository assetRepository;

    @Mock
    private AssetJdbcRepository assetJdbcRepository;

    @Nested
    @DisplayName("addAssetOf()를 실행할 때")
    class Describe_AddAssetOf {
        User user;
        Book book;
        String bookKey;
        BookUser bookUser;

        BookLine bookLine;
        String assetSubCategoryName = "자산";

        @BeforeEach()
        void init() {
            user = UserFixture.emailUser();
            book = BookFixture.createBook();
            bookKey = book.getBookKey();
            bookUser = BookUserFixture.createBookUser(book, user);
        }

        @Nested
        @DisplayName("카테고리가 수입인 가계부 내역이 주어지면")
        class Context_With_IncomeBookLine {
            @BeforeEach
            void init() {
                String incomeSubCategoryName = "급여";
                final BookLineCategory bookLineCategory = BookLineCategoryFixture.incomeBookLineCategory(book, incomeSubCategoryName, assetSubCategoryName);
                bookLine = BookLineFixture.createWithDate(book, bookUser, bookLineCategory, LocalDate.now());
            }

            @Test
            @DisplayName("자산이 업데이트 된다")
            void it_update_asset() {
                assetService.addAssetOf(bookLine);
                verify(assetJdbcRepository, times(1)).saveAll(any(List.class));
            }
        }

        @Nested
        @DisplayName("카테고리가 지출인 가계부 내역이 주어지면")
        class Context_With_OutcomeBookLine {
            @BeforeEach
            void init() {
                String outcomeSubCategoryName = "식비";
                final BookLineCategory bookLineCategory = BookLineCategoryFixture.outcomeBookLineCategory(book, outcomeSubCategoryName, assetSubCategoryName);
                bookLine = BookLineFixture.createWithDate(book, bookUser, bookLineCategory, LocalDate.now());
            }

            @Test
            @DisplayName("자산이 업데이트 된다")
            void it_update_asset() {
                assetService.addAssetOf(bookLine);
                verify(assetJdbcRepository, times(1)).saveAll(any(List.class));
            }
        }

        @Nested
        @DisplayName("카테고리가 이체인 가계부 내역이 주어지면")
        class Context_With_TransferBookLine {
            @BeforeEach
            void init() {
                String transferSubCategoryName = "이체";
                final BookLineCategory bookLineCategory = BookLineCategoryFixture.transferBookLineCategory(book, transferSubCategoryName, assetSubCategoryName);
                bookLine = BookLineFixture.createWithDate(book, bookUser, bookLineCategory, LocalDate.now());
            }

            @Test
            @DisplayName("자산이 업데이트 되지 않는다")
            void it_didnt_update_asset() {
                assetService.addAssetOf(bookLine);
                verify(assetJdbcRepository, never()).saveAll(any(List.class));
            }
        }
    }
}
