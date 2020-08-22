package com.marketplace.versenation.repository;

import com.marketplace.versenation.models.Refresh.RefreshToken;
import com.marketplace.versenation.models.Subscription;
import com.marketplace.versenation.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
   RefreshToken findByToken(String token);

   RefreshToken findByUser(User user);
}
