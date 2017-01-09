package com.manojit.paul.MuBox;

/**
 * Created by Manojit Paul on 29-08-2016.
 */
public class Playlist {
    private String _id;
    private String name;

    public Playlist(String _id, String name) {
        this._id = _id;
        this.name = name;
    }

    public String get_id() {
        return _id;
    }

    public String getName() {
        return name;
    }
}
