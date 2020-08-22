package com.marketplace.versenation.controllers;

import com.marketplace.versenation.exception.ResourceNotFoundException;
import com.marketplace.versenation.logic.CreatorSubscription;
import com.marketplace.versenation.models.Subscription;
import com.marketplace.versenation.models.User;
import com.marketplace.versenation.models.userTypes.CreatorAccount;
import com.marketplace.versenation.models.userTypes.FanAccount;
import com.marketplace.versenation.payload.requests.StripeTokenRequest;
import com.marketplace.versenation.payload.responses.ApiResponse;
import com.marketplace.versenation.payload.responses.StripePublicKeyResponse;
import com.marketplace.versenation.repository.CreatorRepository;
import com.marketplace.versenation.repository.FanRepository;
import com.marketplace.versenation.repository.SubscriptionRepository;
import com.marketplace.versenation.repository.UserRepository;
import com.marketplace.versenation.util.StripeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
public class PaymentController {
    @Value("${stripe.public}")
    private String API_PUBLIC_KEY;
    @Autowired
    UserRepository userRepository;
    private StripeService stripeService;
    @Autowired
    SubscriptionRepository subscriptionRepository;
    @Autowired
    FanRepository fanRepository;
    @Autowired
    CreatorRepository creatorRepository;
    public PaymentController(StripeService stripeService) {
        this.stripeService = stripeService;
    }
    /**
     Get all subscription types from application properties
     **/
    @Value("${stripe.tier.basic.monthly}")
    private String basicMonthlyKey;
    @Value("${stripe.tier.student.monthly}")
    private String studentMonthlyKey;
    @Value("${stripe.tier.bigbreak.monthly}")
    private String bigBreakMonthlyKey;
    @Value("${stripe.tier.bigbreak.yearly}")
    private String bigBreakYearlyKey;
    @Value("${stripe.tier.powerhouse.yearly}")
    private String powerHouseYearlykey;
    @Value("${stripe.tier.powerhouse.monthly}")
    private String powerHouseMonthlyKey;
    //methods for checking stuff


    @GetMapping("/publickey")
    public ResponseEntity<?> getPublicKey(){
        return ResponseEntity.ok(new StripePublicKeyResponse(API_PUBLIC_KEY));
    }
    /**
     * create subscription and customer only if it doesnt exist in the database
     * **/
    @PostMapping("/create-subscription/{subType}")
    public ResponseEntity<?> createSubscription(@RequestBody StripeTokenRequest stripeTokenRequest,@PathVariable String subType){
        //type of subscription
        String type = subType.toUpperCase();
        //find the logged in user first
        User user;
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            user = userRepository.findByUsername(auth.getName())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "username", auth.getName()));
        }catch (Exception e){
            return new ResponseEntity(new ApiResponse(false,"expired user token"), HttpStatus.BAD_REQUEST);
        }
        //if token is empty
        if (stripeTokenRequest.getToken() == null) {
            return new ResponseEntity(new ApiResponse(false, "Stripe payment token is missing. Please, try again later."),HttpStatus.BAD_REQUEST);
        }
        //if account is not fan return error
        //replace this with checkType???
        CreatorSubscription creatorSubscription = new CreatorSubscription(user,type);
          if(!creatorSubscription.checkType()){
              return new ResponseEntity(new ApiResponse(false,"not a subscription type"), HttpStatus.BAD_REQUEST);
          }

        //see if customer exists on database
        Subscription subscription = subscriptionRepository.findByUser(user);
        String customerId = null;
        if(subscription.getCustomerId() == null){
            //now create a customer using email
             customerId= stripeService.createCustomer(user.getEmail(),stripeTokenRequest.getToken());
            if (customerId == null) {
                return new ResponseEntity(new ApiResponse(false, "An error occurred while trying to create a customer."),HttpStatus.BAD_REQUEST);
            }
            //save new customerid to databse
            subscription.setCustomerId(customerId);
            subscriptionRepository.save(subscription);
        }else{
            //if customer exists set customer id
            customerId = subscription.getCustomerId();
        }
        //check if subscription exists on my database
        String subStatus;
        String subscriptionId;
        if(subscription.getSubscriptionId() != null){
            //check the subscriptionstatus on stripe API
            subStatus = stripeService.getSubStatus(subscription.getSubscriptionId());
            if(!subStatus.equals("canceled")){
              //means it has active subscription
                return new ResponseEntity(new ApiResponse(false,"you have an active subscription"),HttpStatus.BAD_REQUEST);
            }
        }
        //if subscription is cancelled or there is no subscription on my database
        //create subscription
        //change code from this on
        String whichKey="";
        switch(type){
            case "BASIC":
                whichKey = basicMonthlyKey;
                break;
            case "STUDENT":
                whichKey = studentMonthlyKey;
                break;
            case "POWERHOUSE":
                whichKey = powerHouseMonthlyKey;
                break;
            case "BIGBREAK":
                whichKey = bigBreakMonthlyKey;
                break;
        }


         subscriptionId = stripeService.createSubscription(customerId, whichKey,"");
        if (subscriptionId == null) {
            return new ResponseEntity(new ApiResponse(false, "An error occurred while trying to create a subscription."),HttpStatus.BAD_REQUEST);
        }

        /**
         create subscription to save on db using subscription tier, user,subscription id and customer id
         **/
        //at this point subscription was succesful and save it to the database
        subscription.setSubscriptionId(subscriptionId);
        subscription.setSubscriptionTier(type);
        subscriptionRepository.save(subscription);

        //update fan or creator account tier
        if(user.getAccountType().toString().equals("FAN")) {
            FanAccount fanAccount = user.getFanAccount();
            fanAccount.upgradeAccount("BASIC");
            fanRepository.save(fanAccount);
        }else if(user.getAccountType().toString().equals("CREATOR")){
            CreatorAccount creatorAccount=user.getCreatorAccount();
            creatorAccount.upgradeAccount(type);
            creatorRepository.save(creatorAccount);
        }

        //return success message
        return ResponseEntity.ok(new ApiResponse(true, "Success! Your subscription id is " + subscriptionId));
    }

    /**
     * cancelling subscription
     *
     * **/
    @PostMapping("/cancel-subscription")
    public ResponseEntity<?> cancelSubscription(){
        //find the logged in user first
        User user;
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            user = userRepository.findByUsername(auth.getName())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "username", auth.getName()));
        }catch (Exception e){
            return new ResponseEntity(new ApiResponse(false,"expired user token"), HttpStatus.BAD_REQUEST);
        }
        //find subscription id by user
        Subscription subscription;
        subscription = subscriptionRepository.findByUser(user);
        if(subscription == null){
            return new ResponseEntity(new ApiResponse(false,"can't find subscription"), HttpStatus.BAD_REQUEST);
        }
        String subscriptionId = subscription.getSubscriptionId();
        boolean status = stripeService.cancelSubscription(subscriptionId);
        if (!status) {
            return new ResponseEntity(new ApiResponse(false,"Failed to cancel the subscription. Please, try later."), HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(new ApiResponse(true,"subscription cancelled succesfully"));
    }
    /**
     * Buy  points for fan/general accounts
     * **/
    @PostMapping("/buypoints")
    public ResponseEntity<?> buyPoints(@RequestBody StripeTokenRequest stripeTokenRequest){
        //find the logged in user first
        User user;
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            user = userRepository.findByUsername(auth.getName())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "username", auth.getName()));
        }catch (Exception e){
            return new ResponseEntity(new ApiResponse(false,"expired user token"), HttpStatus.BAD_REQUEST);
        }
        //check if its a fan account
        if(!user.getAccountType().toString().equals("FAN")){
            return new ResponseEntity(new ApiResponse(false,"creators cannot subscribe to this"), HttpStatus.BAD_REQUEST);
        }
        //check token
        if(stripeTokenRequest.getToken() == null){
            return new ResponseEntity(new ApiResponse(false,"Stripe token missing"), HttpStatus.BAD_REQUEST);
        }
        //create charge
        String chargeId = stripeService.createCharge(user.getEmail(),stripeTokenRequest.getToken(),299); // $2.99
        if(chargeId == null){
            return new ResponseEntity(new ApiResponse(false,"an error happened while trying to charge"), HttpStatus.BAD_REQUEST);
        }
        //success
        /** save chargeid and order information on database(not implemented yet)
         * **/
        FanAccount fanAccount = user.getFanAccount();
        fanAccount.addPoints(40);
        fanRepository.save(fanAccount);
        return ResponseEntity.ok(new ApiResponse(true,"succesfully bought 40 points"));
    }

}
