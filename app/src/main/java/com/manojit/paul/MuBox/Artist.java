package com.manojit.paul.MuBox;


import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import xyz.danoz.recyclerviewfastscroller.vertical.VerticalRecyclerViewFastScroller;


/**
 * A simple {@link Fragment} subclass.
 */
public class Artist extends Fragment {

    ArtistClickFragmentChange changeFragment;
    Context context;

    RecyclerView artistRecyclerView;

    public Artist() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //Log.e("asdf", "ARTIST-----------onCreate");
        View view = inflater.inflate(R.layout.fragment_artist, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        changeFragment = (ArtistClickFragmentChange) getActivity();
        artistRecyclerView = (RecyclerView) view.findViewById(R.id.artistRecyclerView);
        artistRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        artistRecyclerView.setAdapter(new Adapter(getActivity()));
        artistRecyclerView.addItemDecoration(new GridSpacingItemDecoration(2,20,true));
        artistRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(),artistRecyclerView, new onItemClickInterface() {
            @Override
            public void click(View view, int position) {
                //Toast.makeText(getActivity(), "click"+position, Toast.LENGTH_SHORT).show();
                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    changeFragment.artistClickChangeFragment(position);
                }
            }

            @Override
            public void longClick(View view, int position) {
                //Toast.makeText(getActivity(), "longClick"+position, Toast.LENGTH_SHORT).show();
            }
        }));

    }

    public class Adapter extends RecyclerView.Adapter<ArtistViewHolder> {

        Context context;
        LayoutInflater inflater;
        //ArrayList<SongList> songLists;
        //ArrayList<SongList> albumList;
        ArrayList<SongDB> artistLists;

        public Adapter(Context context) {
            this.context = context;
            inflater = LayoutInflater.from(context);
            //songLists = MainActivity.songList;
            //albumList = songsProvider.getAlbumList(songLists);
            artistLists = MainActivity.ArtistList;
            //artistLists = songsProvider.getArtistList(songLists);
        }

        @Override
        public ArtistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.custom_artist_demo, parent, false);
            ArtistViewHolder albumViewHolder = new ArtistViewHolder(view);
            return albumViewHolder;
        }

        @Override
        public void onBindViewHolder(ArtistViewHolder holder, int position) {
            SongDB curr = artistLists.get(position);
            holder.artist_name.setText(curr.getArtist());
            holder.artist_not.setText("Tracks : "+curr.getArtistDB().getArtist_NOT());
            holder.artist_album.setText("Albums : "+curr.getArtistDB().getArtist_NOA());
            /*Glide.with(context)
                    .load("file://" + curr.getAlbumDB().getAlbumArt())
                    .error(R.drawable.play_icon)
                    .centerCrop()
                    .crossFade()
                    .into(holder.artist_image);*/
            try {
                Picasso.with(getContext())
                        .load("file://" + curr.getAlbumDB().getAlbumArt())
                        //.placeholder(R.drawable.play_icon)
                        .placeholder(R.drawable.lo)
                        .into(holder.artist_image);
            }catch(Exception e){
                e.printStackTrace();
                //artistLists.remove(position);
            }

            ViewCompat.setTransitionName(holder.artist_image,String.valueOf(position) + "artistart");
        }

        @Override
        public int getItemCount() {
            if(artistLists!=null) {
                return artistLists.size();
            }
            else{
                return 0;
            }
        }

    }

        class ArtistViewHolder extends RecyclerView.ViewHolder {
            ImageView artist_image;
            TextView artist_name,artist_not,artist_album;

            public ArtistViewHolder(final View itemView) {
                super(itemView);

                artist_image = (ImageView) itemView.findViewById(R.id.artist_image);
                artist_name = (TextView) itemView.findViewById(R.id.artist_name);
                artist_not = (TextView) itemView.findViewById(R.id.artist_not);
                artist_album = (TextView) itemView.findViewById(R.id.artist_album);
                itemView.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                                {
                                    //Log.e("Manojit","dadasdasdasdasdasda");


                                    int data = artistRecyclerView.getChildAdapterPosition(itemView);
                                    //Trail1 album_list = new Trail1(2);
                                    Artist_List_Fragment artist_list_fragment = new Artist_List_Fragment(data);

                                    Transition transition = TransitionInflater.from(getContext()).inflateTransition(R.transition.transition_1);
                                    //setEnterTransition(new Explode());
                                    Fade explode =new Fade();
                                    explode.setDuration(150);
                                    // explode.setStartDelay(350);
                                    Explode slide = new Explode();
                                    slide.setStartDelay(250);
                                    // slide.setDuration(2000);
                                    //setExitTransition(new Fade());
                                    //setReenterTransition(new Fade());
                                    artist_list_fragment.setEnterTransition(explode);
                                    //artist_list_fragment.setExitTransition(explode);
                                    //setReenterTransition(explode);
                                    setExitTransition(explode);
                                    //album_list.setExitTransition(slide);
                                    //album_list.setExitTransition(new Slide(Gravity.TOP));
                                    artist_list_fragment.setSharedElementEnterTransition(transition);
                                    artist_list_fragment.setSharedElementReturnTransition(transition);
                                    setAllowEnterTransitionOverlap(false);
                                    setAllowReturnTransitionOverlap(false);
                                    //album_image.setTransitionName("albumart");


                                    ((AppCompatActivity) context).getSupportFragmentManager()
                                            .beginTransaction()
                                            //.hide(((AppCompatActivity) context).getSupportFragmentManager().findFragmentByTag("naya"))
                                            .addSharedElement(artist_image, "artistart")
                                            .replace(R.id.mainFragmentContainer, artist_list_fragment,"hello_artist_list")
                                            //.show(((AppCompatActivity) context).getSupportFragmentManager().findFragmentById(R.id.mainFragmentContainer))
                                            .addToBackStack(null)
                                            .commit();


                                }

                            }
                        }
                );

            }
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

    public interface ArtistClickFragmentChange{
        public void artistClickChangeFragment(int position);
    }

}


