package com.floney.floney.book.dto.process;

import com.floney.floney.book.entity.CarryOver;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CarryOverInfo {
    private final boolean carryOverStatus;
    private final long carryOverMoney;

    public static CarryOverInfo of(boolean status, CarryOver carryOver) {
        return new CarryOverInfo(status, carryOver.getMoney());
    }
}
