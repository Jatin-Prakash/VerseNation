package com.marketplace.versenation.controllers;

import com.marketplace.versenation.exception.ResourceNotFoundException;
import com.marketplace.versenation.filters.TokenAuthenticationFilter;
import com.marketplace.versenation.models.Following.Following;
import com.marketplace.versenation.models.User;
import com.marketplace.versenation.models.userTypes.CreatorAccount;
import com.marketplace.versenation.payload.responses.ApiResponse;
import com.marketplace.versenation.payload.follow.FollowRequest;
import com.marketplace.versenation.payload.follow.GetFollowersResponse;
import com.marketplace.versenation.repository.CreatorRepository;
import com.marketplace.versenation.repository.FanRepository;
import com.marketplace.versenation.repository.FollowingRepository;
import com.marketplace.versenation.repository.UserRepository;
import com.marketplace.versenation.util.TokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("following")
public class FollowController {
    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    TokenAuthenticationFilter tokenAuthenticationFilter;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CreatorRepository creatorRepository;
    @Autowired
    FanRepository fanRepository;
    @Autowired
    FollowingRepository followingRepository;
    //considering id is creator id
    @PostMapping("follow/id")
    public ResponseEntity<?> FollowCreator(@RequestBody FollowRequest followRequest){
        //first get user profile of the person
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", auth.getName()));
        //find the creator about to be followed
         CreatorAccount creatorAccount;
         Long realId;

        try{
            System.out.println(followRequest.getId());
            realId = Long.parseLong(followRequest.getId());

            creatorAccount = creatorRepository.findById(realId).get();
        }catch(Exception e){
            return new ResponseEntity(new ApiResponse(false,"creator not found"), HttpStatus.BAD_REQUEST);
        }
        //check if youre trying to follow yourself
        if(user.getAccountType().toString().equals("CREATOR")) {
            if (user.getCreatorAccount().getId() == realId) {
                return new ResponseEntity(new ApiResponse(false, "cannot follow yourself"), HttpStatus.BAD_REQUEST);
            }
        }

        Following following=new Following(user,creatorAccount);
        try {
            followingRepository.save(following);
        }catch (Exception e) {
            return new ResponseEntity(new ApiResponse(false,"couldn't follow Creator"), HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(new ApiResponse(true,"follow successful"));

    }
    //unfollow using creator id
    @PostMapping("unfollow/id")
    public ResponseEntity<?> unfollowCreator(@RequestBody FollowRequest followRequest){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", auth.getName()));
        CreatorAccount creatorAccount;
        long creatorId;
        Following following;
        try{
                creatorId = Long.parseLong(followRequest.getId());
                creatorAccount = creatorRepository.findById(creatorId).get();
               following=followingRepository.findByFromAndTo(user,creatorAccount);
                followingRepository.delete(following);
                // followingRepository.deleteByFromAndTo(user.getId(),creatorAccount.getId());
        }catch (Exception e){
            return new ResponseEntity(new ApiResponse(false,"problem unfollowing creator"), HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(new ApiResponse(true,"unfollowed"));
    }

    //get top 100 followers by userid
    @GetMapping("getFollowers/{id}")
    public ResponseEntity<?> getFollowers(@PathVariable long id, HttpServletRequest request){
        User user;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        boolean bool = tokenProvider.validateToken(tokenAuthenticationFilter.getJwtFromRequest(request));
        try{
            user = userRepository.findById(id).get();
        }catch (Exception e){
            return new ResponseEntity(new ApiResponse(false,"user doesnt exist"),HttpStatus.BAD_REQUEST);
        }
        if(!user.getAccountType().toString().equals("CREATOR")){
            return new ResponseEntity(new ApiResponse(false,"user's not a creator"),HttpStatus.BAD_REQUEST);
        }
       List<Following> followers = followingRepository.findTop100ByTo(user.getCreatorAccount());
        GetFollowersResponse getFollowersResponse = new GetFollowersResponse(followers);

    return ResponseEntity.ok(getFollowersResponse);
    }



}
