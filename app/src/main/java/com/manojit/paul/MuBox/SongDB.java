package com.manojit.paul.MuBox;

/**
 * Created by Manojit Paul on 08-08-2016.
 */
public class SongDB {

    private long id;
    private String title;
    private String album;
    private String artist;
    private String path;
    private long albumID;
    private String composer;
    private int year;
    private int trackNumber;
    private long artistID;
    private AlbumDB albumDB;
    private ArtistDB artistDB;
    private int liked;



    public SongDB(long id, String title, String album, String artist, String path, long albumID, String composer, int year, int trackNumber, long artistID) {
        this.id = id;
        this.title = title;
        this.album = album;
        this.artist = artist;
        this.path = path;
        this.albumID = albumID;
        this.composer = composer;
        this.year = year;
        this.trackNumber = trackNumber;
        this.artistID = artistID;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAlbum() {
        return album;
    }

    public String getArtist() {
        return artist;
    }

    public String getPath() {
        return path;
    }

    public long getAlbumID() {
        return albumID;
    }

    public String getComposer() {
        return composer;
    }

    public int getYear() {
        return year;
    }

    public int getTrackNumber() {
        return trackNumber;
    }

    public long getArtistID() {
        return artistID;
    }

    public ArtistDB getArtistDB() {
        return artistDB;
    }

    public void setArtistDB(ArtistDB artistDB) {
        this.artistDB = artistDB;
    }

    public AlbumDB getAlbumDB() {
        return albumDB;
    }

    public void setAlbumDB(AlbumDB albumDB) {
        this.albumDB = albumDB;
    }

    public int getLiked() {
        return liked;
    }

    public void setLiked(int liked) {
        this.liked = liked;
    }
}
