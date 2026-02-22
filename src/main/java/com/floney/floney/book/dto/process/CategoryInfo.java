package com.floney.floney.book.dto.process;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Category information DTO for client communication.
 *
 * <p><b>Usage Guidelines for Clients:</b></p>
 * <ul>
 *   <li>If {@code isDefault = true}: Use {@code categoryKey} for subsequent API requests (e.g., "Food", "Transportation")</li>
 *   <li>If {@code isDefault = false}: Use {@code name} for subsequent API requests (user-defined categories)</li>
 * </ul>
 *
 * <p><b>Example 1 - Default Category:</b></p>
 * <pre>
 * Response: { "isDefault": true, "name": "식비", "categoryKey": "Food" }
 * Usage: When creating booklines, use "Food" as the category identifier
 * </pre>
 *
 * <p><b>Example 2 - User-Defined Category:</b></p>
 * <pre>
 * Response: { "isDefault": false, "name": "내맘대로카테고리", "categoryKey": null }
 * Usage: When creating booklines, use "내맘대로카테고리" as the category identifier
 * </pre>
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CategoryInfo {

    /**
     * Indicates whether this is a default (system-provided) category.
     * Default categories must be accessed via {@code categoryKey} in API requests.
     */
    private boolean isDefault;

    /**
     * The display name of the category (localized for the current language).
     * For user-defined categories, this is also the identifier to use in API requests.
     */
    private String name;

    /**
     * The immutable key for default categories (e.g., "Food", "Transportation").
     * This is {@code null} for user-defined categories.
     * For default categories, this is the required identifier for API requests.
     */
    private String categoryKey;

    @Builder
    @QueryProjection
    public CategoryInfo(final boolean isDefault, final String name, final String categoryKey) {
        this.isDefault = isDefault;
        this.name = name;
        this.categoryKey = categoryKey;
    }
}
