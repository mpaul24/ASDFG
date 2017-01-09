package com.manojit.paul.MuBox;


import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;


/**
 * A simple {@link Fragment} subclass.
 */
public class Playlist_Fragment extends Fragment {


    public Playlist_Fragment() {
        // Required empty public constructor
    }

    static RecyclerView playListRecyclerView;
    Adaper adaper;
    MyViewHolder myViewHolder;
    playListChangeSong playListChangeSong;
    RecyclerView.ViewHolder viewHolder ;
    Context mContext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.playlist_fragment, container, false);
        playListChangeSong = (Playlist_Fragment.playListChangeSong) getActivity();
        playListRecyclerView = (RecyclerView) view.findViewById(R.id.playListRecyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        playListRecyclerView.setLayoutManager(layoutManager);
        adaper = new Adaper(getActivity());
        playListRecyclerView.setAdapter(adaper);
        //this gives a scroll effect and reach the position
        // playListRecyclerView.smoothScrollToPosition(MainActivity.songIndex);

        //this dont give the scrolling effect and directly reach the position
        layoutManager.scrollToPosition(MainActivity.songIndex);



        playListRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), playListRecyclerView, new onItemClickInterface() {
            @Override
            public void click(View view, int position) {
                playListChangeSong.notOldSong();
                playListChangeSong.changeSongFromPlaylist(position);
            }
        }
        ));
        //MainActivity.slidingUpPanelLayout.setScrollableViewHelper(new NestedScrollableViewHelper(playListRecyclerView));
        return view;
    }

    private class Adaper extends RecyclerView.Adapter<MyViewHolder> implements  InterfacePlaylist.OnInterfacePlaylistListener,
            SimpleItemTouchHelperCallback.ItemTouchHelperAdapter{

        private LayoutInflater inflater;
        private Context context;
        ArrayList<SongDB> playListSongs;

        public Adaper(Context context) {
            this.context = context;
            SimpleItemTouchHelperCallback callback = new SimpleItemTouchHelperCallback(this);
            ItemTouchHelper touchHelper  = new ItemTouchHelper(callback);
            touchHelper.attachToRecyclerView(playListRecyclerView);
            inflater = LayoutInflater.from(context);
            playListSongs = MainActivity.songs;
            InterfacePlaylist.getInstance().setListener(this);
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.custom_adapter_1, parent, false);
            myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            viewHolder = holder;
            holder.song_title.setTypeface(null,Typeface.NORMAL);
            holder.song_artist.setTypeface(null,Typeface.NORMAL);
            TypedValue typedValue = new TypedValue();
            mContext.getTheme().resolveAttribute(R.attr.darktextColor,typedValue,true);
            int color = typedValue.data;
            holder.song_title.setTextColor(color);
            holder.song_artist.setTextColor(color);
            SongDB currentSong = playListSongs.get(position);
            holder.song_title.setText(currentSong.getTitle());
            holder.song_artist.setText(currentSong.getArtist());

            Picasso.with(context)
                    .load("file://" + currentSong.getAlbumDB().getAlbumArt())
                    .placeholder(R.drawable.lo)
                    .error(R.drawable.lo)
                    .centerCrop()
                    .resize(150,150)
                    .into(holder.imageView2);
            int pos = MainActivity.songIndex;

            if(pos == position)
            {
                mContext.getTheme().resolveAttribute(R.attr.colorAccent,typedValue,true);
                int color1 = typedValue.data;
                holder.song_title.setTypeface(null,Typeface.BOLD);
                holder.song_artist.setTypeface(null,Typeface.BOLD);
                holder.song_title.setTextColor(color1);
                holder.song_artist.setTextColor(color1);
            }


        }


        @Override
        public int getItemCount() {
            int size = 0;
            if(playListSongs!=null) {
                return playListSongs.size();
            }else{
                return size;
            }
        }


        @Override
        public void stateChanged() {
            int position = InterfacePlaylist.getInstance().getState();
            adaper.notifyDataSetChanged();
            notifyItemChanged(position);
        }
        boolean f = false;

        @Override
        public boolean onItemMove(int fromPosition, int toPosition) {
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(playListSongs, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(playListSongs, i, i - 1);
                }
            }
            notifyItemMoved(fromPosition, toPosition);
            if(MainActivity.songIndex == fromPosition) {
                MainActivity.songIndex = toPosition;
            }
            else if(MainActivity.songIndex < fromPosition && toPosition <= MainActivity.songIndex){
                MainActivity.songIndex = MainActivity.songIndex + 1;
                f = true;
            }
            else if(MainActivity.songIndex > fromPosition && toPosition >= MainActivity.songIndex){
                MainActivity.songIndex = MainActivity.songIndex - 1;
                f = true;
            }
            else if(MainActivity.songIndex < fromPosition &&  MainActivity.songIndex < toPosition){

            }
            else if(MainActivity.songIndex > fromPosition &&  MainActivity.songIndex > toPosition){

            }
            else{
                if(f){
                    MainActivity.songIndex = MainActivity.songIndex +1;

                }
            }
                playListChangeSong.listModified(true);
            return true;
        }

        @Override
        public void onItemDismiss(int position) {
            playListSongs.remove(position);
            notifyItemRemoved(position);
            playListChangeSong.listModified(true);
            playListChangeSong.itemDismissed();
            if(MainActivity.songIndex == position) {
                if(MainActivity.songs.size()!=0) {
                    if (position == (MainActivity.songs.size())) {
                        playListChangeSong.notOldSong();
                        playListChangeSong.changeSongFromPlaylist(position - 1);
                        playListChangeSong.changePlaylistColor();
                    } else {
                        playListChangeSong.notOldSong();
                        playListChangeSong.changeSongFromPlaylist(position);
                        playListChangeSong.changePlaylistColor();
                    }
                }else{
                    playListChangeSong.resetPlayer();
                }
            }else{
                if(position < MainActivity.songIndex)
                MainActivity.songIndex = position;
            }
            Toast.makeText(context, "Song Removed !!", Toast.LENGTH_SHORT).show();

        }
    }



    private class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView2;
        TextView song_title, song_artist;

        public MyViewHolder(final View itemView) {
            super(itemView);

            imageView2 = (ImageView) itemView.findViewById(R.id.artist_image);
            song_title = (TextView) itemView.findViewById(R.id.albumlist_title);
            song_artist = (TextView) itemView.findViewById(R.id.song_artist);
            song_title.setTypeface(null,Typeface.NORMAL);
            song_artist.setTypeface(null,Typeface.NORMAL);
            TypedValue typedValue = new TypedValue();
            mContext.getTheme().resolveAttribute(R.attr.darktextColor,typedValue,true);
            int color = typedValue.data;
            song_title.setTextColor(color);
            song_artist.setTextColor(color);

            itemView.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            TypedValue typedValue = new TypedValue();
                            mContext.getTheme().resolveAttribute(R.attr.colorAccent,typedValue,true);
                            int color1 = typedValue.data;
                            song_artist.setTypeface(null,Typeface.BOLD);
                            song_artist.setTextColor(color1);
                            song_title.setTypeface(null,Typeface.BOLD);
                            song_title.setTextColor(color1);
                            adaper.notifyDataSetChanged();
                        }
                    }
            );
        }

    }

    public interface playListChangeSong {
        void notOldSong();
        void changeSongFromPlaylist(int position);
        void listModified(boolean b);
        void resetPlayer();
        void itemDismissed();
        void changePlaylistColor();
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
                        //onItemClickInterface.longClick(view,recyclerView.getChildPosition(view));
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
        void click(View view, int position);
    }


}
