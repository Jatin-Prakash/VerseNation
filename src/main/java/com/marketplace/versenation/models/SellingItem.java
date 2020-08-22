package com.marketplace.versenation.models;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
public class SellingItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotBlank
    @Size(max = 15)
    private String owner;
    @Size(max = 15)
    private String newOwner;
    private long price;
    //status can be pending,sold,waiting for payment
    private String status = "pending";
    private String nameOfItem;
    private String description;

    @OneToOne(fetch = FetchType.LAZY)
    private Bid bid;

    public long getId() {
        return id;
    }

    public SellingItem() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        description = description;
    }

    public SellingItem(String owner, String nameOfItem,String description){
         this.owner=owner;
         this.nameOfItem=nameOfItem;
         this.description = description;
    }
    public SellingItem(String owner, String nameOfItem, long Price){
         this.owner=owner;
         this.nameOfItem=nameOfItem;
         this.price=price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getNameOfItem() {
        return nameOfItem;
    }

    public void setNameOfItem(String nameOfItem) {
        this.nameOfItem = nameOfItem;
    }
    public String getNewOwner() {
        return newOwner;
    }

    public void setNewOwner(String newOwner) {
        this.newOwner = newOwner;
    }
    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

}
