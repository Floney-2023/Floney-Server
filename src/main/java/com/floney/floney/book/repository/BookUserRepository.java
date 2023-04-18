package com.floney.floney.book.repository;

import com.floney.floney.book.entity.BookUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookUserRepository  extends JpaRepository<BookUser, Long>, BookUserCustomRepository {
}
