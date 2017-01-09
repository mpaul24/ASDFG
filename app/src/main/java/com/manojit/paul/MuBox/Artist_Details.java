package com.manojit.paul.MuBox;

/**
 * Created by M.S.PAUL on 15-07-2016.
 */
public class Artist_Details {

    private String Artist_Album;
    private String Artist_AlbumArt;
    private long Artist_AlbumID;

    public long getArtist_AlbumID() {
        return Artist_AlbumID;
    }

    public void setArtist_AlbumID(long artist_AlbumID) {
        Artist_AlbumID = artist_AlbumID;
    }

    public String getArtist_Album() {
        return Artist_Album;
    }

    public void setArtist_Album(String artist_Album) {
        Artist_Album = artist_Album;
    }

    public String getArtist_AlbumArt() {
        return Artist_AlbumArt;
    }

    public void setArtist_AlbumArt(String artist_AlbumArt) {
        Artist_AlbumArt = artist_AlbumArt;
    }
}
