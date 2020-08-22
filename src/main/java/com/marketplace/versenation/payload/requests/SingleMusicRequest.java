package com.marketplace.versenation.payload.requests;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class SingleMusicRequest {

    @NotBlank
    @Size(min=2,max=40)
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
