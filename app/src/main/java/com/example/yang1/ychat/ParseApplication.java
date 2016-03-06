package com.example.yang1.ychat;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by Yang1 on 2/24/16.
 */
public class ParseApplication extends Application {
    @Override
    public void onCreate(){
        super.onCreate();
        Parse.enableLocalDatastore(this);
        Parse.initialize(this);
    }
}
