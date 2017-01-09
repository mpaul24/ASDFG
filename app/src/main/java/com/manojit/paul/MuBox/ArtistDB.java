package com.manojit.paul.MuBox;

/**
 * Created by Manojit Paul on 08-08-2016.
 */
public class ArtistDB {
    private long Artist_ID;
    private String Artist_NOT;
    private String Artist_NOA;
    private String Artist_Name;
    private SongDB songDB;

    public ArtistDB(long artist_ID, String artist_NOT, String artist_NOA, String artist_Name) {
        Artist_ID = artist_ID;
        Artist_NOT = artist_NOT;
        Artist_NOA = artist_NOA;
        Artist_Name = artist_Name;
    }

    public long getArtist_ID() {
        return Artist_ID;
    }

    public String getArtist_NOT() {
        return Artist_NOT;
    }

    public String getArtist_NOA() {
        return Artist_NOA;
    }

    public String getArtist_Name() {
        return Artist_Name;
    }

    public SongDB getSongDB() {
        return songDB;
    }

    public void setSongDB(SongDB songDB) {
        this.songDB = songDB;
    }
}
