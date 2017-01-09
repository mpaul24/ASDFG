package com.manojit.paul.MuBox;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class CustomPlaylist extends Fragment {

    static Adapter adapter;

    public CustomPlaylist() {
        // Required empty public constructor
    }

    static RecyclerView rvplaylist;
    customPlaylistInterface playlistInterface;
    ImageView imageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view  = inflater.inflate(R.layout.fragment_custom_playlist, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        playlistInterface = (customPlaylistInterface) getActivity();
        rvplaylist = (RecyclerView) view.findViewById(R.id.rvplaylist);
        rvplaylist.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new Adapter();
        rvplaylist.setAdapter(adapter);
        imageView = (ImageView) view.findViewById(R.id.favouriteBackImage);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
            byte[] art =null ;
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.lo);
            if(MainActivity.favouriteList.size() > 0) {
                Picasso.with(getContext())
                        .load("file://" + MainActivity.favouriteList.get(0).getAlbumDB().getAlbumArt())
                        .error(R.drawable.lo)
                        .into(imageView);
                try {
                    mediaMetadataRetriever.setDataSource(MainActivity.favouriteList.get(0).getPath());
                    art = mediaMetadataRetriever.getEmbeddedPicture();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            if (art != null) {
                bitmap = BitmapFactory.decodeByteArray(art, 0, art.length);
            }
            Palette palette = Palette.from(bitmap).maximumColorCount(32).generate();
            int color = palette.getVibrantColor(Color.GRAY);
            if(color == Color.GRAY){
                color = palette.getLightVibrantColor(Color.GRAY);
                if(color == Color.GRAY){
                    color = palette.getDominantColor(Color.GRAY);
                }
            }


            float[] hsv = new float[3];
            Color.colorToHSV(color, hsv);
            hsv[2] *= 0.8f; // value component
            int darkcolor = Color.HSVToColor(hsv);
            //toolbar.setBackgroundColor(color);
            //getActivity().getWindow().setStatusBarColor(darkcolor);
            //getActivity().getWindow().setNavigationBarColor(darkcolor);
        }
    }

    private class Adapter extends RecyclerView.Adapter<Holder>{

        Context context;
        ArrayList<SongDB> favourite;
        public Adapter() {
            context = getContext();
            favourite = MainActivity.favouriteList;
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.custom_adapter_1,parent,false);
            Holder holder = new Holder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            SongDB currentSong = favourite.get(position);
            holder.song_title.setText(currentSong.getTitle());
            holder.song_artist.setText(currentSong.getArtist());
            Picasso.with(getContext())
                    .load("file://" + currentSong.getAlbumDB().getAlbumArt())
                    .placeholder(R.drawable.lo)
                    .resize(150, 150)
                    .centerCrop()
                    .into(holder.imageView2);
        }

        @Override
        public int getItemCount() {
            if(favourite!=null) {
                //Log.e("Manojit",""+favourite.size());
                return favourite.size();

            }
            else{
                //Log.e("Manojit","size0");
                return 0;
            }
        }
    }



    private class Holder extends RecyclerView.ViewHolder{
        ImageView imageView2;
        TextView song_title, song_artist;
        LinearLayout clickableLayout;
        public Holder(final View itemView) {
            super(itemView);

            clickableLayout = (LinearLayout) itemView.findViewById(R.id.clickableLayout);
            imageView2 = (ImageView) itemView.findViewById(R.id.artist_image);
            song_title = (TextView) itemView.findViewById(R.id.albumlist_title);
            song_artist = (TextView) itemView.findViewById(R.id.song_artist);

            clickableLayout.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int position = rvplaylist.getChildAdapterPosition(itemView);
                            playlistInterface.notOldSong();
                            playlistInterface.clickOnPlaylist(position, 4);
                        }
                    }
            );

        }
    }

    public interface customPlaylistInterface{
        void clickOnPlaylist(int position , int choice);
        void notOldSong();
    }
}
