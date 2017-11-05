package greenlife.com.vn.greenfood.fragments;


import android.app.DownloadManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import greenlife.com.vn.greenfood.R;
import greenlife.com.vn.greenfood.activities.FoodDetailActivity;
import greenlife.com.vn.greenfood.adapters.NotificationAdapter;
import greenlife.com.vn.greenfood.events.GetUserFromIDListener;
import greenlife.com.vn.greenfood.models.Notification;
import greenlife.com.vn.greenfood.models.Post;
import greenlife.com.vn.greenfood.models.User;
import greenlife.com.vn.greenfood.network.models.order.Order;
import greenlife.com.vn.greenfood.utils.MapsUltils;

import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment {

    private static final String TAG = NotificationFragment.class.getSimpleName();
    public static final int RequestPermissionCode = 1;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    FirebaseRecyclerAdapter<Order, NotificationViewHolder> firebaseRecyclerAdapter;

    public NotificationFragment() {
        // Required empty public constructor

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        setupUI(view);
        loadData();
        if(!checkPermission()){
            requestPermission();
        }
        return view;
    }

    private void loadData() {
        firebaseAuth = FirebaseAuth.getInstance();
        Log.d(TAG, "my UID : " + firebaseAuth.getCurrentUser().getUid());
        databaseReference = FirebaseDatabase.getInstance()
                .getReference()
                .child("users")
                .child(firebaseAuth.getCurrentUser().getUid())
                .child("orders");
    }

    private void setupUI(View view) {
        toolbar = view.findViewById(R.id.tb_main);
        toolbar.setTitle("Notification");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        recyclerView = view.findViewById(R.id.rv_notification);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),1,GridLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(gridLayoutManager);

    }


    @Override
    public void onStart() {
        super.onStart();
        Query query = databaseReference.orderByChild("time");
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Order, NotificationViewHolder>(
                Order.class,
                R.layout.item_notification,
                NotificationViewHolder.class,
                query
        ) {
            @Override
            public Order getItem(int position) {
                return super.getItem(getItemCount() - (position+1));
            }

            @Override
            protected void populateViewHolder(NotificationViewHolder viewHolder, final Order model, int position) {
                viewHolder.loadData(getContext(), model);
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(), FoodDetailActivity.class).putExtra("post", model.getPostId());
                        startActivity(intent);
                    }
                });
                viewHolder.itemView.findViewById(R.id.btn_call).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onCallPhone(model.getPhone());
                    }
                });

            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder{
        View itemView;
        private ImageView ivNotification;
        private TextView tvTitle;
        private TextView tvDescription;
        private Button btnPhone;

        public NotificationViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            setupUI();
        }

        private void setupUI() {
            ivNotification = itemView.findViewById(R.id.iv_notification);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDescription = itemView.findViewById(R.id.tv_description);
            btnPhone = itemView.findViewById(R.id.btn_call);
        }

        private void loadData(Context context, final Order order){
            //notification image loader
            Picasso.with(context)
                    .load(order.getFoodImgLink())
                    .into(ivNotification);
            if(order.getType().equals("order")){
                MapsUltils.getUser(context, order.getBuyerID(), new GetUserFromIDListener() {
                    @Override
                    public void getUserFromID(final User user) {
                        tvTitle.setText(user.getName() + " order "  + " " + order.getFoodName());
                        tvDescription.setText(order.getTime());
                    }
                });
            } else {
                MapsUltils.getUser(context, order.getSellerID(), new GetUserFromIDListener() {
                    @Override
                    public void getUserFromID(User user) {
                        tvTitle.setText("Bạn đã order "  + " " + order.getFoodName() + " của " + user.getName());
                        tvDescription.setText(order.getTime());
                    }
                });
            }

        }
    }

    public void onCallPhone(String phone){
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        callIntent.setData(Uri.parse("tel:" + phone));
        startActivity(callIntent);
    }

    @Override
    public void onResume() {
        firebaseRecyclerAdapter.notifyDataSetChanged();
        super.onResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length > 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(getActivity(), "Permission Granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getActivity(), "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(getActivity(), new
                String[]{CALL_PHONE}, RequestPermissionCode);
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                CALL_PHONE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

}
