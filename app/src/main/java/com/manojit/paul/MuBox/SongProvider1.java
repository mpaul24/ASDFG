package com.manojit.paul.MuBox;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Manojit Paul on 08-08-2016.
 */
public class SongProvider1 {

    ArrayList<SongDB> songs = new ArrayList();
    Context context;

    public SongProvider1(Context context) {
        this.context = context;
    }

    public ArrayList<SongDB> getSongs() {
        ContentResolver musicResolver = context.getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        Cursor musicCursor = musicResolver.query(musicUri, new String[]{MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media._ID
                , MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.COMPOSER, MediaStore.Audio.Media.YEAR, MediaStore.Audio.Media.TRACK, MediaStore.Audio.Media.ARTIST_ID
        }, null, null, MediaStore.Audio.Media.TITLE);

        if (musicCursor != null && musicCursor.moveToFirst()) {
            //int value=0;
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ARTIST);
            int pathColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.DATA);
            int albumColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ALBUM);
            int albumIDColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ALBUM_ID);
            int composerColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.COMPOSER);
            int yearColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.YEAR);
            int trackColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.TRACK);
            int artistidColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ARTIST_ID);

            //add songs to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                String thisPath = musicCursor.getString(pathColumn);
                String thisAlbum = musicCursor.getString(albumColumn);
                long thisalbumID = musicCursor.getLong(albumIDColumn);
                String thisComposer = musicCursor.getString(composerColumn);
                int thisyear = musicCursor.getInt(yearColumn);
                int track = musicCursor.getInt(trackColumn);
                long thisartistID = musicCursor.getLong(artistidColumn);
                //Log.e("asdfghjkl",thisartistID+"\n"+thisArtist);

                if (thisPath.endsWith(".mp3") || thisPath.endsWith(".m4p") || thisPath.endsWith(".wav")) {
                    SongDB db = new SongDB(thisId, thisTitle, thisAlbum, thisArtist, thisPath, thisalbumID, thisComposer, thisyear, track, thisartistID);
                    if(db != null) {
                        songs.add(db);
                    }
                } else {

                }
                //cursor.moveToNext();
            }
            while (musicCursor.moveToNext());
        }
        if (musicCursor != null) {
            musicCursor.close();
        }
        //Log.e("HI"," "+musicCursor.isClosed());
        if(songs!=null) {
            Collections.sort(songs, new Comparator<SongDB>() {
                @Override
                public int compare(SongDB lhs, SongDB rhs) {
                    return lhs.getTitle().compareToIgnoreCase(rhs.getTitle());
                }
            });
        }

        return songs;

    }

    public ArrayList<AlbumDB> getAlbum() {

        ArrayList<AlbumDB> songs = new ArrayList<>();
        ContentResolver musicResolver = context.getContentResolver();
        Cursor cursor = musicResolver.query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Albums.ALBUM, MediaStore.Audio.Albums.ALBUM_ART, MediaStore.Audio.Albums._ID},
                null,
                null,
                MediaStore.Audio.Albums.ALBUM);
        if (cursor != null && cursor.moveToFirst()) {
            int AlbumName = cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM);
            int AlbumArtPath = cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART);
            int AlbumID = cursor.getColumnIndex(MediaStore.Audio.Albums._ID);

            do {
                String thisAlbumArt = cursor.getString(AlbumArtPath);
                String thisAlbum = cursor.getString(AlbumName);
                long thisAlbumID = cursor.getLong(AlbumID);
                // Log.e("asdfghjkl",thisAlbum+ "/n" + thisAlbumID);
                AlbumDB db = new AlbumDB(thisAlbumArt, thisAlbum, thisAlbumID);
                if(db!=null) {
                    songs.add(db);
                }

            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return songs;
    }

    public ArrayList<ArtistDB> getArtistList() {
        Log.e("asdfghjkl", "------------------------------------------------------------");
        ArrayList<ArtistDB> artistList = new ArrayList<>();
        ContentResolver musicResolver = context.getContentResolver();

        Cursor cursor = musicResolver.query(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Artists.ARTIST, MediaStore.Audio.Artists._ID, MediaStore.Audio.Artists.NUMBER_OF_TRACKS, MediaStore.Audio.Artists.NUMBER_OF_ALBUMS},
                null,
                null,
                null);
        if (cursor != null && cursor.moveToFirst()) {
            //int ArtistALBUMID = cursor.getColumnIndex(MediaStore.Audio.Artists.Albums.ALBUM_ID);
            int ArtistName = cursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST);
            int ArtistID = cursor.getColumnIndex(MediaStore.Audio.Artists._ID);
            int ArtistNOT = cursor.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_TRACKS);
            int ArtistNOA = cursor.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS);

            do {

                Long Artist_ID = cursor.getLong(ArtistID);
                String Artist_NOT = cursor.getString(ArtistNOT);
                String Artist_NOA = cursor.getString(ArtistNOA);
                String Artist_Name = cursor.getString(ArtistName);
                //Log.e("asdfghjkl",Artist_ID+"\n"+Artist_Name);
                ArtistDB db = new ArtistDB(Artist_ID, Artist_NOT, Artist_NOA, Artist_Name);
                if(db!=null) {
                    artistList.add(db);
                }

            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }

        return artistList;
    }



    public ArrayList<SongDB> getAlbumList(ArrayList<SongDB> songLists) {
        ArrayList<SongDB> albumArrayList = null;
        int flag = 0;
        boolean add = false;
        if (songLists == null) {
            //Log.e("asdf","null");
        }

        for (int i = 0; i < songLists.size(); i++) {
            if (albumArrayList == null) {
                albumArrayList = new ArrayList<>();
                albumArrayList.add(songLists.get(i));
            } else {
                for (int j = 0; j < albumArrayList.size(); j++) {
                    add = true;
                    if (albumArrayList.get(j).getAlbumID() == (songLists.get(i).getAlbumID())) {
                        flag++;
                        break;
                    }
                }
                if (add == true) {
                    if (flag == 0) {
                        albumArrayList.add(songLists.get(i));
                        flag = 0;
                    } else {
                        flag = 0;
                    }
                }

            }
        }
        if(albumArrayList!=null) {
            Collections.sort(albumArrayList, new Comparator<SongDB>() {
                @Override
                public int compare(SongDB lhs, SongDB rhs) {
                    return lhs.getAlbum().compareToIgnoreCase(rhs.getAlbum());
                }
            });
        }

        return albumArrayList;
    }

    public ArrayList<SongDB> getArtistList(ArrayList<SongDB> songLists) {
        ArrayList<SongDB> artistArraylist = null;
        int flag = 0;
        boolean add = false;
        if (songLists == null) {
            //Log.e("asdf","null");
        }

        for (int i = 0; i < songLists.size(); i++) {
            if (artistArraylist == null) {
                artistArraylist = new ArrayList<>();
                artistArraylist.add(songLists.get(i));
            } else {
                for (int j = 0; j < artistArraylist.size(); j++) {
                    add = true;
                    if (artistArraylist.get(j).getArtistID() == (songLists.get(i).getArtistID())) {
                        flag++;
                        break;
                    }
                }
                if (add == true) {
                    if (flag == 0) {
                        artistArraylist.add(songLists.get(i));
                        flag = 0;
                    } else {
                        flag = 0;
                    }
                }

            }
        }
        if(artistArraylist!=null) {
            Collections.sort(artistArraylist, new Comparator<SongDB>() {
                @Override
                public int compare(SongDB lhs, SongDB rhs) {
                    return lhs.getArtist().compareToIgnoreCase(rhs.getArtist());
                }
            });
        }

        return artistArraylist;
    }

    public ArrayList<SongDB> getArtistSongList(int data , ArrayList<SongDB> fullList, ArrayList<SongDB> artistList) {
        ArrayList<SongDB> artistSongList = new ArrayList<>();
        SongDB curr = artistList.get(data);
        for (int i = 0; i < fullList.size(); i++) {
            if (fullList.get(i).getArtist().equalsIgnoreCase(curr.getArtist())) {
                artistSongList.add(fullList.get(i));
            }
        }
        return artistSongList;
    }

    public ArrayList<SongDB> getArtistAlbum(ArrayList<SongDB> fullList,SongDB curr)
    {
        ArrayList<SongDB> artistSongList = new ArrayList<>();
        //SongDB curr = artistList.get(data);
        boolean flag = true;
        for (int i = 0; i < fullList.size(); i++) {
            if (fullList.get(i).getArtistID() == curr.getArtistID()) {
                Log.e("asdfghjkl","Match Found!");
                flag = true;
                for(int j=0;j<artistSongList.size();j++){
                    if(artistSongList.get(j).getAlbumID() == (fullList.get(i).getAlbumID()))
                    {
                        flag = false;
                    }
                }
                if(flag) {
                    artistSongList.add(fullList.get(i));
                }
            }
        }
        return artistSongList;
    }

    public ArrayList<Playlist> getPlaylist(Context context)
    {
        ArrayList<Playlist> playlists = new ArrayList<>();
        ContentResolver resolver = context.getContentResolver();
        Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;

        Cursor playlistCursor = resolver.query(uri,null,null,null,null,null);

        if(playlistCursor!=null && playlistCursor.moveToFirst()){
            int _id = playlistCursor.getColumnIndex(MediaStore.Audio.Playlists._ID);
            int name = playlistCursor.getColumnIndex(MediaStore.Audio.Playlists.NAME);
            /*int data = playlistCursor.getColumnIndex(MediaStore.Audio.Playlists.DATA);
            int _count = playlistCursor.getColumnIndex(MediaStore.Audio.Playlists._COUNT);
            int data_added = playlistCursor.getColumnIndex(MediaStore.Audio.Playlists.DATE_ADDED);
            int data_modified = playlistCursor.getColumnIndex(MediaStore.Audio.Playlists.DATE_MODIFIED);*/

            do{
                String _ID = playlistCursor.getString(_id);
                String NAME = playlistCursor.getString(name);
                /*String DATA = playlistCursor.getString(data);
                String _COUNT = playlistCursor.getString(_count);
                String DATA_ADDED = playlistCursor.getString(data_added);
                String DATA_MODIFIED = playlistCursor.getString(data_modified);*/

                //Log.e("Manojit","\n"+_ID+"\n"+NAME+"\n");//DATA+"\n"+_COUNT+"\n"+DATA_ADDED+"\n"+DATA_MODIFIED);
                playlists.add(new Playlist(_ID,NAME));

            }while(playlistCursor.moveToNext());

        }
        if(!playlistCursor.isClosed())
        playlistCursor.close();

        return playlists;
    }

    public void getPlaylistSong (String _idPlaylist)
    {
        long id = Long.parseLong(_idPlaylist);
        //ArrayList<Playlist> playlists = new ArrayList<>();
        ContentResolver resolver = context.getContentResolver();
        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external",id);

        Cursor playlistCursor = resolver.query(uri,null,null,null,null,null);

        if(playlistCursor!=null && playlistCursor.moveToFirst()){
            int _id = playlistCursor.getColumnIndex(MediaStore.Audio.Playlists.Members.AUDIO_ID);
            int name = playlistCursor.getColumnIndex(MediaStore.Audio.Playlists.DATA);
            int data = playlistCursor.getColumnIndex(MediaStore.Audio.Playlists.Members.ARTIST);
            int _count = playlistCursor.getColumnIndex(MediaStore.Audio.Playlists.Members.TITLE);
            int data_added = playlistCursor.getColumnIndex(MediaStore.Audio.Playlists.Members._ID);
            int data_modified = playlistCursor.getColumnIndex(MediaStore.Audio.Playlists.Members.ALBUM);

            do{
                String _ID = playlistCursor.getString(_id);
                //String NAME = playlistCursor.getString(name);
                String DATA = playlistCursor.getString(data);
                String _COUNT = playlistCursor.getString(_count);
                String DATA_ADDED = playlistCursor.getString(data_added);
                String DATA_MODIFIED = playlistCursor.getString(data_modified);

                Log.e("Manojit","\n"+_ID+"\n"+name+"\n"+DATA+"\n"+_COUNT+"\n"+DATA_ADDED+"\n"+DATA_MODIFIED);
                //playlists.add(new Playlist(_ID,NAME));

            }while(playlistCursor.moveToNext());

        }
        if(!playlistCursor.isClosed())
            playlistCursor.close();

        //return playlists;
    }
}

