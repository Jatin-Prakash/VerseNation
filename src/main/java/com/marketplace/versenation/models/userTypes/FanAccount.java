package com.marketplace.versenation.models.userTypes;

import com.marketplace.versenation.models.User;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Table(name="Fan_table")
public class FanAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int points;
    @OneToOne
    private User user;

    public enum AccountTier{
        FREE,
        BASIC,
        STUDENT,
        FAMILY,
        DIEHARD
    }
    @Enumerated(EnumType.ORDINAL)
    private AccountTier accountTier;

    public String getAccountTier() {
        return accountTier.toString();
    }
    public void setAccountTier(FanAccount.AccountTier accountTier) {
        this.accountTier = accountTier;
    }

    public void upgradeAccount(String newTier){
        try{
            this.accountTier = FanAccount.AccountTier.valueOf(newTier);
        }catch(Exception e){
            System.out.println("couldn't upgrade account to "+newTier);
        }
    }

    public FanAccount(){

        accountTier = AccountTier.FREE;
        points = 0;
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

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
    public void addPoints(int addPoints){
        this.points += addPoints;
    }

}
