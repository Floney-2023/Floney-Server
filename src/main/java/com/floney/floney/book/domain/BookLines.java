package com.floney.floney.book.domain;

import com.floney.floney.analyze.domain.BookLineSortingType;
import com.floney.floney.book.domain.entity.BookLine;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;
import java.util.List;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class BookLines {

    private final List<BookLine> bookLines;

    public static BookLines from(final List<BookLine> bookLines) {
        return new BookLines(bookLines);
    }

    public List<BookLine> sort(final BookLineSortingType sortingType) {
        if (sortingType == BookLineSortingType.LATEST) {
            return bookLines.stream().sorted((o1, o2) -> o2.getLineDate().compareTo(o1.getLineDate())).toList();
        }
        if (sortingType == BookLineSortingType.OLDEST) {
            return bookLines.stream().sorted(Comparator.comparing(BookLine::getLineDate)).toList();
        }
        if (sortingType == BookLineSortingType.USER_NICKNAME) {
            return bookLines.stream().sorted(Comparator.comparing(BookLine::getWriterNickName)).toList();
        }
        if (sortingType == BookLineSortingType.LINE_SUBCATEGORY_NAME) {
            return bookLines.stream().sorted(Comparator.comparing(BookLine::getDescription)).toList();
        }
        log.error("존재하지 않는 BookLineSortingType 입니다. sortingType: {}", sortingType);
        return bookLines;
    }
}
