package com.floney.floney.common.exception.book;

import com.floney.floney.common.exception.common.ErrorType;
import com.floney.floney.common.exception.common.FloneyException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ExcelMakingException extends FloneyException {

    public ExcelMakingException(final String message) {
        super(ErrorType.FAIL_TO_CREATE_EXCEL, HttpStatus.INTERNAL_SERVER_ERROR);
        logger.error("엑셀 오류 발생 [ERROR_MSG] : {} \n [ERROR_STACK] : {} \n ", message, getStackTrace());
    }
}
