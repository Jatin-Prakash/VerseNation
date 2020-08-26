package com.marketplace.versenation.service;
//logic behind allowing bids

import com.marketplace.versenation.models.Bid;
import com.marketplace.versenation.models.SellingItem;
import com.marketplace.versenation.models.User;
import com.marketplace.versenation.models.userTypes.CreatorAccount;

public class BiddingService {
     public boolean canCreateBid(User user, SellingItem sellingItem){
     /*    if(user.isPremiumAccount() && user.getParentUserType().getUserType().equals("CREATOR") && sellingItem.getOwner().equals(user.getUsername())){
             return true;
         }*/
         return false;
     }
     public boolean canJoinRoom(User user,Bid bid){
       CreatorAccount creatorAccount= user.getCreatorAccount();
        if(!CreatorAccount.AccountTier.FREE.equals(creatorAccount.getAccountTier())&& !bid.getSellingItem().getOwner().equals(user.getUsername())
                 && bid.canAddBidder() && !isUserACreator(user) &&  canPlaceBidWithUserName(user.getUsername(), bid)){
             return true;
         }
         return false;
     }

     public boolean hasNewUserAdded(){
     return false;
     }
     public boolean isUserACreator(User user){
         return User.AccountType.CREATOR.equals(user.getAccountType());
     }
    public boolean canPlaceBidWithUserName(String user,Bid bid){
        if(bid.getBidders().contains(user) && bid.hasBiddingStarted()){
            return false;
        }
        return true;
    }

}
