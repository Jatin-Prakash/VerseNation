package com.marketplace.versenation.models.Following;

import com.marketplace.versenation.models.User;
import com.marketplace.versenation.models.userTypes.CreatorAccount;

import javax.persistence.*;

@Entity
@Table(uniqueConstraints= @UniqueConstraint(columnNames={"from_user_fk", "to_creator_account_fk"}))
public class Following {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;

    @ManyToOne
    @JoinColumn(name="from_user_fk")
    private User from;

    //followed
    @ManyToOne
    @JoinColumn(name="to_creator_account_fk")
    private CreatorAccount to;

    public Following() {};

    public Following(User from,CreatorAccount to){
        this.from=from;
        this.to=to;
    }

    public long getId() {
        return id;
    }

    public User getFrom() {
        return from;
    }

    public void setFrom(User from) {
        this.from = from;
    }

    public CreatorAccount getTo() {
        return to;
    }

    public void setTo(CreatorAccount to) {
        this.to = to;
    }
}
