package org.fuckboilerplate.rxsocialconnect;

import android.app.Application;

import org.fuckboilerplate.rx_social_connect.RxSocialConnect;

/**
 * Created by victor on 17/05/16.
 */
public class SampleApp extends Application {

    @Override public void onCreate() {
        super.onCreate();
        RxSocialConnect.register(this);
    }
}
