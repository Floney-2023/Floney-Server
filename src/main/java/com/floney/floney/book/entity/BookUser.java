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
import java.time.LocalDateTime;

@Entity
@Getter
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookUser extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

    private Boolean status;

    @Builder
    private BookUser(Long id, LocalDateTime createdAt, LocalDateTime updatedAt, User user, Book book, Boolean status) {
        super(id, createdAt, updatedAt);
        this.user = user;
        this.book = book;
        this.status = status;
    }

    public static BookUser of(User user, Book book) {
        return BookUser.builder()
            .user(user)
            .book(book)
            .build();
    }
}
