package com.manojit.paul.MuBox;

/**
 * Created by Manojit Paul on 18-10-2016.
 */

public class CustomModel {

    public interface OnCustomStateListener {
        void notifyItem();
    }

    private static CustomModel mInstance;
    private OnCustomStateListener mListener;
    private long id;

    private CustomModel() {}

    public static CustomModel getInstance() {
        if(mInstance == null) {
            mInstance = new CustomModel();
        }
        return mInstance;
    }

    public void setListener(OnCustomStateListener listener) {
        mListener = listener;
    }

    public void changeState(long id) {
        if(mListener != null) {
            this.id = id;
            notifyStateChange();
        }
    }

    public long getState() {
        return id;
    }

    private void notifyStateChange() {
        mListener.notifyItem();
    }
}
