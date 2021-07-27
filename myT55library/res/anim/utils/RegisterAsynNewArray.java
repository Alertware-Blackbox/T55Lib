package com.blackbox.roadways.utils;

import android.app.Activity;
import android.os.AsyncTask;

import com.blackbox.roadways.interfaces.UpdateCountListener;


public class RegisterAsynNewArray extends AsyncTask<String, Void, String> {

    Activity mActivity;

    public RegisterAsynNewArray(Activity activity){
        this.mActivity=activity;
    }

    @Override
    protected void onPreExecute() {

    }


    @Override
    protected String doInBackground(String... params) {
        new RegisterNewArray().register(
                params[0],
                params[1],
                mActivity
        );
        return "";
    }

    @Override
    protected void onPostExecute(String codeResult) {
        ((UpdateCountListener)mActivity).onUpdateCount();
    }
}

