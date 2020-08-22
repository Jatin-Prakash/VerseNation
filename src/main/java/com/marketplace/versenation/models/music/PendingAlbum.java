package com.marketplace.versenation.models.music;

import com.marketplace.versenation.models.audit.DateAudit;

import javax.persistence.*;
import java.util.List;

@Entity
public class PendingAlbum extends DateAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany
    private List<PendingMusic> pendingSongs;

    private String albumName;
    public  PendingAlbum(){

    }

    public PendingAlbum(String albumName,List<PendingMusic> pendingSongs) {
      this.pendingSongs=pendingSongs;
      this.albumName=albumName;
    }

    public List<PendingMusic> getPendingSongs() {
        return pendingSongs;
    }

    public void setPendingSongs(List<PendingMusic> pendingSongs) {
        this.pendingSongs = pendingSongs;
    }

    public void addSongToAlbum(PendingMusic pendingMusic){
        this.pendingSongs.add(pendingMusic);
    }

    public Long getId() {
        return id;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }
}
