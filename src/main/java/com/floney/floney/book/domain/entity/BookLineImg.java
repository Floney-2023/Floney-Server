package com.floney.floney.book.domain.entity;

import com.floney.floney.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

@Entity
@Getter
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookLineImg extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private BookLine bookLine;

    @ManyToOne(fetch = FetchType.LAZY)
    private RepeatBookLine repeatBookLine;

    private String imgUrl;

    public BookLineImg(BookLine bookLine, String imgUrl){
        this.bookLine = bookLine;
        this.imgUrl = imgUrl;
        this.repeatBookLine = null;
    }

    public BookLineImg(RepeatBookLine repeatBookLine, String imgUrl){
        this.bookLine = null;
        this.repeatBookLine = repeatBookLine;
        this.imgUrl = imgUrl;
    }
}
