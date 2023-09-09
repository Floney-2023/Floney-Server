package com.floney.floney.book.repository;

import com.floney.floney.book.dto.process.MyBookInfo;
import com.floney.floney.book.dto.process.OurBookUser;
import com.floney.floney.book.entity.Book;
import com.floney.floney.book.entity.BookUser;
import com.floney.floney.user.entity.User;

import java.util.List;
import java.util.Optional;

public interface BookUserCustomRepository {

    void isMax(Book book);

    List<OurBookUser> findAllUser(String bookKey);

    Optional<BookUser> findBookUserByKey(String auth, String bookKey);

    Optional<BookUser> findBookUserByCode(String currentUserEmail, String bookCode);

    List<MyBookInfo> findMyBookInfos(User user);

    List<Book> findMyBooks(User user);

    long countBookUser(Book book);

    BookUser findBookUserBy(String email, Book targetBook);

    List<BookUser> findBookUserHaveToDelete();

    Optional<User> findBookUserWhoSubscribe(Book targetBook);

    Optional<User> findAnyBookUser(Book targetBook);
}
