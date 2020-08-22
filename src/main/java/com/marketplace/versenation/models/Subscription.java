package com.marketplace.versenation.models;

import javax.persistence.*;
import java.time.Instant;
/**
 * This is gonna be created when a customer succesfully signs up
 *
 * **/
@Entity
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String subscriptionTier;
    private String subscriptionId;
    private String customerId;
    @OneToOne
    private User user;


    public Subscription() {
    }
    public Subscription(String subscriptionTier,User user){
        this.subscriptionTier=subscriptionTier;
        this.user=user;
    }

    public Subscription(String subscriptionTier, User user,String subscriptionId,String customerId) {
        this.subscriptionTier = subscriptionTier;
        this.user = user;
        this.subscriptionId=subscriptionId;
        this.customerId=customerId;
    }

    public long getId() {
        return id;
    }

    public String getSubscriptionTier() {
        return subscriptionTier;
    }

    public void setSubscriptionTier(String subscriptionTier) {
        this.subscriptionTier = subscriptionTier;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
