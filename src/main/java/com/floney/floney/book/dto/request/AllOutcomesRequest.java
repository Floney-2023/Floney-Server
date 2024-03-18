package com.floney.floney.book.dto.request;

import com.floney.floney.common.domain.vo.DateDuration;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class AllOutcomesRequest {

    private String bookKey;

    private List<String> usersEmails;

    @NotNull(message = "duration을 입력해주세요")
    private DateDuration duration;
}
