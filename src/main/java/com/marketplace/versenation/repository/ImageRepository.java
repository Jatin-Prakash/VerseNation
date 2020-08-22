package com.marketplace.versenation.repository;

import com.marketplace.versenation.models.Image;
import com.marketplace.versenation.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

}
