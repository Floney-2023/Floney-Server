package com.floney.floney.book.repository;

import com.floney.floney.book.dto.process.MyBookInfo;
import com.floney.floney.book.dto.process.OurBookUser;
import com.floney.floney.book.entity.Book;
import com.floney.floney.book.entity.BookUser;
import com.floney.floney.user.entity.User;
import java.util.List;
import java.util.Optional;

public interface BookUserCustomRepository {

    int getCurrentJoinUserCount(Book book);

    List<OurBookUser> findAllUser(String bookKey);

    BookUser findBookUserByEmail(String userEmail, String bookKey);

    Optional<BookUser> findBookUserByKey(String auth, String bookKey);

    Optional<BookUser> findBookUserByCode(String currentUserEmail, String bookCode);

    List<MyBookInfo> findMyBookInfos(User user);

    List<Book> findMyInactiveBooks(User user);

    List<Book> findBookByOwner(User user);

    long countInBook(Book book);

    BookUser findBookUserBy(String email, Book targetBook);

    boolean existsByUserEmailAndBookKey(String email, String bookKey);

    Optional<User> findBookUserWhoSubscribe(Book targetBook);

    List<BookUser> findAllByUserId(Long userId);

    void inactiveAllByBook(Book book);
}
