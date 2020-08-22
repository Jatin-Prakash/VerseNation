package com.marketplace.versenation.models;

public enum ArtistDescription {
    RAPPER,PRODUCER,ENGINEER,SINGER,GHOSTWRITER,SONGWRITER,GUITARIST,COMPOSER,CUSTOM;

    public static boolean contains(String test) {

        for (ArtistDescription c : ArtistDescription.values()) {
            if (c.name().equals(test)) {
                return true;
            }
        }

        return false;
    }
}
