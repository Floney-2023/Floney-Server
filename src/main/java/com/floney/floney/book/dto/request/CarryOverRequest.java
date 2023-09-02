package com.floney.floney.book.dto.request;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CarryOverRequest {
    private boolean status;
    private String bookKey;
}
