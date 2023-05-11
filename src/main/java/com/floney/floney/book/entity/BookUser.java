package com.floney.floney.book.entity;

import com.floney.floney.common.BaseEntity;
import com.floney.floney.user.entity.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Getter
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
public class BookUser extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

    @Builder
    private BookUser(User user, Book book) {
        this.user = user;
        this.book = book;
    }

    public static BookUser of(User user, Book book) {
        return BookUser.builder()
            .user(user)
            .book(book)
            .build();
    }

    public String getNickName() {
        return this.user.getNickname();
    }
}
