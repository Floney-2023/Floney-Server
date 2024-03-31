package com.floney.floney.favorite.repository;

import com.floney.floney.book.domain.category.entity.Category;
import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.book.domain.entity.BookLine;
import com.floney.floney.book.domain.entity.BookUser;
import com.floney.floney.favorite.entity.Favorite;
import com.floney.floney.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

  List<Favorite> findAllByUser(User user);

  @Query("SELECT f.bookLine FROM Favorite f " +
      "WHERE f.user = :user AND f.bookLine.categories.lineCategory = :flow " +
      "AND f.status = com.floney.floney.common.constant.Status.ACTIVE " +
      "AND f.bookLine.status = com.floney.floney.common.constant.Status.ACTIVE " +
      "ORDER BY f.createdAt DESC")
  List<BookLine> findAllBookLinesByUserAndFlow(User user, Category flow);

  Optional<Favorite> findByUserAndBookLine(User user, BookLine bookLine);

  @Modifying
  @Query("UPDATE Favorite f SET f.status = com.floney.floney.common.constant.Status.INACTIVE " +
      "WHERE f.bookLine = :bookLine")
  void inactivateByBookLine(BookLine bookLine);

  @Modifying
  @Query("UPDATE Favorite f SET f.status = com.floney.floney.common.constant.Status.INACTIVE " +
      "WHERE f.bookLine IN :bookLines")
  void inactivateByBookLine(List<BookLine> bookLines);

  @Modifying
  @Query("UPDATE Favorite f SET f.status = com.floney.floney.common.constant.Status.INACTIVE " +
      "WHERE f.bookLine.book = :book")
  void inactivateAllByBook(Book book);

  @Modifying
  @Query("UPDATE Favorite f SET f.status = com.floney.floney.common.constant.Status.INACTIVE " +
      "WHERE f.bookLine.book.owner = :bookUser")
  void inactivateAllByBookUser(BookUser bookUser);
}
