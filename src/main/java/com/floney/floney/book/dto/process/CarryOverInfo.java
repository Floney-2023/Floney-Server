package com.floney.floney.book.dto.process;

import com.floney.floney.book.entity.Book;
import com.floney.floney.book.util.DateFactory;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class CarryOverInfo {
    private boolean carryOverStatus;
    private long carryOverMoney;

    private CarryOverInfo(boolean carryOverStatus) {
        this.carryOverStatus = carryOverStatus;
    }

    @Builder
    private CarryOverInfo(boolean carryOverStatus, long carryOverMoney) {
        this.carryOverStatus = carryOverStatus;
        this.carryOverMoney = carryOverMoney;
    }

    public static CarryOverInfo of(Book book) {
        if (book.isCarryOver()) {
            return new CarryOverInfo(book.isCarryOver(), book.getCarryOverMoney());
        } else {
            return new CarryOverInfo(book.isCarryOver());
        }

    }

    public static CarryOverInfo createIfFirstDay(Book book,String date) {
        if(DateFactory.isFirstDay(date) && book.isCarryOver()){
            return new CarryOverInfo(book.isCarryOver(), book.getCarryOverMoney());
        }
        else {
            return new CarryOverInfo(book.isCarryOver());
        }
    }
}
