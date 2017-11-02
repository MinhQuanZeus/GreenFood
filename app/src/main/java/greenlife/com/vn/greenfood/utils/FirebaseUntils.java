package greenlife.com.vn.greenfood.utils;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import greenlife.com.vn.greenfood.models.User;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * Created by QuanT on 11/2/2017.
 */

public class FirebaseUntils {
    public static FirebaseUntils firebaseUntils;
    private static User user;
    public static FirebaseUntils getInstance(){
        if(firebaseUntils == null){
            user = new User();
            firebaseUntils = new FirebaseUntils();
        }
        return firebaseUntils;
    }
    public User getUserByID(String userID){
        getUser(userID);
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    private void getUser(final String userID) {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("users")
                .child(userID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        setUser(user);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}
