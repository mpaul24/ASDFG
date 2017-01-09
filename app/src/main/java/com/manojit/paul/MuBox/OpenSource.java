package com.manojit.paul.MuBox;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Manojit Paul on 10/28/2016.
 */

public class OpenSource extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().setTitle("MuBOX 1.0b\nOpenSource License");
        return inflater.inflate(R.layout.dialog_open_source,container,false);
    }
}
