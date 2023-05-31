package com.floney.floney.book.repository;

import com.floney.floney.book.entity.BookUser;
import com.floney.floney.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookUserRepository  extends JpaRepository<BookUser, Long>, BookUserCustomRepository {
    int countBookUserByUserAndStatus(User user,Boolean status);

    Optional<BookUser> findByUser(User user);
}
