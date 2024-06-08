package com.floney.floney.book.repository.favorite;

import com.floney.floney.book.domain.category.entity.Category;
import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.book.domain.favorite.Favorite;
import com.floney.floney.common.constant.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import javax.persistence.LockModeType;
import java.util.List;

public interface FavoriteRepository extends JpaRepository<Favorite, Long>, FavoriteCustomRepository {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Favorite> findAllExclusivelyByBookAndLineCategoryAndStatus(final Book book, final Category lineCategory, final Status status);

    List<Favorite> findAllByBookAndLineCategoryAndStatusOrderByIdDesc(final Book book, final Category lineCategory, final Status status);
}
