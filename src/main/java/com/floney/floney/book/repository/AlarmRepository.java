package com.floney.floney.book.repository;

import com.floney.floney.book.entity.Alarm;
import com.floney.floney.book.entity.Book;
import com.floney.floney.book.entity.BookUser;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmRepository extends JpaRepository<Alarm, Long>, AlarmCustomRepository {

    List<Alarm> findAllByBookAndBookUser(Book book, BookUser user);
}
