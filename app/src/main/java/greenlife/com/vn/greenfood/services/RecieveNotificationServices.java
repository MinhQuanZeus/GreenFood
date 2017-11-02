package greenlife.com.vn.greenfood.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import greenlife.com.vn.greenfood.R;
import greenlife.com.vn.greenfood.activities.MainActivity;
import greenlife.com.vn.greenfood.models.User;
import greenlife.com.vn.greenfood.network.models.order.Order;
import greenlife.com.vn.greenfood.utils.FirebaseUntils;
import greenlife.com.vn.greenfood.utils.LibrarySupportManager;

/**
 * Created by QuanT on 11/2/2017.
 */

public class RecieveNotificationServices extends FirebaseMessagingService {
    private static final String TAG = RecieveNotificationServices.class.getSimpleName();
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabaseReference;
    private static int x=0;
    private User buyer;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG,"From: "+remoteMessage.getFrom());
        if (remoteMessage.getNotification().getBody().length() > 0) {
            Log.d(TAG, "foreground notification : " + remoteMessage.getNotification().getBody());
        }
        Gson gSon = new Gson();
        final Order orderResponse = gSon.fromJson(remoteMessage.getNotification().getBody(), Order.class);
        if (orderResponse != null){
            Log.d(TAG, "Parse OK : " + orderResponse.toString());
            final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference.child("users")
                    .child(orderResponse.getBuyerID())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            applyOrder(orderResponse, user.getName());
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

        }

    }



    private void applyOrder(Order order, String buyerName) {
        mDatabaseReference = FirebaseDatabase.getInstance()
                .getReference()
                .child("users")
                .child(order.getSellerID())
                .child("history");
        DatabaseReference newPost = mDatabaseReference.push();
        newPost.child("type").setValue("order");
        newPost.child("buyerID").setValue(order.getBuyerID());
        newPost.child("foodName").setValue(order.getFoodName());
        newPost.child("foodImgLink").setValue(order.getFoodImgLink());
        newPost.child("time").setValue(order.getTime());
        newPost.child("status").setValue("Unchecked");



        //2. Notification
        String title = buyerName + " orders " + order.getFoodName();
        Intent resultIntent = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        Intent rejectIntent = new Intent(getBaseContext(), MainActivity.class);
        rejectIntent.putExtra("order_key", mDatabaseReference.getRef().getKey());
        PendingIntent rejectPendingIntent = PendingIntent.getBroadcast(getBaseContext(), 0, rejectIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_logo)
                        .setLargeIcon(LibrarySupportManager.getInstance().getBitmapFromURL(order.getFoodImgLink()))
                        .setContentTitle(title)
                        .setContentText(order.getTime())
                        .addAction(R.drawable.ic_done_black_24dp, "Đồng ý", resultPendingIntent)
                        .addAction(R.drawable.ic_close_black_24dp, "Huỷ bỏ", rejectPendingIntent);

        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(100+x, mBuilder.build());
        x++;

    }

}
