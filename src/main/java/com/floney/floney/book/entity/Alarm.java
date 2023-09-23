package com.floney.floney.book.entity;

import com.floney.floney.book.dto.request.SaveAlarmRequest;
import com.floney.floney.common.entity.BaseEntity;
import com.floney.floney.user.entity.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.time.LocalDate;

@Entity
@Getter
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Alarm extends BaseEntity {

    @ManyToOne
    private Book book;

    @OneToOne
    private User user;

    private String content;

    private String imgUrl;

    private LocalDate date;

    @Builder
    public Alarm(Book book, User user, String content, String imgUrl, LocalDate date) {
        this.book = book;
        this.user = user;
        this.content = content;
        this.imgUrl = imgUrl;
        this.date = date;
    }

    public static Alarm of(Book book, User user, SaveAlarmRequest request){
        return Alarm.builder()
            .user(user)
            .book(book)
            .imgUrl(request.getImgUrl())
            .content(request.getContent())
            .date(request.getDate())
            .build();
    }
}
