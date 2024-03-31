package com.floney.floney.favorite.entity;

import com.floney.floney.book.domain.entity.BookLine;
import com.floney.floney.common.constant.Status;
import com.floney.floney.common.entity.BaseEntity;
import com.floney.floney.user.entity.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Favorite extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private User user;

    @ManyToOne
    private BookLine bookLine;

    private Favorite(User user, BookLine bookLine) {
        this.user = user;
        this.bookLine = bookLine;
    }

    public static Favorite of(User user, BookLine bookLine) {
        return new Favorite(user, bookLine);
    }

    public void activate() {
        this.status = Status.ACTIVE;
    }
}
