package com.floney.floney.common.constant;

import lombok.Getter;

@Getter
public enum Status {

    ACTIVE(true),
    INACTIVE(false);

    private boolean status;

    Status(boolean status) {
        this.status = status;
    }
}
