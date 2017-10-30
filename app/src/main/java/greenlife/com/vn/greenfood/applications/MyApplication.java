package greenlife.com.vn.greenfood.applications;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;

import greenlife.com.vn.greenfood.utils.NetworkUtils;

/**
 * Created by QuanT on 9/11/2017.
 */

public class MyApplication extends Application{
    private static MyApplication myApplication;

    public static synchronized MyApplication getInstance(){
        return myApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        myApplication = this;
    }

    public void setConnectivityListener(NetworkUtils.ConnectivityReceiverListener listener){
        NetworkUtils.connectivityReceiverListener = listener;
    }

    @Override
    public void onTerminate() {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(FirebaseAuth.getInstance().getCurrentUser().getUid());
        super.onTerminate();
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
