package com.marketplace.versenation.models.music;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.marketplace.versenation.models.audit.DateAudit;
import com.marketplace.versenation.models.userTypes.CreatorAccount;
import org.apache.commons.lang3.RandomStringUtils;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * this is where all the music go when you upload a music and
 * is waiting for approval by one of our members
 * **/
@Entity
public class PendingMusic extends DateAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private CreatorAccount musicCreator;

    @JsonIgnore
    private String location;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private PendingAlbum pendingAlbum;

    @NotNull
    @Size(min=2,max=40)
    private String title;

    public PendingMusic(){

    }
    public PendingMusic(CreatorAccount musicCreator,String location,String title){
        this.musicCreator=musicCreator;
        this.location=location;
        this.title=title;
    }

    public Long getId() {
        return id;
    }

    public CreatorAccount getMusicCreator() {
        return musicCreator;
    }

    public void setMusicCreator(CreatorAccount musicCreator) {
        this.musicCreator = musicCreator;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public String generateUniqueFileName() {
        String filename = "";
        long millis = System.currentTimeMillis();
        String datetime = new Date().toGMTString();
        datetime = datetime.replace(" ", "");
        datetime = datetime.replace(":", "");
        String rndchars = RandomStringUtils.randomAlphanumeric(16);
        filename = rndchars + "_" + datetime + "_" + millis;
        return filename;
    }

    public PendingAlbum getPendingAlbum() {
        return pendingAlbum;
    }

    public void setPendingAlbum(PendingAlbum pendingAlbum) {
        this.pendingAlbum = pendingAlbum;
    }
}
