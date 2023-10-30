package com.floney.floney.book.repository;

import com.floney.floney.book.entity.Book;
import com.floney.floney.book.entity.BookUser;
import com.floney.floney.common.constant.Status;
import com.floney.floney.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookUserRepository extends JpaRepository<BookUser, Long>, BookUserCustomRepository {

    int countBookUserByUserAndStatus(User user, Status status);

    List<BookUser> findByUserAndStatus(User user, Status status);

    List<BookUser> findAllByBookAndStatus(Book book, Status status);
}
