package com.marketplace.versenation.logic;

import com.marketplace.versenation.models.User;
import com.marketplace.versenation.payload.responses.ApiResponse;
import com.marketplace.versenation.util.StripeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CreatorSubscription {

    /**
     Get all subscription types from application properties
     **/
    @Value("${stripe.tier.basic.monthly}")
    private String basicMonthlyKey;
    @Value("${stripe.tier.student.monthly}")
    private String studentMonthlyKey;
    @Value("${stripe.tier.bigbreak.monthly}")
    private String bigBreakMonthlyKey;
    @Value("${stripe.tier.powerhouse.monthly}")
    private String powerHouseMonthly;

    private final List<String> creatorTypes = Arrays.asList(new String[]{"POWERHOUSE","BIGBREAK"});
    private final List<String> fanTypes = Arrays.asList(new String[]{"BASIC","STUDENT"});
    private HashMap<String,String> values = new HashMap<>();
    private User user;
    private String type;

    public CreatorSubscription(User user,String type){
        this.user=user;
        this.type=type;

    }
    public boolean checkType(){
        //check if user is creator or fan
        if(user.getAccountType().toString().equals("FAN")){
            //it's a fan account
            if(fanTypes.contains(type)){
                //it's a valid form of subscription
                return true;
            }
            //it doesn't have valid form of subscribtion
            return false;
        }else if(user.getAccountType().toString().equals("CREATOR")){
            //it's a creator account
            if(creatorTypes.contains(type)){
                return true;
            }
            return false;
        }else {
            return false;
        }
    }
    //public ResponseEntity<?> trySubscribing(StripeService stripeService,)

}
