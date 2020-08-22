package com.marketplace.versenation.repository;

import com.marketplace.versenation.models.Subscription;
import com.marketplace.versenation.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
 Subscription findByUser(User user);
}
