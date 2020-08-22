package com.marketplace.versenation.repository;

import com.marketplace.versenation.models.Bidding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BiddingRepository extends JpaRepository<Bidding, Long>  {

}
