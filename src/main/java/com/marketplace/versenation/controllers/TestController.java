package com.marketplace.versenation.controllers;

import com.marketplace.versenation.exception.ResourceNotFoundException;
import com.marketplace.versenation.models.User;
import com.marketplace.versenation.payload.UserInfo;
import com.marketplace.versenation.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @Autowired
    UserRepository userRepository;
    @GetMapping("profile")
    public ResponseEntity<?> test(){

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", auth.getName()));
      UserInfo userInfo;
         if(user.getAccountType() == User.AccountType.CREATOR) {
            userInfo = new UserInfo(user.getUsername(), user.getAccountType().toString(), user.getEmail(),user.getCreatorAccount().getAccountTier());
        }else{
            userInfo = new UserInfo(user.getUsername(), user.getAccountType().toString(),user.getEmail(),user.getFanAccount().getAccountTier());
        }
        return ResponseEntity.ok(userInfo);

    }
}
