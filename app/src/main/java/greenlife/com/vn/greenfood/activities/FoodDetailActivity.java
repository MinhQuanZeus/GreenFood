package greenlife.com.vn.greenfood.activities;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import greenlife.com.vn.greenfood.R;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

import greenlife.com.vn.greenfood.fragments.rating_fragment.RatingDialogFragment;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import greenlife.com.vn.greenfood.events.GetUserFromIDListener;
import greenlife.com.vn.greenfood.models.Post;
import greenlife.com.vn.greenfood.models.User;
import greenlife.com.vn.greenfood.network.RetrofitFactory;
import greenlife.com.vn.greenfood.network.models.order.NotificationAPI;
import greenlife.com.vn.greenfood.network.models.order.Order;
import greenlife.com.vn.greenfood.network.models.order.SendOrderRequest;
import greenlife.com.vn.greenfood.network.models.order.SendOrderResponse;
import greenlife.com.vn.greenfood.network.services.SendOrderService;
import greenlife.com.vn.greenfood.utils.Config;
import greenlife.com.vn.greenfood.utils.LibrarySupportManager;
import greenlife.com.vn.greenfood.utils.MapsUltils;
import greenlife.com.vn.greenfood.utils.NotificationHandleUtils;

public class FoodDetailActivity extends AppCompatActivity {

    private static final String TAG = FoodDetailActivity.class.getSimpleName();
    private Toolbar toolbar;
    private Post post;
    private FirebaseAuth firebaseAuth;
    private User sellerUser;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private DatabaseReference mDatabaseReference;

    private TextView tvPostTime;
    private TextView tvDistance;
    private TextView tvUserName;
    private TextView tvFoodTitle;
    private TextView tvAddress;
    private TextView tvPrice;
    private TextView tvDescription;
    private ImageView ivAvatar;
    private ImageView ivFood;
    private RatingBar ratingBar;

    private Button btnOrder;
    private TextView tvFoodName;
    private Button btn_rating;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);
        firebaseAuth = FirebaseAuth.getInstance();
        setupUI();
        getOrder();
        onReceiverOrder();
    }



    private void onReceiverOrder() {
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    String message = intent.getStringExtra("message");
                    Log.d(TAG, "on receiver order : "+message);
                }
            }
        };
    }

    private void setupUI() {
        tvPostTime = (TextView) findViewById(R.id.tv_current_time);
        tvDistance = (TextView) findViewById(R.id.tv_distance);
        tvUserName = (TextView) findViewById(R.id.tv_username);
        tvFoodTitle = (TextView) findViewById(R.id.tv_food_name);
        tvAddress = (TextView) findViewById(R.id.tv_address);
        tvPrice = (TextView) findViewById(R.id.tv_price);
        tvDescription = (TextView) findViewById(R.id.tv_description);
        ivAvatar = (ImageView) findViewById(R.id.iv_avatar);
        ivFood = (ImageView) findViewById(R.id.iv_food);
        ratingBar = (RatingBar) findViewById(R.id.rating_bar);
        btn_rating = (Button) findViewById(R.id.btn_rating);

        //for send order
        btnOrder = (Button) findViewById(R.id.btn_order);
        tvFoodName = (TextView) findViewById(R.id.tv_food_name);
        toolbar = (Toolbar) findViewById(R.id.tb_main);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setTitle("Food Detail");
        toolbar.setTitleTextColor(getResources().getColor(R.color.icons));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        loadData();
    }

    private void loadData() {
        Intent intent = getIntent();
        String postID = (String) intent.getSerializableExtra("postID");
        DatabaseReference databaseReference;
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("post").orderByChild("id").equalTo(postID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                post = dataSnapshot.getValue(Post.class);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        tvPostTime.setText(post.getTime());
      // User user = MapsUltils.getUser(this, post.getUserID());
        Picasso.with(this)
                .load(post.getImage())
                .into(ivFood);
        getUser(this, post.getUserID());

        //Get distance 2 location, from current user to store
        tvDistance.setText(MapsUltils.getDistanceFromLocation("22C Thành Công, Khu tập thể Bắc Thành Công, Thành Công, Ba Đình, Hà Nội, Vietnam", post.getAddress(), tvDistance));
        tvDescription.setText(post.getDescription());
        tvFoodName.setText((post.getTitle()));
        tvPrice.setText(formatNumber(post.getPrice()));
        tvAddress.setText(post.getAddress());
    }

    private void getUser(final Context context, String userID) {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("users")
                .child(userID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        setUser(user);
                        tvUserName.setText(user.getName());
                        Picasso.with(context)
                                .load(user.getAvatar())
                                .transform(new CropCircleTransformation())
                                .into(ivAvatar);
                        Log.d(TAG, "Fail user : " + user.getTokenID());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public static String formatNumber(long number) {
        if (number < 1000) {
            return String.valueOf(number);
        }
        try {
            NumberFormat formatter = new DecimalFormat("###,###");
            String resp = formatter.format(number);
            resp = resp.replaceAll(",", ".");
            return resp;
        } catch (Exception e) {
            return "";
        }
    }

    private void getOrder() {
        //Handle Order Button
        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(post.getUserID().compareToIgnoreCase(firebaseAuth.getCurrentUser().getUid())==0){
                    Toast.makeText(FoodDetailActivity.this, "Bạn không thể đặt hàng chính sản phẩm của mình ", Toast.LENGTH_SHORT).show();
                } else {
                    sendOrderNotificationMessage();
                }

            }
        });

        // Handle Button rating
        btn_rating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Show rating dialog

                if(post.getUserID().compareToIgnoreCase(firebaseAuth.getCurrentUser().getUid())==0){
                    Toast.makeText(FoodDetailActivity.this, "Bạn không thể tự đánh giá mình ", Toast.LENGTH_SHORT).show();
                } else {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    RatingDialogFragment ratingBarFragment = RatingDialogFragment.getInstant();
                    ratingBarFragment.setData( post.getId() ,firebaseAuth.getCurrentUser().getUid());
                    ratingBarFragment.show(fragmentManager, "dialog");
                }

            }
        });
    }

    public void setUser(User user) {
        this.sellerUser = user;
    }

    private void sendOrderNotificationMessage() {
        //get Token ID
        getUser(this, post.getUserID());
        Log.d(TAG,"address ID : "+sellerUser.getTokenID());
        MapsUltils.getUser(this, firebaseAuth.getCurrentUser().getUid(), new GetUserFromIDListener() {
            @Override
            public void getUserFromID(User user) {
                Order order = null;
                Log.d(TAG, "token ID : " + user.getTokenID());
                final String now = LibrarySupportManager.getInstance().currentDateTime();
                if(post != null){
                    order = new Order(
                            firebaseAuth.getCurrentUser().getUid(),
                            user.getName(),
                            post.getUserID(),
                            tvFoodName.getText().toString(),
                            post.getImage(),
                            "order",
                            now
                    );
                }

                Log.d(TAG, "my order : " + order.toString());
                if(order != null) {
                    Log.d(TAG,"From: "+sellerUser.getUserID());
                    Map<String, String> headerMap = new HashMap<>();
                    headerMap.put("Content-Type","application/json");
                    headerMap.put("Authorization","key=AAAAtbtKAQY:APA91bE6qVASCgSzgZmqeMHENWlmhL8ZFo7AH8p6GdevrosMTPs5tPIFwIE-qiGsijLQio9NPq62uVQAPIW5pegMl2hH6wPA1PwlfS9MocDAo9AIg6JZPiIeOnjgZNMUtxS4n8Z3uh6C");
                    RetrofitFactory.getInstance("https://fcm.googleapis.com/fcm/")
                            .createService(SendOrderService.class)
                            .sendOrder(
                                    headerMap,
                                    new SendOrderRequest("/topics/"+post.getUserID(), new NotificationAPI(order))
                            ).enqueue(new Callback<SendOrderResponse>() {
                        @Override
                        public void onResponse(Call<SendOrderResponse> call, Response<SendOrderResponse> response) {
                            Log.d(TAG, "response code : " + response.code());

                            if(response.code() == 200){
                                Log.d(TAG, "OK send");
                                if(response.body()
                                        .getSuccess() != 0){
                                    Log.d(TAG, "Success send");
                                    //create order list of buyer
                                    mDatabaseReference = FirebaseDatabase.getInstance()
                                            .getReference()
                                            .child("users")
                                            .child(firebaseAuth.getCurrentUser().getUid())
                                            .child("history");
                                    DatabaseReference newPost = mDatabaseReference.push();
                                    newPost.child("type").setValue("request");
                                    newPost.child("sellerID").setValue(post.getUserID());
                                    newPost.child("foodName").setValue(post.getTitle());
                                    newPost.child("time").setValue(now);
                                    newPost.child("foodImgLink").setValue(post.getImage());
                                    newPost.child("status").setValue("waiting");
                                }
                                Toast.makeText(FoodDetailActivity.this, "Bạn đã order thành công ", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<SendOrderResponse> call, Throwable t) {
                            Log.d(TAG, "Fail sending");
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter("registrationComplete"));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(getResources().getString(R.string.notification_push)));
        // clear the notification area when the app is opened
        NotificationHandleUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }
}
