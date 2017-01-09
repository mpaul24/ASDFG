package com.manojit.paul.MuBox;

import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SearchableActivity extends AppCompatActivity {

    SearchView searchView;
    TextView queryText;
    String query;
    RecyclerView searchRecyclerView;
    ArrayList<SongDB> resultList = new ArrayList<>();
    Adapeter adapeter;
    ImageView searchImage;
    TextView searchText,song;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        if(preferences.contains("theme"))
        {
            String s = preferences.getString("theme","light");
            if(s.equals("dark"))
                setTheme(R.style.AppTheme_Dark);
            else if(s.equals("red"))
                setTheme(R.style.AppTheme_Red);
            else if(s.equals("purple"))
                setTheme(R.style.AppTheme_Purple);
            else if(s.equals("green"))
                setTheme(R.style.AppTheme_Green);
        }

        setContentView(R.layout.activity_searchable);

        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);

        searchImage = (ImageView) findViewById(R.id.searchImage);
        searchText = (TextView) findViewById(R.id.searcgText);
        song = (TextView) findViewById(R.id.song);
        searchRecyclerView = (RecyclerView) findViewById(R.id.searchRecyclerView);
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        adapeter = new Adapeter();
        searchRecyclerView.setAdapter(adapeter);
        //Log.e("Manojit","Search");
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
            doQuery(query);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.search_menu,menu);
        //SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.menu_serch).getActionView();
        // Assumes current activity is the searchable activity
        if(searchView!=null) {
            searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
            searchView.setBackgroundColor(Color.WHITE);
            searchView.setFadingEdgeLength(10);
            searchView.setX(-25f);
            searchView.setQuery(query,false);
            searchView.setSubmitButtonEnabled(false);
            searchView.setOnQueryTextListener(
                    new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String query) {
                            return false;
                        }

                        @Override
                        public boolean onQueryTextChange(String newText) {

                            doQuery(newText);

                            return true;
                        }
                    }
            );
            //searchView.set
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public void doQuery(String query){
                resultList.clear();
            ArrayList<SongDB> quryList = MainActivity.songList;
            for (int i = 0; i < quryList.size(); i++) {
                if (quryList.get(i).getTitle().toLowerCase().contains(query.toLowerCase())) {
                    resultList.add(quryList.get(i));
                }
            }

            if(resultList!=null){
                song.setVisibility(View.VISIBLE);
                searchImage.setVisibility(View.GONE);
                searchText.setVisibility(View.GONE);
                //Log.e("Manoit","Hello   ");
                Adapeter adapeter = new Adapeter();
                //adapeter.notifyDataSetChanged();
                searchRecyclerView.setAdapter(adapeter);
                if(resultList.size() == 0){
                    song.setVisibility(View.GONE);
                    searchImage.setVisibility(View.VISIBLE);
                    searchText.setVisibility(View.VISIBLE);
                    //Adapeter adapeter = new Adapeter();
                    searchRecyclerView.setAdapter(adapeter);
                    //searchRecyclerView.removeAllViewsInLayout();
                }else{

                }
            }
    }

    private class Adapeter extends RecyclerView.Adapter<SearchViewHolder>{

        LayoutInflater inflater;
        public Adapeter() {
            inflater = LayoutInflater.from(getBaseContext());
        }

        @Override
        public SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.search_custom_adapter,parent,false);
            SearchViewHolder albumViewHolder = new SearchViewHolder(view);
            return albumViewHolder;
        }


        @Override
        public void onBindViewHolder(SearchViewHolder holder, int position) {
            SongDB currentSong = resultList.get(position);
            holder.song_title.setText(currentSong.getTitle());
            holder.song_artist.setText(currentSong.getArtist());

            Picasso.with(getBaseContext())
                    .load("file://"+currentSong.getAlbumDB().getAlbumArt())
                    .placeholder(R.drawable.lo)
                    .resize(150, 150)
                    .centerCrop()
                    .into(holder.imageView2);
        }

        @Override
        public int getItemCount() {
            if(resultList.size() == 0){
                return 0;
            }
            return resultList.size();
        }
    }

    public class SearchViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView2;
        TextView song_title,song_artist;

        public SearchViewHolder(final View itemView) {
            super(itemView);
            imageView2 = (ImageView) itemView.findViewById(R.id.artist_image);
            song_title = (TextView) itemView.findViewById(R.id.albumlist_title);
            song_artist = (TextView) itemView.findViewById(R.id.song_artist);
            itemView.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int pos = searchRecyclerView.getChildAdapterPosition(itemView);
                            SongDB songDB = resultList.get(pos);
                           // searchInterface.setSongFromSearch(1);
                            //mainActivity.setSongFromSearch(1);
                            CustomModel.getInstance().changeState(songDB.getId());
                            Toast.makeText(SearchableActivity.this, ""+songDB.getTitle()+" is playing!", Toast.LENGTH_SHORT).show();
                        }
                    }
            );
        }
    }
}
