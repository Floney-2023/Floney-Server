package com.floney.floney.book.repository;

import com.floney.floney.book.entity.BookAnalyze;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

import static com.floney.floney.book.entity.QBook.book;
import static com.floney.floney.book.entity.QBookAnalyze.bookAnalyze;

@Repository
@RequiredArgsConstructor
public class BookAnalyzeRepositoryImpl implements BookAnalyzeCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public BookAnalyze findAnalyze(String bookKey, LocalDate date){
        return jpaQueryFactory.selectFrom(bookAnalyze)
            .innerJoin(bookAnalyze.book,book)
            .on(book.bookKey.eq(bookKey))
            .where(bookAnalyze.analyzeDate.eq(date))
            .fetchOne();
    }
}
