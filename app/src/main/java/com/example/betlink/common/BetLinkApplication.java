package com.example.betlink.common;

import android.app.Application;
import com.example.betlink.data.SessionManager;

public class BetLinkApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize SessionManager with Application Context
        SessionManager.getInstance().init(this);
    }
}
