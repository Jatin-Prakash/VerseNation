package com.marketplace.versenation.payload.requests;

import javax.validation.constraints.NotBlank;

public class StripeTokenRequest {
    @NotBlank
    private String token;



    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
