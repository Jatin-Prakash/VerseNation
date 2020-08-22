package com.marketplace.versenation.models.Refresh;

import com.marketplace.versenation.models.User;
import org.apache.commons.lang3.RandomStringUtils;

import javax.persistence.*;

@Entity
@Table(uniqueConstraints = {
@UniqueConstraint(columnNames = {
        "token"
})})
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    @PrimaryKeyJoinColumn
    @OneToOne
    private User user;

    public RefreshToken() {
    }

    public RefreshToken(User user) {
        this.user = user;
    }

    public void generateRandomToken(){
      token = RandomStringUtils.randomAlphanumeric(80);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
