package com.marketplace.versenation.repository;

import com.marketplace.versenation.models.Bid;
import com.marketplace.versenation.models.SellingItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BidRepository extends JpaRepository<Bid, Long> {

    Optional<Bid> findBySellingItem(SellingItem sellingItem);

    Boolean existsBySellingItem(SellingItem sellingItem);

}
