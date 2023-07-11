package com.floney.floney.book.dto;

import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
public class AllOutcomesReqeust {
    private List<String> usersEmails;

    @NotNull
    private DatesRequest dates;

    public AllOutcomesReqeust(List<String> usersEmails, DatesRequest dates) {
        this.usersEmails = usersEmails;
        this.dates = dates;
    }

    public List<String> users() {
        return usersEmails;
    }

}
