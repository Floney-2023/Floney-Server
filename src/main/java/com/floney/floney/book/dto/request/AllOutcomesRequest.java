package com.floney.floney.book.dto.request;

import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
public class AllOutcomesRequest {

    private String bookKey;

    private List<String> usersEmails;

    @NotNull
    private DatesDuration duration;

    public AllOutcomesRequest(String bookKey, List<String> usersEmails, DatesDuration duration) {
        this.bookKey = bookKey;
        this.usersEmails = usersEmails;
        this.duration = duration;
    }

    public List<String> users() {
        return usersEmails;
    }
}
