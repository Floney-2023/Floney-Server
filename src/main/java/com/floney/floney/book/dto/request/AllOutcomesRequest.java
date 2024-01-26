package com.floney.floney.book.dto.request;

import com.floney.floney.common.domain.vo.DateDuration;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@ToString
@NoArgsConstructor
public class AllOutcomesRequest {

    private String bookKey;

    private List<String> usersEmails;

    @NotNull(message = "duration을 입력해주세요")
    private DateDuration duration;

    public AllOutcomesRequest(String bookKey, List<String> usersEmails, DateDuration duration) {
        this.bookKey = bookKey;
        this.usersEmails = usersEmails;
        this.duration = duration;
    }
}
