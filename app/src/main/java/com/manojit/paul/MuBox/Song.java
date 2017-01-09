package com.manojit.paul.MuBox;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


import java.util.ArrayList;

import xyz.danoz.recyclerviewfastscroller.vertical.VerticalRecyclerViewFastScroller;


/**
 * A simple {@link Fragment} subclass.
 */
public class Song extends Fragment {

    ListView songView;
    getItemIndex comm;
    RecyclerView recyclerView;
    RecyclerViewAdapter recyclerViewAdapter;
    VerticalRecyclerViewFastScroller fastScroller;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public Song() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //Log.e("asdf", "SONG-------onCreate");
        View v = inflater.inflate(R.layout.fragment_song, container, false);
        comm = (getItemIndex) getActivity();
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerViewAdapter = new RecyclerViewAdapter(getActivity());
        fastScroller = (VerticalRecyclerViewFastScroller) v.findViewById(R.id.fast_scroller);

        // Connect the recycler to the scroller (to let the scroller scroll the list)
        fastScroller.setRecyclerView(recyclerView);

        // Connect the scroller to the recycler (to let the recycler scroll the scroller's handle)
        recyclerView.setOnScrollListener(fastScroller.getOnScrollListener());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new onItemClickInterface() {
            @Override
            public void click(View view, int position) {
                /*comm.notOldSongSongFragment();
                comm.getIndex(position,1);*/
            }

            @Override
            public void longClick(View view, int position) {
                //Toast.makeText(getActivity(), "Item longClicked " + position, Toast.LENGTH_SHORT).show();
                //Toast.makeText(getActivity(), "vdjavjdvjavdj", Toast.LENGTH_SHORT).show();
            }
        }
        ));
        //recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //Log.e("asdf","SONG-------onCreate");
        return v;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        comm = (getItemIndex) getActivity();
        Log.e("asdf", "SONG-------onActivityCreated");


        //ArrayAdapter<String> ad = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,a);
        //CustomAdapter ad = new CustomAdapter(getActivity());
        //songView.setAdapter(ad);
        /*songView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        comm.getIndex(position);
                    }
                }
        );*/


    }


    public class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        GestureDetector gestureDetector;
        onItemClickInterface onItemClickInterface;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final onItemClickInterface onItemClickInterface) {
            this.onItemClickInterface = onItemClickInterface;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View view = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (view != null && onItemClickInterface != null) {
                        onItemClickInterface.longClick(view, recyclerView.getChildPosition(view));
                    }
                    super.onLongPress(e);
                }
            }
            );

        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View view = rv.findChildViewUnder(e.getX(), e.getY());
            if (view != null && onItemClickInterface != null && gestureDetector.onTouchEvent(e)) {
                onItemClickInterface.click(view, rv.getChildPosition(view));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    public interface onItemClickInterface {
        public void click(View view, int position);

        public void longClick(View view, int position);
    }


    public interface getItemIndex {

        public void getIndex(int data, int choice);

        public void notOldSongSongFragment();
        void SongGiveIndex(int data,int position);

        public void addtoQueue(int position);
    }

    private class RecyclerViewAdapter extends RecyclerView.Adapter<MyViewHolder>{


        LayoutInflater inflater;
        Context context;
        ArrayList<SongDB> songLists;
        int previousposition;
        final int MaxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = MaxMemory / 8;

        public RecyclerViewAdapter(Context context) {
            this.context = context;
            inflater = LayoutInflater.from(context);
            songLists = MainActivity.songList;
            //Log.e("Manojit","Hello"+songLists);
        }


        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = inflater.inflate(R.layout.custom_adapter, parent, false);
            MyViewHolder myViewHolder = new MyViewHolder(v);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            try{
            SongDB currentSong = songLists.get(position);
            holder.song_title.setText(currentSong.getTitle());
            holder.song_artist.setText(currentSong.getArtist());
            Picasso.with(context)
                    .load("file://" + currentSong.getAlbumDB().getAlbumArt())
                    .placeholder(R.drawable.lo)
                    .resize(100, 100)
                    .centerCrop()
                    .into(holder.imageView2);}catch (Exception e){
                e.printStackTrace();
                //songLists.remove(position);
            }
        /*Glide.with(context)
                .load("file://"+currentSong.getAlbumDB().getAlbumArt())
                .placeholder(R.drawable.play_icon)
                .error(R.drawable.play_icon)
                .into(holder.imageView2);*/


        }

        @Override
        public int getItemCount() {
            if (songLists != null) {
                return songLists.size();
            } else {
                return 0;
            }
        }

    }


    private class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView2, popupMenu;
        TextView song_title, song_artist;
        LinearLayout clickableLayout;

        public MyViewHolder(final View itemView) {
            super(itemView);

            clickableLayout = (LinearLayout) itemView.findViewById(R.id.clickableLayout);
            popupMenu = (ImageView) itemView.findViewById(R.id.popupMenu);
            imageView2 = (ImageView) itemView.findViewById(R.id.artist_image);
            song_title = (TextView) itemView.findViewById(R.id.albumlist_title);
            song_artist = (TextView) itemView.findViewById(R.id.song_artist);

            popupMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Log.e("Manojit","Hello");
                    // Toast.makeText(getActivity(), "Hello", Toast.LENGTH_SHORT).show();
                    final PopupMenu menu = new PopupMenu(getContext(), v);
                    menu.getMenuInflater().inflate(R.menu.popup_song,menu.getMenu());
                    menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.popup_song_play:
                                    int position = recyclerView.getChildAdapterPosition(itemView);
                                    comm.notOldSongSongFragment();
                                    comm.SongGiveIndex(position, 1);
                                    //MusicPlayer.playAll(mContext, songIDs, position, -1, TimberUtils.IdType.NA, false);
                                    break;
                                case R.id.popup_song_goto_album:
                                    int data = 0;
                                    for(int i =0;i<MainActivity.AlbumList.size();i++){
                                        if(MainActivity.songList.get(recyclerView.getChildAdapterPosition(itemView)).getAlbumID() ==
                                                MainActivity.AlbumList.get(i).getAlbumID()){
                                            data = i;
                                            break;
                                        }
                                    }
                                    Album_list album_list = new Album_list(data);
                                    ((AppCompatActivity) getActivity()).getSupportFragmentManager()
                                            .beginTransaction()
                                            //.hide(((AppCompatActivity) context).getSupportFragmentManager().findFragmentByTag("naya"))
                                            .replace(R.id.mainFragmentContainer, album_list,"hello")
                                            //.show(((AppCompatActivity) context).getSupportFragmentManager().findFragmentById(R.id.mainFragmentContainer))
                                            .addToBackStack(null)
                                            .commit();

                                    break;
                                case R.id.popup_song_goto_artist:

                                    int data1 = 0;
                                    for(int i =0;i<MainActivity.ArtistList.size();i++){
                                        if(MainActivity.songList.get(recyclerView.getChildAdapterPosition(itemView)).getArtistID() ==
                                                MainActivity.ArtistList.get(i).getArtistID()){
                                            data1 = i;
                                            break;
                                        }
                                    }

                                    Artist_List_Fragment artist_list_fragment = new Artist_List_Fragment(data1);

                                    ((AppCompatActivity) getActivity()).getSupportFragmentManager()
                                            .beginTransaction()
                                            //.hide(((AppCompatActivity) context).getSupportFragmentManager().findFragmentByTag("naya"))
                                            //.addSharedElement(artist_image, "artistart")
                                            .replace(R.id.mainFragmentContainer, artist_list_fragment,"hello")
                                            //.show(((AppCompatActivity) context).getSupportFragmentManager().findFragmentById(R.id.mainFragmentContainer))
                                            .addToBackStack(null)
                                            .commit();
                                    break;
                                case R.id.popup_song_addto_queue:
                                    comm.addtoQueue(recyclerView.getChildAdapterPosition(itemView));
                                    break;
                            }
                            return true;
                        }
                    });
                    menu.show();
                }
            });

            clickableLayout.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Toast.makeText(getActivity(), "Hello", Toast.LENGTH_SHORT).show();
                            int position = recyclerView.getChildAdapterPosition(itemView);
                            comm.notOldSongSongFragment();
                            comm.SongGiveIndex(position, 1);
                        }
                    }
            );




        }
    }
}

