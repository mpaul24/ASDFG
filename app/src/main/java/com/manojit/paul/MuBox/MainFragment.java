package com.manojit.paul.MuBox;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {
    Toolbar toolbar;
    ViewPager viewPager;
    TabLayout tabs;
    mainFragment  comm;

    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar = (Toolbar) view.findViewById(R.id.appbar11);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        //((AppCompatActivity)getActivity()).getSupportActionBar().setIcon(R.drawable.drawer_icon);
        toolbar.setLogo(R.drawable.drawer_icon);
        toolbar.setTitle("MuBOX");
        toolbar.setTitleMarginStart(50);

        toolbar.setTitleTextColor(Color.WHITE);
        comm = (mainFragment) getActivity();
        tabs = (TabLayout) view.findViewById(R.id.tabs);
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setCurrentItem(1);
        Adapter adapter = new Adapter(getChildFragmentManager());
        adapter.addFragment(new Song() , "SONG");
        adapter.addFragment(new Album() , "ALBUM");
        adapter.addFragment(new Artist() , "ARTIST");
        viewPager.setAdapter(adapter);
        tabs.setupWithViewPager(viewPager);
        View view1 = toolbar.getChildAt(1);
        view1.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        comm.aa();
                    }
                }
        );

    }

    private class Adapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) { //app icon in action bar clicked; go back
            //do something

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public interface mainFragment{
        public void aa();

    }
}
