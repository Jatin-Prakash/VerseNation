package com.marketplace.versenation.controllers;

import com.marketplace.versenation.models.User;
import com.marketplace.versenation.payload.responses.ApiResponse;
import com.marketplace.versenation.payload.UserInfo;
import com.marketplace.versenation.repository.CreatorRepository;
import com.marketplace.versenation.repository.FanRepository;
import com.marketplace.versenation.repository.FollowingRepository;
import com.marketplace.versenation.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    UserRepository userRepository;
    @Autowired
    CreatorRepository creatorRepository;
    @Autowired
    FanRepository fanRepository;
    @Autowired
    FollowingRepository followingRepository;

    @GetMapping("{id}")
    public ResponseEntity<?> getUserFromId(@PathVariable long id){
      User user;
      try {
          user = userRepository.findById(id).get();
      }catch (Exception e){
          return new ResponseEntity(new ApiResponse(false,"user doesn't exist with that id"),HttpStatus.BAD_REQUEST);
      }
      UserInfo userInfo;
     if(user.getAccountType().toString().equals("CREATOR")) {
         userInfo = new UserInfo(user.getUsername(), user.getAccountType().toString(), user.getEmail(), user.getCreatorAccount().getAccountTier());
     }else{
         userInfo = new UserInfo(user.getUsername(), user.getAccountType().toString(), user.getEmail(), user.getFanAccount().getAccountTier());
     }
     return ResponseEntity.ok(userInfo);
    }
}
