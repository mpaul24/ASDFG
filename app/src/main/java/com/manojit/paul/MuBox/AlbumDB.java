package com.manojit.paul.MuBox;

/**
 * Created by Manojit Paul on 08-08-2016.
 */
public class AlbumDB {
    private String Album;
    private String AlbumArt;
    private long AlbumID;



    public AlbumDB(String albumArt, String album, long albumID) {
        AlbumArt = albumArt;
        Album = album;
        AlbumID = albumID;

    }

    public String getAlbum() {
        return Album;
    }

    public String getAlbumArt() {
        return AlbumArt;
    }

    public long getAlbumID() {
        return AlbumID;
    }
}
