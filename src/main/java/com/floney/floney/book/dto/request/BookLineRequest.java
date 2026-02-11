package com.floney.floney.book.dto.request;

import com.floney.floney.book.domain.RepeatDuration;
import com.floney.floney.book.domain.category.CategoryType;
import com.floney.floney.book.domain.entity.BookLine;
import com.floney.floney.book.domain.entity.BookLineCategory;
import com.floney.floney.book.domain.entity.BookLineImg;
import com.floney.floney.book.domain.entity.BookUser;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Request DTO for creating or updating booklines (내역).
 *
 * <p><b>Important: Category Field Usage</b></p>
 * <ul>
 *   <li><b>Default categories:</b> Use {@code categoryKey} (e.g., "Food", "Transportation")</li>
 *   <li><b>User-defined categories:</b> Use the category {@code name}</li>
 * </ul>
 *
 * <p>To determine if a category is default, check the {@code isDefault} field
 * from the category list API response (GET /books/{key}/categories).</p>
 */
@Getter
@ToString
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookLineRequest {

    private long lineId;
    private String bookKey;
    private double money;
    private CategoryType lineType;

    /**
     * Asset subcategory identifier.
     * <ul>
     *   <li>For default categories: Use categoryKey (e.g., "Cash", "CreditCard")</li>
     *   <li>For user-defined categories: Use the category name</li>
     * </ul>
     */
    private String asset;

    /**
     * Line subcategory identifier (also known as expense/income subcategory).
     * <ul>
     *   <li>For default categories: Use categoryKey (e.g., "Food", "Transportation", "Salary")</li>
     *   <li>For user-defined categories: Use the category name</li>
     * </ul>
     * Example: "Food" for the default food category, "내맘대로카테고리" for a user-defined category
     */
    private String line; // TODO: 차후에 변수명 subType으로 변경

    private LocalDate lineDate;
    private String description;
    private Boolean except;
    private RepeatDuration repeatDuration;
    private String memo;
    private List<String> imageUrl;

    public BookLine to(final BookUser bookUser, final BookLineCategory bookLineCategory) {
        return BookLine.builder()
            .book(bookUser.getBook())
            .writer(bookUser)
            .lineDate(lineDate)
            .money(money)
            .description(description)
            .exceptStatus(except)
            .categories(bookLineCategory)
            .memo(memo)
            .build();
    }
}
