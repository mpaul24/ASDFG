package com.manojit.paul.MuBox;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


/**
 * A simple {@link Fragment} subclass.
 */
public class Album_list extends Fragment {

    RelativeLayout above;
    int AlbumPosition;
    ImageView albumListArt,fake;
    RecyclerView albumListRecyclerView;
    ArrayList<SongDB> songLists = MainActivity.AlbumList;
    ArrayList<SongDB> fullList = MainActivity.songList;
    ArrayList<SongDB> albumList;
    TextView fullScreenTitle,fullScreenMusicArtist,fullScreenYear;
    AlbumListGivesIndex albumListGivesIndex;
    FloatingActionButton floatingActionButton;
    float mLeft,mTop,mWidth,mHeight,top,left,scaleX,scaleY;
    Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbar;
    AppBarLayout appBarLayout;
    int color;

    public Album_list() {

        // Required empty public constructor
    }
    public Album_list(int data){
        AlbumPosition = data;
        albumList = getList();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album_list, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        appBarLayout = (AppBarLayout) view.findViewById(R.id.appbar);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setTitle(""+songLists.get(AlbumPosition).getAlbum());
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        //((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        collapsingToolbar =
                (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitleEnabled(false);


        floatingActionButton = (FloatingActionButton) view.findViewById(R.id.albumFloatingButton);
        above = (RelativeLayout) view.findViewById(R.id.above);
        albumListGivesIndex = (AlbumListGivesIndex) getActivity();
        fake = (ImageView) view.findViewById(R.id.fake);
        albumListArt = (ImageView) view.findViewById(R.id.albumListart);
        fullScreenTitle = (TextView) view.findViewById(R.id.fullScreenTitle);
        fullScreenMusicArtist = (TextView) view.findViewById(R.id.fullScreenMusicArtist);
        fullScreenYear = (TextView) view.findViewById(R.id.fullScreenYear);

        /*Glide.with(getContext())
                .load("file://" + songLists.get(AlbumPosition).getAlbumDB().getAlbumArt())
                .error(R.drawable.play_icon)
                .centerCrop()
                .crossFade()
                .into(albumListArt);*/
        try {
            Picasso.with(getContext())
                    .load("file://" + songLists.get(AlbumPosition).getAlbumDB().getAlbumArt())
                    //.placeholder(R.drawable.lo)
                    .error(R.drawable.lo)
                    .into(albumListArt);
        }catch (Exception e){
            e.printStackTrace();
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
            byte[] art =null ;
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.lo);
            if(MainActivity.AlbumList.get(AlbumPosition).getPath() != null) {
                try {
                    mediaMetadataRetriever.setDataSource(MainActivity.AlbumList.get(AlbumPosition).getPath());
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
            this.color = color;

            float[] hsv = new float[3];
            Color.colorToHSV(color, hsv);
            hsv[2] *= 0.8f; // value component
            int darkcolor = Color.HSVToColor(hsv);
            //toolbar.setBackgroundColor(color);
            collapsingToolbar.setBackgroundColor(color);
            collapsingToolbar.setContentScrimColor(color);
            getActivity().getWindow().setStatusBarColor(darkcolor);
            //getActivity().getWindow().setNavigationBarColor(darkcolor);
            //ImageView imageView = (ImageView) view.findViewById(R.id.imageView2);
            floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(color));
            //DrawableCompat.setTint(imageView.getDrawable(), ContextCompat.getColor(getContext(),color));
        }

        floatingActionButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                        SharedPreferences.Editor editor = preferences.edit();
                        Gson gson = new Gson();
                        String song = gson.toJson(albumList);
                        editor.putString("Albumlist", song);
                        editor.commit();
                        int index = getPositionInMainList(albumList.get(0).getTitle());
                        albumListGivesIndex.notOldSongSongFragment();
                        albumListGivesIndex.AlbumListGivesIndex(index, 0);
                    }
                }
        );



        String composer = songLists.get(AlbumPosition).getComposer();
        if (composer == "" || composer == null) {
            composer = albumList.get(0).getArtist();
        }
        fullScreenTitle.setText(songLists.get(AlbumPosition).getAlbum());
        fullScreenMusicArtist.setText(composer);
        if (songLists.get(AlbumPosition).getYear() != 0) {
            fullScreenYear.setText("" + songLists.get(AlbumPosition).getYear());
        } else {
            fullScreenYear.setText("");
        }

        ImageView imageView = (ImageView) view.findViewById(R.id.imageView2);
        DrawableCompat.setTint(imageView.getDrawable(),color);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        albumListRecyclerView = (RecyclerView) view.findViewById(R.id.albumListRecyclerView);
        albumListRecyclerView.setAdapter(new Adapter(getActivity()));
        albumListRecyclerView.setLayoutManager(layoutManager);
        albumListRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), albumListRecyclerView, new onItemClickInterface() {
            @Override
            public void click(View view, int position) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = preferences.edit();
                Gson gson = new Gson();
                String song = gson.toJson(albumList);
                editor.putString("Albumlist", song);
                editor.commit();
                int index = getPositionInMainList(albumList.get(position).getTitle());
                albumListGivesIndex.notOldSongSongFragment();
                albumListGivesIndex.AlbumListGivesIndex(index, position);
            }

            @Override
            public void longClick(View view, int position) {
                Toast.makeText(getActivity(), "Item longClicked " + position, Toast.LENGTH_SHORT).show();
            }
        }
        ));
        albumListRecyclerView.setNestedScrollingEnabled(false);

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
        {
            ViewTreeObserver observer = fake.getViewTreeObserver();
            observer.addOnPreDrawListener(
                    new ViewTreeObserver.OnPreDrawListener() {
                        @Override
                        public boolean onPreDraw() {
                            fake.getViewTreeObserver().removeOnPreDrawListener(this);
                            try {
                                Picasso.with(getActivity())
                                        .load("file://" + songLists.get(AlbumPosition).getAlbumDB().getAlbumArt())
                                        .error(R.drawable.lo)
                                        .into(fake);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            fullScreenTitle.setVisibility(View.GONE);
                            fullScreenMusicArtist.setVisibility(View.GONE);
                            fullScreenYear.setVisibility(View.GONE);
                            albumListRecyclerView.setVisibility(View.GONE);
                            above.setVisibility(View.GONE);
                            albumListArt.setVisibility(View.GONE);
                            int[] ints = new int[2];
                            fake.getLocationOnScreen(ints);
                            mLeft = ints[0];
                            mTop = ints[1];
                            mWidth = fake.getWidth();
                            mHeight = fake.getHeight();
                            scaleX = Album.width / mWidth;
                            scaleY = Album.height / mHeight;
                            left = Album.left - mLeft;
                            top = Album.top - mTop;

                            enterAnimation();

                            return true;
                        }
                    }
            );
        }

    }

    public void enterAnimation(){
        fake.setPivotX(0f);
        fake.setPivotY(0f);
        fake.setScaleX(scaleX);
        fake.setScaleY(scaleY);
        fake.setTranslationX(left);
        fake.setTranslationY(top);

        fake.animate().setDuration(500)
                .scaleX(1f).scaleY(1f)
                .translationX(0f).translationY(0f)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        fullScreenTitle.setVisibility(View.VISIBLE);fullScreenMusicArtist.setVisibility(View.VISIBLE);
                        fullScreenYear.setVisibility(View.VISIBLE);
                        albumListRecyclerView.setVisibility(View.VISIBLE);
                        above.setVisibility(View.VISIBLE);
                        albumListArt.setVisibility(View.VISIBLE);
                        fake.setVisibility(View.GONE);

                    }
                });
    }

/*----------------------------------------------------------IMPORTANT----------------------------------------------

 */

    public class Adapter extends RecyclerView.Adapter<Holder>{


        Context context;
        LayoutInflater inflater;
        public Adapter(Context context) {
            this.context = context;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.album_list_custom,parent,false);
            Holder holder = new Holder(view);
            return holder;
        }

        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            holder.albumlist_title.setText(albumList.get(position).getTitle());
            holder.albumlist_artist.setText(albumList.get(position).getArtist());
            long track = albumList.get(position).getTrackNumber();
            if(track>=1000){
                track = track%1000;
            }
            holder.songIndex.setText(""+track);
        }

        @Override
        public int getItemCount() {
            if(albumList!=null) {
                return albumList.size();
            }
            else{
                return 0;
            }
        }
    }

    public class Holder extends RecyclerView.ViewHolder{

        TextView albumlist_title,albumlist_artist,songIndex;

        public Holder(View itemView) {
            super(itemView);
            albumlist_artist = (TextView) itemView.findViewById(R.id.albumlist_artist);
            albumlist_title = (TextView) itemView.findViewById(R.id.albumlist_title);
            songIndex = (TextView) itemView.findViewById(R.id.song_Index);
        }
    }

    public class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        GestureDetector gestureDetector;
        onItemClickInterface onItemClickInterface;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView , final onItemClickInterface onItemClickInterface) {
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
            }
            );

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

    public ArrayList<SongDB> getList(){
        ArrayList<SongDB> albumList= new ArrayList<>();
        SongDB curr = songLists.get(AlbumPosition);
        long ID = curr.getAlbumID();
        for(int i=0;i<fullList.size();i++){
            if(fullList.get(i).getAlbumID()==ID){
                albumList.add(fullList.get(i));
                //Log.e("Manojit",""+fullList.get(i).getTitle());
            }
        }

        Collections.sort(albumList, new Comparator<SongDB>(){
            public int compare(SongDB a, SongDB b){
                return a.getTitle().compareToIgnoreCase(b.getTitle());
            }
        });

        return albumList;
    }
//--------------------------------------------------------------------------------------------------------------------------

    public int getPositionInMainList(String Title){
        //Log.e("asdf",""+Title);
        for(int i=0;i<fullList.size();i++){
            //Log.e("asdf",fullList.get(i).getTitle()+" "+Title);
            if(fullList.get(i).getTitle().equals(Title)){
                return i;
            }
        }
        return 0;
    }

    public  interface AlbumListGivesIndex{
        public void AlbumListGivesIndex(int data,int position);
        public void notOldSongSongFragment();
    }

}
