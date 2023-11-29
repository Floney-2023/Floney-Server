package com.floney.floney.book.dto.request;

import com.floney.floney.book.dto.process.DatesDuration;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@ToString
@NoArgsConstructor
public class AllOutcomesRequest {

    private  String bookKey;

    private  List<String> usersEmails;

    @NotNull(message = "duration을 입력해주세요")
    private  DatesDuration duration;

    public AllOutcomesRequest(String bookKey, List<String> usersEmails, DatesDuration duration) {
        this.bookKey = bookKey;
        this.usersEmails = usersEmails;
        this.duration = duration;
    }
}
