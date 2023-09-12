package com.floney.floney.user.client;

import org.springframework.web.client.RestTemplate;

public interface ClientProxy {

    default RestTemplate createRequest() {
        return new RestTemplate();
    }

    String getAuthId(String authToken);

}
