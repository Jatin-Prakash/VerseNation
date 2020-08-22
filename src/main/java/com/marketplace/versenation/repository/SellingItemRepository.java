package com.marketplace.versenation.repository;

import com.marketplace.versenation.models.SellingItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SellingItemRepository extends JpaRepository<SellingItem, Long> {

}
