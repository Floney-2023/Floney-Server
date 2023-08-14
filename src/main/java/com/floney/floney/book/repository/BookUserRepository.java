package com.floney.floney.book.repository;

import com.floney.floney.book.entity.Book;
import com.floney.floney.book.entity.BookUser;
import com.floney.floney.common.constant.Status;
import com.floney.floney.user.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookUserRepository extends JpaRepository<BookUser, Long>, BookUserCustomRepository {

    int countBookUserByUserAndStatus(User user, Status status);

    List<BookUser> findByUser(User user);

    List<BookUser> findAllByBookAndStatus(Book book, Status status);

    boolean existsByBookAndUser_EmailAndStatus(final Book book, final String user_email, final Status status);
}
