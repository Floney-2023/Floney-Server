package com.floney.floney.book.repository;

import com.floney.floney.book.entity.Alarm;
import com.floney.floney.book.entity.Book;
import com.floney.floney.book.entity.BookUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlarmRepository extends JpaRepository<Alarm, Long>, AlarmCustomRepository {

    List<Alarm> findAllByBookAndBookUser(Book book, BookUser user);
}
