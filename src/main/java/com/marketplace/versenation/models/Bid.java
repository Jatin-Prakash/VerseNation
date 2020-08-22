package com.marketplace.versenation.models;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.validator.constraints.Range;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Entity
@Table( uniqueConstraints = {
        @UniqueConstraint(columnNames = {
                "selling_item_id"
        })
})
public class Bid {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name="bid_closing_number")
    private final int bidClosingNumber = 3;
    @Column(name="one_day_in_millies")
    private final long oneDayInMillies= 86400000;
    @CreationTimestamp
    @Temporal(TemporalType.DATE)
    @Column(name="bid_creation_date")
    private Date creationDate;
    @Temporal(TemporalType.DATE)
    private Date bidStartDate;
    @NotBlank
    @Size(max = 15)
    private String bidCreator;
    //user accounts for bidders
    @ElementCollection
    private List<String> bidders=new ArrayList<>();

    //bids by users
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn
    private List<Bidding> bids=new ArrayList<>();
    @Size(max = 15)
    private String bidWinner;
    @Range(min=20,max=100000)
    private long startingPrice;

    @OneToOne(fetch = FetchType.LAZY)
    private SellingItem sellingItem;

    private boolean hasBidStarted=true;
    private boolean isBidRoomClosed=false;
    private boolean isbiddingDone=false;


    public Bid(){

    }



    public long getId() {
        return id;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public long getDateDiff(Date date1, Date date2) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return TimeUnit.HOURS.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }
    public Date getCurrentDate(){
        Date date= new Date();
        return date;
    }
    public boolean canAddBidder(){
        if(hasBidStarted && !isBidRoomClosed && !isbiddingDone){
            return true;
        }
        return false;
    }
    public boolean hasBiddingStarted(){
        if(!hasBidStarted && isBidRoomClosed && !isbiddingDone){
            return true;
        }
        return false;
    }
    public boolean isBidFull(){
        return this.bidders.size() >= bidClosingNumber;
    }
    public void startBid(){
        this.hasBidStarted = false;
        this.isBidRoomClosed = true;
        //starting bid

        bidStartDate = new Date();
    }
    public void addBidder(String bidder){
                //have to check is bidder can enter
                if (canAddBidder()) {
                    this.bidders.add(bidder);

                } else {
                    System.out.println(bidder+" couldn't be added");
                }

    }
    public void placeBid(Bidding bidding){
        if(hasBiddingStarted() && bidders.contains(bidding.getBidder())){
            if(this.bids.isEmpty() ){
                if (bidding.getPrice()>this.startingPrice){
                    System.out.println(bidding.getPrice());
                    this.bids.add(bidding);
                }else{
                    System.out.println(bidding.getBidder()+" bid cannot be lower than "+this.startingPrice);
                }
            }else{
                if(this.bids.get(this.bids.size()-1).getPrice()< bidding.getPrice()){
                    System.out.println(bidding.getPrice());
                    this.bids.add(bidding);
                }else{
                }
            }
            //need help with checking time frequently??
            if(isBidDone()){
                this.bidDone();
            }
            //the above code only checks time when someone bids
        }else{
            System.out.println("bidding hasn't started yet or bidder not in room");
        }
    }
    //special control of closing bid for testing
    public void closeBid(){
        this.bidDone();
       // System.out.println("the winner of this bid is "+this.bids.get(this.bids.size()-1).getBidder().getName());
       // System.out.println("he has won "+sellingItem.getOwner().getName()+"'s "+sellingItem.getNameOfItem()+" for the price of "+sellingItem.getPrice());
    }
    //special control of selling item if closed and no bid is found
    //end of special control
    public boolean isBidDone(){
        //checks if bid has been running for a day
        Date currentDate = new Date();
        long timeDiffinMilli = getDateDiff(bidStartDate,currentDate);
        if(timeDiffinMilli > oneDayInMillies){
            return true;
        }
        return false;
    }
    public void bidDone(){
        //if bid has been running for a day closes bid
        this.isBidRoomClosed=false;
        this.isbiddingDone=true;
        //sets bid winner to user that bid last
        if(!this.bids.isEmpty()) {
            this.bidWinner = this.bids.get(this.bids.size() - 1).getBidder();
            this.sellingItem.setStatus("waiting for payment");
            this.sellingItem.setNewOwner(this.bidWinner);
            this.sellingItem.setPrice(this.bids.get(this.bids.size()-1).getPrice());
        }else{
            System.out.println("no one decided to bid");
        }

    }

    public void setBidCreator(String bidCreator) {
        this.bidCreator = bidCreator;
    }

    public void setBidders(List<String> bidders) {
        this.bidders = bidders;
    }

    public void setBids(List<Bidding> bids) {
        this.bids = bids;
    }

    public String getBidWinner() {
        return bidWinner;
    }

    public void setBidWinner(String bidWinner) {
        this.bidWinner = bidWinner;
    }

    public void setStartingPrice(long startingPrice) {
        this.startingPrice = startingPrice;
    }

    public void setSellingItem(SellingItem sellingItem) {
        this.sellingItem = sellingItem;
    }

    public String getBidCreator() {
        return bidCreator;
    }

    public long getStartingPrice() {
        return startingPrice;
    }

    public SellingItem getSellingItem() {
        return sellingItem;
    }

    public List<Bidding> getBids() {
        return bids;
    }

    public List<String> getBidders() {
        return bidders;
    }
}
