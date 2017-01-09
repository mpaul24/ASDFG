package com.manojit.paul.MuBox;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;

import xyz.danoz.recyclerviewfastscroller.vertical.VerticalRecyclerViewFastScroller;


/**
 * A simple {@link Fragment} subclass.
 */
public class Album extends Fragment   {

    public Album() {
        // Required empty public constructor
    }


    RecyclerView albumRecycle;
    AlbumRecyclerAdapter albumRecyclerAdapter;
    RecyclerView.LayoutManager layoutManager;
    AlbumlistClick albumlistClick;
    static float left, top, width, height;
    VerticalRecyclerViewFastScroller fastScroller;
    Context context;
    //static int COLOR ;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album, container, false);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            TypedValue typedValue = new TypedValue();
            context.getTheme().resolveAttribute(R.attr.colorPrimaryDark,typedValue,true);
            int color = typedValue.data;
            getActivity().getWindow().setStatusBarColor(color);
            //getActivity().getWindow().setNavigationBarColor(color);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        albumlistClick = (AlbumlistClick) getActivity();
        albumRecycle = (RecyclerView) view.findViewById(R.id.albumRecycle);
        albumRecyclerAdapter = new AlbumRecyclerAdapter(getContext());
        layoutManager = new GridLayoutManager(getContext(),2);
        albumRecycle.setAdapter(albumRecyclerAdapter);
        albumRecycle.setLayoutManager(layoutManager);
        albumRecycle.addItemDecoration(new GridSpacingItemDecoration(2,20,true));
        albumRecycle.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), albumRecycle, new onItemClickInterface() {
            @Override
            public void click(View view, int position) {
                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    //Log.e("Manojit","asdadasdasdasda");
                    int[] ints = new int[2];
                    view.getLocationOnScreen(ints);
                    left = ints[0];
                    top = ints[1];
                    width = view.getWidth();
                    height = view.getHeight() - 100;
                    //Log.e("qwerty","AlbumClick"+"\n"+left+"\n"+top+ view.getWidth()+"\n"+height);
                    albumlistClick.changeFragment(position);
                }
            }

            @Override
            public void longClick(View view, int position) {
            }
        }));
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
            });
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


    public interface AlbumlistClick {
        public void changeFragment(int data);
    }

    private class AlbumRecyclerAdapter extends RecyclerView.Adapter<AlbumViewHolder> {

        Context context;
        LayoutInflater inflater;
        ArrayList<SongDB> albumList = MainActivity.AlbumList;
        boolean aBoolean = false;
        int mainColor;

        public AlbumRecyclerAdapter(Context context) {
            this.context = context;
        }

        @Override
        public AlbumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.custom_album_demo, parent, false);
            AlbumViewHolder albumViewHolder = new AlbumViewHolder(view);
            return albumViewHolder;
        }

        @Override
        public void onBindViewHolder(final AlbumViewHolder holder,final int position) {
            SongDB curr = albumList.get(position);
            holder.album_name.setText(curr.getAlbum());
            holder.album_artist.setText(curr.getArtist());

            try {
                Picasso.with(getContext())
                        .load("file://" + curr.getAlbumDB().getAlbumArt())
                        .into(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                            }

                            @Override
                            public void onBitmapFailed(Drawable errorDrawable) {

                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                            }
                        });

                Picasso.with(getContext())
                        .load("file://" + curr.getAlbumDB().getAlbumArt())
                        .placeholder(R.drawable.lo)
                        .into(holder.album_image);
            }catch(Exception e){
                e.printStackTrace();
                //albumList.remove(position);
            }




            ViewCompat.setTransitionName(holder.album_image,String.valueOf(position) + "_albumart");


        }

        private class backGround extends AsyncTask<Integer, Void , Void>{

            int color;
            int position;
            @Override
            protected Void doInBackground(Integer... params) {

                position = params[0];
                MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                byte[] art =null ;
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.lo);
                if(MainActivity.AlbumList.get(params[0]).getPath() != null) {
                    try {
                        mediaMetadataRetriever.setDataSource(MainActivity.AlbumList.get(params[0]).getPath());
                        art = mediaMetadataRetriever.getEmbeddedPicture();
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
                if (art != null) {
                    bitmap = BitmapFactory.decodeByteArray(art, 0, art.length);
                }
                Palette palette = Palette.from(bitmap).maximumColorCount(32).generate();
                color = palette.getVibrantColor(Color.GRAY);
                if(color == Color.GRAY){
                    color = palette.getLightVibrantColor(Color.GRAY);
                    if(color == Color.GRAY){
                        color = palette.getDominantColor(Color.GRAY);
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Log.e("Manojit",""+color);
                mainColor = color;
                aBoolean = true;
                notifyItemChanged(position);
            }
        }

        @Override
        public int getItemCount() {
            if(albumList!=null) {
                return albumList.size();
            }
            else{
                return 0 ;
            }
        }


    }

    private class AlbumViewHolder extends RecyclerView.ViewHolder {
        ImageView album_image;
        LinearLayout imagebelow;
        TextView album_name, album_artist;

            public AlbumViewHolder(final View itemView) {
            super(itemView);

            imagebelow = (LinearLayout) itemView.findViewById(R.id.imagebelow);
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

                                int data = albumRecycle.getChildAdapterPosition(itemView);

                                //Trail1 album_list = new Trail1(2);
                                Album_list album_list = new Album_list(data);

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


                                ((AppCompatActivity) context).getSupportFragmentManager()
                                        .beginTransaction()
                                        //.hide(((AppCompatActivity) context).getSupportFragmentManager().findFragmentByTag("naya"))
                                        .addSharedElement(album_image, "albumart")
                                        .replace(R.id.mainFragmentContainer, album_list,"hello_album_list")
                                        //.show(((AppCompatActivity) context).getSupportFragmentManager().findFragmentById(R.id.mainFragmentContainer))
                                        .addToBackStack(null)
                                        .commit();


                            }

                        }
                    }
            );
        }
    }


}


