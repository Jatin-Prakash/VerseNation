package com.marketplace.versenation.controllers;

import com.marketplace.versenation.exception.ResourceNotFoundException;
import com.marketplace.versenation.models.userTypes.CreatorAccount;
import com.marketplace.versenation.service.BiddingService;
import com.marketplace.versenation.models.Bid;
import com.marketplace.versenation.models.User;
import com.marketplace.versenation.payload.JoinBidRequest;
import com.marketplace.versenation.payload.responses.ApiResponse;
import com.marketplace.versenation.repository.BidRepository;
import com.marketplace.versenation.repository.SellingItemRepository;
import com.marketplace.versenation.repository.UserRepository;
import com.marketplace.versenation.util.StripeService;
import com.marketplace.versenation.util.Utility;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("bidding")
public class BiddingController {

     static int numberOfBidders = 0;
     static List<String> userName = new ArrayList<String>();
    @Autowired
    UserRepository userRepository;
    private StripeService stripeService;
    private BiddingService biddingService;

    @Autowired
    SellingItemRepository sellingItemRepository;
    @Autowired
    BidRepository bidRepository;

    @PostMapping("/join")
    public ResponseEntity<?> joinRoom(@Valid @RequestBody JoinBidRequest joinBidRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", auth.getName()));
        userName.add(user.getName());
        Bid bid;

        long getDateDiff = 0L;
        long getDateDiffInMinutes=0L;
        boolean isUserNewInBiddingRoom = false;
        try {
            bid = bidRepository.findById(joinBidRequest.getTheBidId()).get();
            isUserNewInBiddingRoom = bid.getBidders().contains(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
        long biddingAmount;
        getDateDiff = bid.getDateDiff(bid.getCreationDate(), bid.getCurrentDate());
        Boolean isUserPresent = bid.getBidders().contains(user.getEmail());
        //Will only enter if the difference is of 48 hours
        if (47 < getDateDiff && isUserPresent) {
            // bidding price need to be higher than the previous one : incorporated
            if(bid.isFirstBid(bid)){
                bid.startBid();
                biddingAmount=bid.getStartingPrice();
            }else {
                biddingAmount=bid.getBids().get(bid.getBids().size()-1).getPrice() + 2;
            }
            //This logic goes in BiddingRoomCreator
            //instead Check if the user is already in the room

            if (!biddingService.canJoinRoom(user, bid)) {
                return ResponseEntity.badRequest().build();
            }

            //This logic goes in BiddingRoomCreator
            String chargeId = stripeService.createCharge(user.getEmail(), "", 40);
            if (!Utility.isNotNullOrEmpty(chargeId))
                return ResponseEntity.noContent().build();
            //add bidder

           //save bid into repo
            bidRepository.save(bid);
            getDateDiffInMinutes = bid.getDateDiffInMinutes(bid.getCreationDate(), bid.getCurrentDate());
            if(!(15>getDateDiffInMinutes)){
             try {
                 Thread.sleep(6000);
             }catch (InterruptedException e){
                 //new User enters
                 bid.setBidders(userName);
                 isUserNewInBiddingRoom=true;
             }
            }
            else if(!(1>getDateDiff)) {
                try {
                    Thread.sleep(900000);
                }catch (InterruptedException e){
                    //new User enters
                    bid.setBidders(userName);
                    isUserNewInBiddingRoom=true;
                }
            }

            if(!isUserNewInBiddingRoom){
                bid.setBidWinner(user.getUsername());
                bid.closeBid();
                return ResponseEntity.ok(new ApiResponse(true, "we have a winner "+user.getName()));
            }


        }else{
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new ApiResponse(true, "we have a winner "+user.getName()));
    }
}
