package com.floney.floney.book.entity;

import com.floney.floney.book.dto.request.SaveAlarmRequest;
import com.floney.floney.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.time.LocalDateTime;

@Entity
@Getter
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Alarm extends BaseEntity {

    @ManyToOne
    private Book book;

    @OneToOne
    private BookUser bookUser;

    private String title;

    private String body;

    private String imgUrl;

    private LocalDateTime date;

    private Boolean isReceived;

    @Builder
    public Alarm(Book book, BookUser bookUser, String title, String body, String imgUrl, LocalDateTime date, Boolean isReceived) {
        this.book = book;
        this.bookUser = bookUser;
        this.title = title;
        this.body = body;
        this.imgUrl = imgUrl;
        this.date = date;
        this.isReceived = isReceived;
    }

    public static Alarm of(Book book, BookUser user, SaveAlarmRequest request) {
        return Alarm.builder()
            .bookUser(user)
            .book(book)
            .imgUrl(request.getImgUrl())
            .title(request.getTitle())
            .body(request.getBody())
            .date(request.getDate())
            .build();
    }

    public void updateReceived(boolean isReceived) {
        this.isReceived = isReceived;
    }
}
