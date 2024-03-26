package com.floney.floney.common.exception.common;


import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

@Getter
public class ErrorResponse {

    private final String message;
    private final String code;
    private Map<String, Object> attributes;

    private ErrorResponse(ErrorType errorType) {
        this.message = errorType.getMessage();
        this.code = errorType.getCode();
        this.attributes = new HashMap<>();
    }

    @JsonCreator
    private ErrorResponse(final String message, final String code) {
        this.message = message;
        this.code = code;
        this.attributes = new HashMap<>();
    }

    private ErrorResponse(final ErrorType errorType, final Map<String, Object> attributes) {
        this.message = errorType.getMessage();
        this.code = errorType.getCode();
        this.attributes = attributes;
    }

    public static ErrorResponse of(final ErrorType errorType) {
        return new ErrorResponse(errorType);
    }

    public static ErrorResponse of(final ErrorType errorType, final String customMessage) {
        return new ErrorResponse(customMessage, errorType.getCode());
    }

    public static ErrorResponse of(final ErrorType errorType, final Map<String, Object> attributes) {
        return new ErrorResponse(errorType, attributes);
    }

    @JsonAnyGetter
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

}
