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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("bidding")
public class BiddingController {
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

        Bid bid;

        long getDateDiff = 0L;
        try {
            bid = bidRepository.findById(joinBidRequest.getTheBidId()).get();
            getDateDiff = bid.getDateDiff(bid.getCreationDate(), bid.getCurrentDate());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
        if (0L != getDateDiff && 47 < getDateDiff) {
            if (!biddingService.canJoinRoom(user, bid)) {
                return ResponseEntity.ok(new ApiResponse(false, "bidder can't join room"));
            }

            String chargeId = stripeService.createCharge(user.getEmail(), "", 40);
            if (!Utility.isNotNullOrEmpty(chargeId))
                return ResponseEntity.noContent().build();
            //add bidder
            bid.addBidder(user.getUsername());
           //save bid into repo
            bidRepository.save(bid);

            return ResponseEntity.ok(new ApiResponse(true, "bidder has joined room"));



        }else{
            return ResponseEntity.notFound().build();
        }
    }
}
