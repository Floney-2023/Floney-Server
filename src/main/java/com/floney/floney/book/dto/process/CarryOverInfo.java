package com.floney.floney.book.dto.process;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CarryOverInfo {
    private final boolean carryOverStatus;
    private final double carryOverMoney;
}
