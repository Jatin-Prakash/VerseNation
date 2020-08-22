package com.marketplace.versenation.repository;

import com.marketplace.versenation.models.music.PendingAlbum;
import com.marketplace.versenation.models.music.PendingMusic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PendingAlbumRepository extends JpaRepository<PendingAlbum, Long> {
}
