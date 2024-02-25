package com.floney.floney.book.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class CarryOverRequest {
    private boolean status;
    private String bookKey;
}
