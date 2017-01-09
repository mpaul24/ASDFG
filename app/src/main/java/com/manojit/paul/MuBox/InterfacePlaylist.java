package com.manojit.paul.MuBox;

/**
 * Created by Manojit Paul on 18-10-2016.
 */

public class InterfacePlaylist {

    public interface OnInterfacePlaylistListener {
        void stateChanged();
    }

    private static InterfacePlaylist mInstance;
    private OnInterfacePlaylistListener mListener;
    private int songIndex;

    private InterfacePlaylist() {}

    public static InterfacePlaylist getInstance() {
        if(mInstance == null) {
            mInstance = new InterfacePlaylist();
        }
        return mInstance;
    }

    public void setListener(OnInterfacePlaylistListener listener) {
        mListener = listener;
    }

    public void changeState(int songIndex) {
        if(mListener != null) {
            this.songIndex = songIndex;
            notifyStateChange();
        }
    }

    public int getState() {
        return songIndex;
    }

    private void notifyStateChange() {
        mListener.stateChanged();
    }

}
