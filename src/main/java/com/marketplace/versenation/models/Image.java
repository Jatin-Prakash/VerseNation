package com.marketplace.versenation.models;

import com.marketplace.versenation.models.audit.DateAudit;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.Instant;

import java.util.Date;


@Entity
public class Image extends DateAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String path;


    public Image(String path){
        this.path=path;
    }
    public Image(){

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



    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }


    public Long getId() {
        return id;
    }
}
