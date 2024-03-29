package com.floney.floney.book.domain.entity;

import com.floney.floney.common.entity.BaseEntity;
import com.floney.floney.user.entity.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Getter
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookUser extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

    @Column(length = 300)
    private String profileImg;

    @Builder
    private BookUser(User user, Book book, String profileImg) {
        this.user = user;
        this.book = book;
        this.profileImg = profileImg;
    }

    public static BookUser of(User user, Book book) {
        return BookUser.builder()
                .user(user)
                .book(book)
                .profileImg(user.getProfileImg())
                .build();
    }

    public String getNickName() {
        return this.user.getNickname();
    }

    public void updateProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }

    public boolean isOwner() {
        return book.isOwner(user.getEmail());
    }
}
