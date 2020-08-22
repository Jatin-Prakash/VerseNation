package com.marketplace.versenation.payload;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class SellingItemRequest {


    @NotBlank
    @Size(max =100)
    private String description;

    @NotBlank
    @Size(max=20)
    private String nameOfItem;


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNameOfItem() {
        return nameOfItem;
    }

    public void setNameOfItem(String nameOfItem) {
        this.nameOfItem = nameOfItem;
    }
}
