package com.floney.floney.book.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SeeProfileRequest {
    private String bookKey;
    private boolean seeProfileStatus;

    public SeeProfileRequest(String bookKey, boolean seeProfileStatus) {
        this.bookKey = bookKey;
        this.seeProfileStatus = seeProfileStatus;
    }
}
