package com.floney.floney.common.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class PresignedUrlDto {
    private String fileName;
    private String url;

    public PresignedUrlDto(String fileName, String url) {
        this.fileName = fileName;
        this.url = url;
    }
}
