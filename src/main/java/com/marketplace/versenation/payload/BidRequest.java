package com.marketplace.versenation.payload;

import org.hibernate.validator.constraints.Range;


public class BidRequest {
   private long sellingItemId;

    @Range(min=20,max=100000)
    private long startingPrice;

    public BidRequest(long sellingItemId,long startingPrice) {
        this.sellingItemId = sellingItemId;
        this.startingPrice=startingPrice;
    }

    public long getSellingItemId() {
        return sellingItemId;
    }

    public void setSellingItemId(long sellingItemId) {
        this.sellingItemId = sellingItemId;
    }

    public long getStartingPrice() {
        return startingPrice;
    }

    public void setStartingPrice(long startingPrice) {
        this.startingPrice = startingPrice;
    }
}
