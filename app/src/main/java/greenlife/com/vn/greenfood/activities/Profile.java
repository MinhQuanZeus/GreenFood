package greenlife.com.vn.greenfood.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import greenlife.com.vn.greenfood.R;
import greenlife.com.vn.greenfood.models.User;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class Profile extends AppCompatActivity implements View.OnClickListener{
    private FirebaseAuth mAuth;
    private User user;
    ImageView isAvatar;
    TextView userName;
    TextView description;
    TextView link;
    TextView noPost;
    TextView noFollow;
    String defineUser;
    LinearLayout change_setting;
    Button btnfollow;
    TextView btn_fixInfor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mAuth = FirebaseAuth.getInstance();
        defineUser = getIntent().getStringExtra("uerID");
        String uId = mAuth.getCurrentUser().getUid();
        isAvatar = (ImageView)findViewById(R.id.iv_profile_image);
        userName = (TextView)findViewById(R.id.tv_username);
        description = (TextView)findViewById(R.id.tv_description);
        link = (TextView)findViewById(R.id.tv_link);
        noPost = (TextView)findViewById(R.id.tv_NoPost);
        noFollow = (TextView)findViewById(R.id.tv_NoFollow);
        change_setting = (LinearLayout) findViewById(R.id.ln_edit_profile);
        btnfollow = (Button)findViewById(R.id.btn_follow);
        btn_fixInfor = (TextView)findViewById(R.id.btn_fixInfor);
        if(defineUser==null || defineUser.equalsIgnoreCase(uId)){
            getUser(this,uId);
            change_setting.setVisibility(View.VISIBLE);
            btnfollow.setVisibility(View.INVISIBLE);
        }
        else {
            getUser(this,defineUser);
            change_setting.setVisibility(View.INVISIBLE);
            btnfollow.setVisibility(View.VISIBLE);
        }

        btn_fixInfor.setOnClickListener(this);

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
                            description.setText("");
                        }
                        // set link
                        if(user.getLink()!=null){
                            link.setText(user.getLink());
                        }
                        else {
                            link.setText("");
                        }
//                        noPost.setText(user.);
//                        noFollow.setText(user.);

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
           // case R.id.
        }
    }
}
