package com.floney.floney.book.repository;

import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.book.domain.entity.BookUser;
import com.floney.floney.book.dto.process.MyBookInfo;
import com.floney.floney.book.dto.process.OurBookUser;
import com.floney.floney.user.entity.User;

import java.util.List;
import java.util.Optional;

public interface BookUserCustomRepository {

    List<OurBookUser> findAllUser(String bookKey);

    Optional<String> findOldestBookUserEmailExceptOwner(User user, Book book);

    Optional<BookUser> findBookUserByEmailAndBookKey(String userEmail, String bookKey);

    boolean existsByBookKeyAndUserEmail(String bookKey, String userEmail);

    Optional<BookUser> findBookUserByCode(String userEmail, String bookCode);

    List<MyBookInfo> findMyBookInfos(User user);

    int countByBook(Book book);

    int countByBookExclusively(Book book);
    
    List<BookUser> findAllByUserId(Long userId);

    void inactiveAllByBook(Book book);
}
