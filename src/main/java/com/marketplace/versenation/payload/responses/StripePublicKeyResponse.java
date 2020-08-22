package com.marketplace.versenation.payload.responses;

public class StripePublicKeyResponse {
    private String PublicKey;

    public StripePublicKeyResponse(String publicKey) {
        PublicKey = publicKey;
    }

    public String getPublicKey() {
        return PublicKey;
    }

    public void setPublicKey(String publicKey) {
        PublicKey = publicKey;
    }
}
