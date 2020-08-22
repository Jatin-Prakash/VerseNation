package com.marketplace.versenation.controllers;

import com.marketplace.versenation.config.AppProperties;
import com.marketplace.versenation.filters.TokenAuthenticationFilter;
import com.marketplace.versenation.models.ArtistDescription;
import com.marketplace.versenation.models.AuthProvider;
import com.marketplace.versenation.models.Refresh.RefreshToken;
import com.marketplace.versenation.models.Subscription;
import com.marketplace.versenation.models.User;
import com.marketplace.versenation.models.userTypes.CreatorAccount;
import com.marketplace.versenation.models.userTypes.FanAccount;
import com.marketplace.versenation.payload.requests.AuthenticationRequest;
import com.marketplace.versenation.payload.requests.SignUpRequest;
import com.marketplace.versenation.payload.requests.SignUpRequestFan;
import com.marketplace.versenation.payload.responses.ApiResponse;
import com.marketplace.versenation.payload.responses.AuthResponseWithRefresh;
import com.marketplace.versenation.payload.responses.AuthenticationResponse;
import com.marketplace.versenation.repository.*;
import com.marketplace.versenation.security.MyUserDetailsService;

import com.marketplace.versenation.util.TokenProvider;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.persistence.RollbackException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("authenticate")
public class AuthController {
    @Autowired
    AppProperties appProperties;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private CreatorRepository creatorRepository;
    @Autowired
    private FanRepository fanRepository;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private MyUserDetailsService myUserDetailsService;
    @Autowired
    private TokenAuthenticationFilter tokenAuthenticationFilter;
//    @Autowired
//    private JwtTokenProvider tokenProvider;
    @Autowired
    private TokenProvider tokenProvider;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @GetMapping("hello")
    public String hello(){

        return "hello world";
    }
    @RequestMapping(value="login",method= RequestMethod.POST)
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest authenticationRequest) throws Exception{
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getUsername(),
                        authenticationRequest.getPassword()
                )
        );
        User user;
        SecurityContextHolder.getContext().setAuthentication(authentication);
         user = userRepository.findByUsername(authentication.getName()).get();
        String jwt = tokenProvider.createToken(authentication);
        //get refresh token
        RefreshToken refreshTok;

        try {
            refreshTok = refreshTokenRepository.findByUser(user);
            //checks if refreshTok exists
            refreshTok.generateRandomToken();
        }catch(Exception e){
            refreshTok = new RefreshToken(user);
            refreshTok.generateRandomToken();
        }
        //set random refresh token
        try {
            refreshTokenRepository.save(refreshTok);

        }catch (RollbackException e){
            refreshTok.setToken("failedGeneratingRandomToken");
            System.out.println("not unique gotta log back in 5 minutes");
        }
        String refreshToken = refreshTok.getToken();
        return ResponseEntity.ok(new AuthResponseWithRefresh(jwt,refreshToken));

    }
   /**
    * When a user sign up a subscription should be createrd
    * **/
    @PostMapping("/signup/creator")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        if(userRepository.existsByUsername(signUpRequest.getUsername())) {
            return new ResponseEntity(new ApiResponse(false, "Username is already taken!"),
                    HttpStatus.BAD_REQUEST);
        }

        if(userRepository.existsByEmail(signUpRequest.getEmail())) {
            return new ResponseEntity(new ApiResponse(false, "Email Address already in use!"),
                    HttpStatus.BAD_REQUEST);
        }

        // Creating user's account

      //  User user = new User(signUpRequest.getName(), signUpRequest.getUsername(),
        //        signUpRequest.getEmail(), signUpRequest.getPassword(),"CREATOR");
        //checks if artist description is part of enum
        if(!ArtistDescription.contains(signUpRequest.getArtistDescription())){
            return new ResponseEntity(new ApiResponse(false,"not a right Artist description"),HttpStatus.BAD_REQUEST);
        }
        //converts to enum
         ArtistDescription artistDescription = ArtistDescription.valueOf(signUpRequest.getArtistDescription());

          User user = new User(signUpRequest.getName(), signUpRequest.getUsername(),
                signUpRequest.getEmail(), signUpRequest.getPassword(),User.AccountType.CREATOR);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        user.setCreatorAccount(new CreatorAccount());
        user.setProvider(AuthProvider.local);
        User result = userRepository.save(user);
        //creating a refresh token


       CreatorAccount creatorAccount = result.getCreatorAccount();
       creatorAccount.setUser(result);
       creatorAccount.setBio(signUpRequest.getBio());
       creatorAccount.setArtistDescription(artistDescription);
       creatorRepository.save(creatorAccount);

       //create an empty subscription row
       Subscription subscription = new Subscription("FREE",result);
       subscriptionRepository.save(subscription);
        return ResponseEntity.ok(new ApiResponse(true, "User registered successfully"));
    }

    //sign up for a fan
    @PostMapping("/signup/fan")
    public ResponseEntity<?> registerFan(@Valid @RequestBody SignUpRequestFan signUpRequest) {
        if(userRepository.existsByUsername(signUpRequest.getUsername())) {
            return new ResponseEntity(new ApiResponse(false, "Username is already taken!"),
                    HttpStatus.BAD_REQUEST);
        }

        if(userRepository.existsByEmail(signUpRequest.getEmail())) {
            return new ResponseEntity(new ApiResponse(false, "Email Address already in use!"),
                    HttpStatus.BAD_REQUEST);
        }

        // Creating user's account
        User user = new User(signUpRequest.getName(), signUpRequest.getUsername(),
                signUpRequest.getEmail(), signUpRequest.getPassword(),User.AccountType.FAN);

        user.setPassword(passwordEncoder.encode(user.getPassword()));

       // user.setCreatorAccount(new CreatorAccount());

        user.setFanAccount(new FanAccount());
        user.setProvider(AuthProvider.local);
        User result = userRepository.save(user);
        //creating a refresh token
//        RefreshToken refreshToken = new RefreshToken(result);
//        refreshToken.generateRandomToken();
//        refreshTokenRepository.save(refreshToken);

        FanAccount fanAccount= result.getFanAccount();
        fanAccount.setUser(result);

        fanRepository.save(result.getFanAccount());

        //create an empty subscription row
        Subscription subscription = new Subscription("FREE",result);
        subscriptionRepository.save(subscription);


        return ResponseEntity.ok(new ApiResponse(true, "User registered successfully"));
    }
    @GetMapping("/refreshJWT")
    public ResponseEntity<?> refreshJwt(HttpServletRequest request){
        String jwtToken;
        String refreshToken;
        long userid;
        User user;
        String newJwtToken;
        //get jwtToken first;
        try {
            jwtToken = tokenAuthenticationFilter.getJwtFromRequest(request);
            refreshToken = request.getHeader("refresh-token");
        }catch(Exception e){
            return new ResponseEntity(new ApiResponse(false,"can't find jwt or refresh token authentication"),HttpStatus.BAD_REQUEST);
        }
        //see if jwt token is expired
        try{
            Jwts.parser().setSigningKey(appProperties.getAuth().getTokenSecret()).parseClaimsJws(jwtToken);
        }catch(ExpiredJwtException e){
            //if the token is expired
            try{
                //get user using refreshToken
                user = refreshTokenRepository.findByToken(refreshToken).getUser();
                //check if user from refreshtoken and user from jwtToken are the same
                //generate new jwt token
                    newJwtToken = tokenProvider.createTokenFromUser(user);
                    return ResponseEntity.ok(new AuthenticationResponse(newJwtToken));
                    //send it in the response

            } catch (Exception c){

            }
        }catch (Exception e){
            return new ResponseEntity(new ApiResponse(false,"something wrong with authentication"),HttpStatus.BAD_REQUEST);
        }
      return new ResponseEntity(new ApiResponse(false,"there was a problem generating new JWT token"),HttpStatus.BAD_REQUEST);
    }
    @PostMapping("/logout")
    public ResponseEntity<?> logout (HttpServletRequest request){
        String refreshToken = request.getHeader("refresh-token");
    RefreshToken refreshToken1 = refreshTokenRepository.findByToken(refreshToken);
       if(refreshToken1!= null){
           refreshTokenRepository.delete(refreshToken1);
       }
       return ResponseEntity.ok(new ApiResponse(true,"succesfully logged out"));
    }

}
