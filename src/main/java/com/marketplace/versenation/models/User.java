package com.marketplace.versenation.models;

import com.marketplace.versenation.models.Following.Following;
import com.marketplace.versenation.models.audit.DateAudit;
import com.marketplace.versenation.models.userTypes.CreatorAccount;
import com.marketplace.versenation.models.userTypes.FanAccount;

import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = {
                "username"
        }),
        @UniqueConstraint(columnNames = {
                "email"
        })
})
public class User extends DateAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 40)
    private String name;
    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    @NotBlank
    @Size(max = 15)
    private String username;

    @NaturalId
    @NotBlank
    @Size(max = 40)
    @Email
    private String email;

    private String imageUrl;

    @Column(nullable = false)
    private Boolean emailVerified = false;

    @NotNull
    @Enumerated(EnumType.STRING)
    private AuthProvider provider;

    @NotBlank
    @Size(max = 100)
    private String password;

    private String providerId;
    //profilepictures
    @OneToMany
    @JoinColumn(name="profile_pics")
    private List<Image> profilePictures= new ArrayList<>();
    //coverpictures
    @OneToMany
    @JoinColumn(name="cover_pics")
    private List<Image> coverPictures = new ArrayList<>();

    @OneToMany(mappedBy="from")
    private List<Following> following;

    @Where(clause = "accountType = 'CREATOR'")
    @OneToOne(cascade = {CascadeType.ALL})
    @JoinColumn
    private CreatorAccount creatorAccount;

   @Where(clause = "accountType = 'FAN'")
    @OneToOne(cascade = {CascadeType.ALL})
    @JoinColumn
    private FanAccount fanAccount;

    public User() {

    }

    public User(String name, String username, String email, String password,AccountType accountType) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
        this.accountType=accountType;

    }
    public enum AccountType{
        CREATOR,FAN
    }
    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public CreatorAccount getCreatorAccount() {
        return creatorAccount;
    }

    public void setCreatorAccount(CreatorAccount creatorAccount) {
        this.creatorAccount = creatorAccount;
    }

    public FanAccount getFanAccount() {
        return fanAccount;
    }

    public void setFanAccount(FanAccount fanAccount) {
        this.fanAccount = fanAccount;
    }


    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Boolean getEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public AuthProvider getProvider() {
        return provider;
    }

    public void setProvider(AuthProvider provider) {
        this.provider = provider;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }
    //getter and setter for profile pics and cover pics

    public List<Image> getProfilePictures() {
        return profilePictures;
    }

    public void setProfilePictures(List<Image> profilePictures) {
        this.profilePictures = profilePictures;
    }
    public void addProfilePicture(Image image){
        this.profilePictures.add(image);
    }
    public void addCoverPicture(Image image){
        this.coverPictures.add(image);
    }

    public List<Image> getCoverPictures() {
        return coverPictures;
    }

    public void setCoverPictures(List<Image> coverPictures) {
        this.coverPictures = coverPictures;
    }

}
