package com.floney.floney.book.dto.request;

import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
public class AllOutcomesRequest {

    private String bookKey;

    private List<String> usersEmails;

    @NotNull
    private DatesRequest dates;

    public AllOutcomesRequest(String bookKey, List<String> usersEmails, DatesRequest dates) {
        this.bookKey = bookKey;
        this.usersEmails = usersEmails;
        this.dates = dates;
    }

    public List<String> users() {
        return usersEmails;
    }

}
