package com.floney.floney.common.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class PresignedUrlDto {
    private String fileName;
    private String url;
    private String viewUrl;

    public PresignedUrlDto(String fileName, String url,String viewUrl) {
        this.fileName = fileName;
        this.url = url;
        this.viewUrl = viewUrl;
    }
}
