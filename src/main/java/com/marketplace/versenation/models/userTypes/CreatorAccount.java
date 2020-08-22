package com.marketplace.versenation.models.userTypes;

import com.marketplace.versenation.models.ArtistDescription;
import com.marketplace.versenation.models.Following.Following;
import com.marketplace.versenation.models.User;
import com.marketplace.versenation.repository.CreatorRepository;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@Table(name="Creator_table")
public class CreatorAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private User user;
    //tiers
    public enum AccountTier{
        FREE,
        BIGBREAK, //upto a hundered people join room closes
        POWERHOUSE //upto 500 people
    }
    @Enumerated(EnumType.ORDINAL)
    private ArtistDescription artistDescription;
    @Enumerated(EnumType.ORDINAL)
    private AccountTier accountTier;

    @Size(max=400)
    private String bio;


    //followers

    @OneToMany(mappedBy="to")
    private List<Following> followers;

    public CreatorAccount(){
       // super.setUserType("CREATOR");
        accountTier = AccountTier.FREE;
    }

    public String getAccountTier() {
        return accountTier.toString();
    }

    public void setAccountTier(AccountTier accountTier) {
        this.accountTier = accountTier;
    }
    public void upgradeAccount(String newTier){
        try{
            this.accountTier = AccountTier.valueOf(newTier);
        }catch(Exception e){
            System.out.println("couldn't upgrade account to "+newTier);
        }
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public ArtistDescription getArtistDescription() {
        return artistDescription;
    }

    public void setArtistDescription(ArtistDescription artistDescription) {
        this.artistDescription = artistDescription;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
