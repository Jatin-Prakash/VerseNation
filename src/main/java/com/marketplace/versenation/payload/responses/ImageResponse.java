package com.marketplace.versenation.payload.responses;

public class ImageResponse {
    private byte[] imageBody;
    private String imageType;

    public byte[] getImageBody() {
        return imageBody;
    }

    public void setImageBody(byte[] imageBody) {
        this.imageBody = imageBody;
    }

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }
}
