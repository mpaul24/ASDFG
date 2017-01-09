package com.manojit.paul.MuBox;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class Artist_List_Fragment extends Fragment {

    RecyclerView grid_Artist_RecylerView, list_Artist_RecyclerView;
    int data;
    ArrayList<SongDB> fullList;
    ArrayList<SongDB> artistList;
    ArrayList<SongDB> artistSongList;
    ArrayList<SongDB> albumList;
    ArrayList<SongDB> artistAlbumList;
    ArtistListAlbumFragmentChange artistListAlbumFragmentChange;
    ImageView imageView,imageView2;
    SongDB current;


    public Artist_List_Fragment(int data) {
        this.data = data;
        fullList = MainActivity.songList;
        albumList = MainActivity.AlbumList;
        artistList = MainActivity.ArtistList;
        SongProvider1 provider1 = new SongProvider1(getActivity());
        artistSongList = provider1.getArtistSongList(data,fullList, artistList);
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_artist_list, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        artistListAlbumFragmentChange = (ArtistListAlbumFragmentChange) getActivity();

        current = artistList.get(data);
        final Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setTitle(current.getArtist());
        //toolbar.setTitleTextColor(Color.GREEN);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        //((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("A");

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitleEnabled(false);


        imageView = (ImageView) view.findViewById(R.id.backdrop);
        imageView2 = (ImageView) view.findViewById(R.id.fak);
        /*Glide.with(this)
                .load("file://" + current.getAlbumDB().getAlbumArt())
                .error(R.drawable.play_icon)
                .centerCrop()
                .crossFade()
                .into(imageView);*/
        try {
            Picasso.with(getContext())
                    .load("file://" + current.getAlbumDB().getAlbumArt())
                    .placeholder(R.drawable.lo)
                    //.placeholder(R.drawable.play_icon)
                    .error(R.drawable.lo)
                    .into(imageView);
        }catch(Exception e){
            e.printStackTrace();
        }

        imageView2.setVisibility(View.GONE);


        /*getActivity().runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(500);
                            Glide.with(getActivity())
                                    .load("file://" + current.getAlbumDB().getAlbumArt())
                                    .error(R.drawable.play_icon)
                                    .centerCrop()
                                    .crossFade()
                                    .into(imageView2);
                            imageView2.setVisibility(View.VISIBLE);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );*/

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
            byte[] art =null ;
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.lo);
            if(MainActivity.ArtistList.get(data).getPath() != null) {
                try {
                    mediaMetadataRetriever.setDataSource(MainActivity.ArtistList.get(data).getPath());
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
            collapsingToolbar.setBackgroundColor(color);
            collapsingToolbar.setContentScrimColor(color);
            getActivity().getWindow().setStatusBarColor(darkcolor);
            //getActivity().getWindow().setNavigationBarColor(darkcolor);
        }


        grid_Artist_RecylerView = (RecyclerView) view.findViewById(R.id.grid_Artist_RecylerView);
        list_Artist_RecyclerView = (RecyclerView) view.findViewById(R.id.list_Artist_RecyclerView);
        grid_Artist_RecylerView.setLayoutManager(new GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false));
        grid_Artist_RecylerView.setAdapter(new GridAdapter(getActivity()));
        list_Artist_RecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        list_Artist_RecyclerView.setAdapter(new ListAdapter(getActivity()));
        grid_Artist_RecylerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(),grid_Artist_RecylerView,new onItemClickInterface()
        {

            @Override
            public void click(View view, int position) {
                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    int index = getAlbumListSongIndex(position);
                    artistListAlbumFragmentChange.artistAlbumFragmentChange(index);
                }
            }

            @Override
            public void longClick(View view, int position) {

            }
        }));

        list_Artist_RecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), list_Artist_RecyclerView, new onItemClickInterface() {
            @Override
            public void click(View view, int position) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = preferences.edit();
                Gson gson = new Gson();
                String song = gson.toJson(artistSongList);
                editor.putString("ArtistSongList", song);
                editor.commit();
                //int index = getPositionInMainList(albumList.get(position).getTitle());
                artistListAlbumFragmentChange.notOldSongSongFragment();
                artistListAlbumFragmentChange.artistListSongsList(position);
            }

            @Override
            public void longClick(View view, int position) {

            }
        }));

        grid_Artist_RecylerView.setNestedScrollingEnabled(false);
        list_Artist_RecyclerView.setNestedScrollingEnabled(false);
    }

    private void load() {

    }

    public class GridAdapter extends RecyclerView.Adapter<GridViewHolder> {

        Context context;
        LayoutInflater inflater;
        ArrayList<SongDB> artistList = MainActivity.ArtistList;
        SongDB curr = artistList.get(data);

        public GridAdapter(Context context) {
            this.context = context;
            inflater = LayoutInflater.from(context);
            SongProvider1 provider1 = new SongProvider1(getActivity());
            artistAlbumList = new ArrayList<>();
            artistAlbumList = provider1.getArtistAlbum(fullList,curr);
        }

        @Override
        public GridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.custom_artist_album, parent, false);
            GridViewHolder albumViewHolder = new GridViewHolder(view);
            return albumViewHolder;
        }

        @Override
        public void onBindViewHolder(GridViewHolder holder, int position) {
            SongDB curr = artistAlbumList.get(position);
            holder.album_name.setText(curr.getAlbum());
            /*Glide.with(context)
                    .load("file://" + curr.getAlbumDB().getAlbumArt())
                    .error(android.R.drawable.ic_media_play)
                    .centerCrop()
                    .crossFade()
                    .into(holder.album_image);*/
            Picasso.with(getContext())
                    .load("file://" + curr.getAlbumDB().getAlbumArt())
                    .placeholder(R.drawable.lo)
                    .error(R.drawable.lo)
                    .into(holder.album_image);

            ViewCompat.setTransitionName(holder.album_image,String.valueOf(position) + "_albumArtistArt");

        }

        @Override
        public int getItemCount() {
            if(artistAlbumList!=null) {
                return artistAlbumList.size();
            }
            else{
                return 0;
            }
        }

    }

    class GridViewHolder extends RecyclerView.ViewHolder {

        ImageView album_image;
        TextView album_name, album_artist;

        public GridViewHolder(final View itemView) {
            super(itemView);

            album_image = (ImageView) itemView.findViewById(R.id.artist_image);
            album_name = (TextView) itemView.findViewById(R.id.album_name);
            album_artist = (TextView) itemView.findViewById(R.id.album_artist);

            itemView.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                            {
                                //Log.e("Manojit","dadasdasdasdasdasda");

                                int data = grid_Artist_RecylerView.getChildAdapterPosition(itemView);
                                int index = getAlbumListSongIndex(data);
                                //Trail1 album_list = new Trail1(2);
                                Album_list album_list = new Album_list(index);

                                Transition transition = TransitionInflater.from(getContext()).inflateTransition(R.transition.transition);
                                //setEnterTransition(new Explode());
                                Fade explode =new Fade();
                                explode.setDuration(150);
                                // explode.setStartDelay(350);
                                Slide slide = new Slide(Gravity.TOP);
                                // slide.setDuration(2000);
                                //setExitTransition(new Fade());
                                //setReenterTransition(new Fade());
                                album_list.setEnterTransition(explode);
                                //album_list.setExitTransition(slide);
                                //album_list.setExitTransition(new Slide(Gravity.TOP));
                                album_list.setSharedElementEnterTransition(transition);
                                setSharedElementReturnTransition(transition);
                                setAllowEnterTransitionOverlap(false);
                                setAllowReturnTransitionOverlap(false);
                                //album_image.setTransitionName("albumart");


                                ((AppCompatActivity) getContext()).getSupportFragmentManager()
                                        .beginTransaction()
                                        //.hide(((AppCompatActivity) context).getSupportFragmentManager().findFragmentByTag("naya"))
                                        .addSharedElement(album_image, "albumart")
                                        .replace(R.id.mainFragmentContainer, album_list,"hello")
                                        //.show(((AppCompatActivity) context).getSupportFragmentManager().findFragmentById(R.id.mainFragmentContainer))
                                        .addToBackStack(null)
                                        .commit();


                            }
                        }
                    }
            );
        }

    }

    public class ListAdapter extends RecyclerView.Adapter<ListViewHolder> {

        Context context;
        LayoutInflater inflater;
        ArrayList<SongDB> artistList = MainActivity.ArtistList;
        SongDB curr = artistList.get(data);

        public ListAdapter(Context context) {
            this.context = context;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.custom_adapter, parent, false);
            ListViewHolder listViewHolder = new ListViewHolder(view);
            return listViewHolder;
        }

        @Override
        public void onBindViewHolder(ListViewHolder holder, int position) {
            SongDB currentSong = artistSongList.get(position);
            holder.song_title.setText(currentSong.getTitle());
            holder.song_artist.setText(currentSong.getArtist());
        /*Picasso.with(context)
                .load("file://"+currentSong.getAlbumArt())
                .error(android.R.drawable.ic_media_play)
                .into(holder.imageView2);*/
            Picasso.with(context)
                    .load("file://" + currentSong.getAlbumDB().getAlbumArt())
                    .placeholder(R.drawable.lo)
                    .error(R.drawable.lo)
                    .into(holder.imageView2);

        }


        @Override
        public int getItemCount() {
            if(artistSongList!=null) {
                return artistSongList.size();
            }
            else{
                return 0;
            }
        }
    }

    class ListViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView2;
        TextView song_title, song_artist;

        public ListViewHolder(View itemView) {
            super(itemView);

            imageView2 = (ImageView) itemView.findViewById(R.id.artist_image);
            song_title = (TextView) itemView.findViewById(R.id.albumlist_title);
            song_artist = (TextView) itemView.findViewById(R.id.song_artist);
        }


    }
    public ArrayList<SongList> getArtistSongList(ArrayList<SongList> fullList, ArrayList<SongList> artistList) {
        ArrayList<SongList> artistSongList = new ArrayList<>();
        SongList curr = artistList.get(data);
        for (int i = 0; i < fullList.size(); i++) {
            if (fullList.get(i).getArtist().equalsIgnoreCase(curr.getArtist_Name())) {
                artistSongList.add(fullList.get(i));
            }
        }
        return artistSongList;
    }

    public class RecyclerTouchListener implements RecyclerView.OnItemTouchListener{

        GestureDetector gestureDetector;
        onItemClickInterface onItemClickInterface;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final onItemClickInterface onItemClickInterface) {
            this.onItemClickInterface = onItemClickInterface;
            gestureDetector = new GestureDetector(context,new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View view = recyclerView.findChildViewUnder(e.getX(),e.getY());
                    if(view!=null && onItemClickInterface!=null){
                        onItemClickInterface.longClick(view,recyclerView.getChildPosition(view));
                    }
                    super.onLongPress(e);
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View view = rv.findChildViewUnder(e.getX(),e.getY());
            if(view!=null && onItemClickInterface!=null && gestureDetector.onTouchEvent(e)){
                onItemClickInterface.click(view,rv.getChildPosition(view));
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
    public interface onItemClickInterface{
        public void click(View view , int position);
        public void longClick(View view , int position);
    }

    public int getAlbumListSongIndex(int position){
        int index = 0;
        SongDB curr = artistAlbumList.get(position);
        for(int i = 0;i<albumList.size();i++){
            if(albumList.get(i).getAlbumID() == (curr.getAlbumID())){
                index = i;
                break;
            }
        }
        return index;
    }

    public interface ArtistListAlbumFragmentChange{
        public void artistAlbumFragmentChange(int position);
        public void artistListSongsList(int position);
        public void notOldSongSongFragment();
    }

}
