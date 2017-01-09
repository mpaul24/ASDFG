package com.manojit.paul.MuBox;

import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Manojit Paul on 23-10-2016.
 */

public class AboutUSDialog extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().setTitle("MuBOX 1.0b");
        View v =  inflater.inflate(R.layout.about_us,container,false);
        TextView google = (TextView) v.findViewById(R.id.google);
        TextView facebook = (TextView) v.findViewById(R.id.facebook);
        TextView github = (TextView) v.findViewById(R.id.github);
        TextView bug = (TextView) v.findViewById(R.id.bug);
        google.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String url = "https://plus.google.com/118257061409686736634";
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    }
                }
        );

        facebook.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String url = "https://www.facebook.com/manojit.paul.16";
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    }
                }
        );

        github.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String url = "https://github.com/mpaul24";
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    }
                }
        );

        bug.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                "mailto","manojitp4@gmail.com",null));
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "FeedBack");
                        //emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
                        startActivity(Intent.createChooser(emailIntent, "Send email..."));
                    }
                }
        );
        return v;

    }
}
