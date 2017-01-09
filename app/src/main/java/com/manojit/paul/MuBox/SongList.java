package com.manojit.paul.MuBox;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by M.S.PAUL on 15-06-2016.
 */
public class SongList implements Serializable {
    private long id;
    private String title;
    private String album;
    private String artist;
    private String path;
    private String albumArt;
    private long albumID;
    private String composer;
    private int year;
    private int trackNumber;
    private ArrayList<Artist_Details> artist_details;

    public ArrayList<Artist_Details> getArtist_details() {
        return artist_details;
    }

    public String getArtist_NOA() {
        return Artist_NOA;
    }

    public long getArtist_ID() {
        return Artist_ID;
    }

    public String getArtist_NOT() {
        return Artist_NOT;
    }


    private long Artist_ID;
    private String Artist_NOT;
    private String Artist_NOA;
    private String Artist_Name;


    public SongList(long id, String title, String album, String artist, String path,
                    String albumArt, int year, int trackNumber, String composer, long albumID) {

        this.id = id;
        this.title = title;
        this.album = album;
        this.artist = artist;
        this.path = path;
        this.albumArt = albumArt;
        this.year = year;
        this.trackNumber = trackNumber;
        this.composer = composer;
        this.albumID = albumID;
    }

    public String getArtist_Name() {
        return Artist_Name;
    }


    public SongList(String Artist_Name, long artist_ID, String artist_NOA, String artist_NOT, ArrayList<Artist_Details> artist_details) {

        this.Artist_Name = Artist_Name;
        this.artist_details = artist_details;

        Artist_ID = artist_ID;
        Artist_NOA = artist_NOA;
        Artist_NOT = artist_NOT;
    }

    public long getAlbumID() {
        return albumID;
    }

    public String getComposer() {
        return composer;
    }

    public long getYear() {
        return year;
    }

    public long getTrackNumber() {
        return trackNumber;
    }

    public String getAlbum() {
        return album;
    }


    public String getArtist() {
        return artist;
    }


    public String getAlbumArt() {
        return albumArt;
    }


    public String getTitle() {
        return title;
    }


    public long getId() {
        return id;
    }

    public String getPath() {
        return path;
    }

}