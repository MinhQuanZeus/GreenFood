package greenlife.com.vn.greenfood.activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import greenlife.com.vn.greenfood.R;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class Profile extends AppCompatActivity {
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        String uId = mAuth.getCurrentUser().getUid();
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
}
