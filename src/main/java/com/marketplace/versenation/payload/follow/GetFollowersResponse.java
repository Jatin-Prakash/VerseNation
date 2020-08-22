package com.marketplace.versenation.payload.follow;

import com.marketplace.versenation.models.Following.Following;
import com.marketplace.versenation.payload.UserInfo;

import java.util.ArrayList;
import java.util.List;

public class GetFollowersResponse {
    List<UserInfo> userInfos=new ArrayList<>();

    public GetFollowersResponse(List<Following> followers){
        if(!followers.isEmpty()){
            for(Following follower: followers){
             UserInfo userInfo = new UserInfo(follower.getFrom().getUsername(),follower.getFrom().getAccountType().toString(),
                     follower.getFrom().getEmail(),
                     follower.getFrom().getAccountType().toString().equals("CREATOR")? follower.getFrom().getCreatorAccount().getAccountTier():
                             follower.getFrom().getFanAccount().getAccountTier()
             );
                userInfos.add(userInfo);
            }
        }
    }

    public List<UserInfo> getUserInfos() {
        return userInfos;
    }

    public void setUserInfos(List<UserInfo> userInfos) {
        this.userInfos = userInfos;
    }
}
