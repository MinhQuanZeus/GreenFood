package greenlife.com.vn.greenfood.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import greenlife.com.vn.greenfood.R;
import greenlife.com.vn.greenfood.fragments.NormalNewFeedFragment;
import greenlife.com.vn.greenfood.models.Post;
import greenlife.com.vn.greenfood.models.User;
import greenlife.com.vn.greenfood.utils.LibrarySupportManager;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener{
    private FirebaseAuth mAuth;
    private User user;
    Toolbar statusToolBar;
    ImageView isAvatar;
    TextView userName;
    TextView description;
    TextView link;
    TextView noPost;
    TextView noFollow;
    String defineUser;
    LinearLayout change_setting;
    TextView btn_fixInfor;
    ImageView btnLogout;
    private RecyclerView rvNewFeed;
    private DatabaseReference databaseReference;
    int postCount=0;
    String uId="";
    FirebaseRecyclerAdapter<Post, PostViewHolder> firebaseRecyclerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mAuth = FirebaseAuth.getInstance();
        defineUser = getIntent().getStringExtra("userId");
        uId = mAuth.getCurrentUser().getUid();
        isAvatar = (ImageView)findViewById(R.id.iv_profile_image);
        userName = (TextView)findViewById(R.id.tv_username);
        description = (TextView)findViewById(R.id.tv_description);
        link = (TextView)findViewById(R.id.tv_link);
        noPost = (TextView)findViewById(R.id.tv_NoPost);
        change_setting = (LinearLayout) findViewById(R.id.ln_edit_profile);
        btn_fixInfor = (TextView)findViewById(R.id.btn_fixInfor);
        rvNewFeed = findViewById(R.id.rv_image_food);
        statusToolBar = findViewById(R.id.tb_status_profile);
        btnLogout = findViewById(R.id.btn_Settings);
        if(defineUser==null || defineUser.equalsIgnoreCase(uId)){
            defineUser = uId;
            getUser(this,uId);
            change_setting.setVisibility(View.VISIBLE);
        }
        else {
            getUser(this,defineUser);
            change_setting.setVisibility(View.VISIBLE);
        }
        noPost.setText(postCount+"");
        btn_fixInfor.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
        // list post
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),3,GridLayoutManager.VERTICAL,false);
        rvNewFeed.setLayoutManager(gridLayoutManager);
        loadData();
        displayListPosts();
        // toolbar
        statusToolBar.setTitle("Cá nhân");
        statusToolBar.setTitleTextColor(getResources().getColor(R.color.white));
        statusToolBar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        setSupportActionBar(statusToolBar);
        statusToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        // hyper link text
        TextView textView =(TextView)findViewById(R.id.tv_link);
        textView.setClickable(true);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        String text = "<a href='http://www.google.com'> Google </a>";
        textView.setText(Html.fromHtml(text));
    }

    public void setUser(User user) {
        this.user = user;
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
                        Picasso.with(context)
                                .load(user.getAvatar())
                                .transform(new CropCircleTransformation())
                                .into(isAvatar);
                        // set user name
                        userName.setText(user.getName());
                        // set description
                        if(user.getDescription() != null){
                            description.setText(user.getDescription());
                        }
                        else {
                            description.setText("intro");
                        }
                        // set link
                        if(user.getLink()!=null){
                            link.setText(user.getLink());
                        }
                        else {
                            link.setText("social web");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_fixInfor:
                Log.d("Profile", "onclick");
                Intent changeProfile = new Intent(this,ChangeProfileActivity.class);
                startActivity(changeProfile);
                break;
            case R.id.btn_Settings:
                AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                builder1.setMessage("Thoát khỏi Green Food");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Đồng ý",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent logout = new Intent(getApplicationContext(),LoginActivity.class);
                                startActivity(logout);
                            }
                        });

                builder1.setNegativeButton(
                        "Hủy",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
                break;
            // case R.id.
        }
    }

    // setUI list post
    private void setupUI(View view) {
        rvNewFeed = view.findViewById(R.id.rv_newfeed);
        rvNewFeed.setHasFixedSize(true);
        //ivLoading = (SpinKitView)view.findViewById(R.id.iv_loading);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),3,GridLayoutManager.VERTICAL,false);
        rvNewFeed.setLayoutManager(gridLayoutManager);
    }
    @Override
    public void onResume() {
        firebaseRecyclerAdapter.notifyDataSetChanged();
        super.onResume();
    }
    @Override
    public void onStart() {
        super.onStart();

        //get data from firebase
        Query query = databaseReference.orderByChild("time");
        updateNewFeedByQuery(query);
    }
    public void updateNewFeedByQuery(Query query){
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Post, PostViewHolder>(
                Post.class,
                R.layout.item_food_in_profile,
                PostViewHolder.class,
                query
        ) {
            @Override
            public Post getItem(int position) {
                noPost.setText(getItemCount()+"");
                return super.getItem(getItemCount() - (position+1));
            }

            @Override
            protected void populateViewHolder(PostViewHolder viewHolder, final Post model, int position) {
                //load data in view holder
                viewHolder.loadData(getApplicationContext(), model);
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), FoodDetailActivity.class).putExtra("post",model.getId());
                        Log.d("Detail","Des: "+model.getDescription());
                        startActivity(intent);
                    }
                });
            }
        };
        rvNewFeed.setAdapter(firebaseRecyclerAdapter);
    }
    public static class PostViewHolder extends RecyclerView.ViewHolder{
        private ImageView ivFood;
        //private TextView postID;
        View itemView;
        public PostViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            setupUI();
        }
        private void setupUI() {
            //Bind itemView
            //postID = itemView.findViewById(R.id.tv_idpost);
            ivFood = itemView.findViewById(R.id.iv_post);
        }
        private void loadData(Context context, Post post){
            //1. Load title(food name), description, price
            //postID.setText(post.getId());
            Picasso.with(context)
                    .load(post.getImage())
                    .into(ivFood);
        }

    }
    private void loadData() {
        databaseReference = FirebaseDatabase.getInstance()
                .getReference()
                .child("post");
    }
    public void displayListPosts(){
        Query query = databaseReference.orderByChild("userID").equalTo(defineUser);
        updateNewFeedByQuery(query);
    }
}
