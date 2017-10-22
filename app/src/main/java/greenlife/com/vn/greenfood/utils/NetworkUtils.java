package greenlife.com.vn.greenfood.utils;

import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

import greenlife.com.vn.greenfood.applications.MyApplication;

/**
 * Created by QuanT on 10/1/2017.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class NetworkUtils extends JobService {
    public static NetworkUtils networkUntils;
    public static ConnectivityReceiverListener connectivityReceiverListener;

    public static NetworkUtils getInstance(){
        if(networkUntils == null){
            networkUntils = new NetworkUtils();
        }
        return networkUntils;
    }

    public static boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) MyApplication
                .getInstance()
                .getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetword = connectivityManager.getActiveNetworkInfo();
        return activeNetword != null && activeNetword.isConnectedOrConnecting();
    }

    public static boolean hasConnection(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        ConnectivityManager connectivityManager = (ConnectivityManager) MyApplication.getInstance().getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activedNetwork = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = activedNetwork != null && activedNetwork.isConnectedOrConnecting();
        if(connectivityReceiverListener != null){
            connectivityReceiverListener.onNetwordConnectionChanged(isConnected);
        }
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

    public interface ConnectivityReceiverListener{
        void onNetwordConnectionChanged(boolean isConnected);
    }
}
