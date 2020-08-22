package com.marketplace.versenation.models;

import javax.persistence.*;

@Entity
public class Bidding {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String bidder;
    private long price;

    @ManyToOne(fetch = FetchType.LAZY)
    private Bid bid;

    public Bidding(String bidder, long price){
        this.bidder=bidder;
        this.price=price;

    }

    public String getBidder() {
        return bidder;
    }

    public void setBidder(String bidder) {
        this.bidder = bidder;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }
}
