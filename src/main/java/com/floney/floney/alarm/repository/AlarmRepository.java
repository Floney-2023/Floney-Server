package com.floney.floney.alarm.repository;

import com.floney.floney.book.domain.entity.Alarm;
import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.book.domain.entity.BookUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlarmRepository extends JpaRepository<Alarm, Long>, AlarmCustomRepository {

    List<Alarm> findAllByBookAndBookUser(Book book, BookUser user);
}
