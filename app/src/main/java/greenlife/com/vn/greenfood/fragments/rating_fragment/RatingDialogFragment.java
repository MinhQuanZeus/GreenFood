package greenlife.com.vn.greenfood.fragments.rating_fragment;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import greenlife.com.vn.greenfood.R;
import greenlife.com.vn.greenfood.models.Post;
import greenlife.com.vn.greenfood.models.User;
import greenlife.com.vn.greenfood.models.UserRatePost;


/**
 * A simple {@link Fragment} subclass.
 */
public class RatingDialogFragment extends DialogFragment {

    RatingBar ratingBar;
    Button getRating;

    private DatabaseReference databaseReference;

    private StorageReference mStorageReference;
    private Post post;
    private String user_Current_Id;

    private UserRatePost cur_userRatepost;


    public static RatingDialogFragment ratingDialogFragment;

    String postID ;

    public static RatingDialogFragment getInstant() {
        if (ratingDialogFragment == null) {

            ratingDialogFragment = new RatingDialogFragment();
        }
        return ratingDialogFragment;
    }

    public RatingDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rating_dialog, container, false);
        getDialog().setTitle("Đánh Gía");
        ratingBar = (RatingBar) view.findViewById(R.id.rating_bar);
        getRating = (Button) view.findViewById(R.id.get_rating);
        getRating.setOnClickListener(new OnGetRatingClickListener());
        return view;
    }

    public synchronized void setData(String postId, String currentuserId) {
        this.postID = postId;
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("post").orderByChild("id").equalTo(postID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot itemDataSnaphot : dataSnapshot.getChildren()){
                    post = itemDataSnaphot.getValue(Post.class);
                    break;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        this.user_Current_Id = currentuserId;
    }

    private class OnGetRatingClickListener implements View.OnClickListener {
        @Override
        public synchronized void onClick(View view) {
            //RatingBar$getRating() returns float value, you should cast(convert) it to string to display in a view
            float rating = (float) ratingBar.getRating();
            databaseReference = FirebaseDatabase.getInstance().getReference();
            if (!isNetworkAvailable()) {
                Toast.makeText(getContext(), "Không có Internet", Toast.LENGTH_SHORT).show();
            } else {
                //HANDLE UPDATE RATE
                //step 1 : GET POST
                getPostUserCurrent(getContext(),post.getId(), user_Current_Id);
               // if (cur_userRatepost == null) {
                    float sum = post.getNumberRatePeople() * post.getRateAvgRating() + rating;
                    float avrRate = sum / (post.getNumberRatePeople() + 1);

                    post.setRateAvgRating(avrRate);
                    post.setNumberRatePeople(post.getNumberRatePeople() + 1);

                    //add userpost
                    pushUserpost(rating);
                    updatePost();


              //  } else {

               //     updateUserPost(rating);
              //      updatePostNew();
             //   }


            }
            Toast.makeText(getContext(), "Thành công ! Cám ơn sự đánh giá của bạn", Toast.LENGTH_SHORT).show();
            ratingDialogFragment.dismiss();

        }
    }

    private void updatePostNew() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("userrateposts").orderByChild("idPost").equalTo(postID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final ArrayList<Float> listAVG = new ArrayList<>();
                        for(DataSnapshot itemData : dataSnapshot.getChildren()){
                            UserRatePost tempPost = itemData.getValue(UserRatePost.class);
                            listAVG.add(tempPost.getRate());

                        }
                        float sum = 0;
                        for(Float a : listAVG){
                            sum = + a;
                        }
                        final float avg = sum/listAVG.size();

                        databaseReference.child("post").orderByChild("id").equalTo(postID)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for(DataSnapshot itemData : dataSnapshot.getChildren()){

                                            databaseReference.child("post").child(itemData.getKey()).child("rate").setValue(avg);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void updateUserPost(final float rating) {

        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("userrateposts").orderByChild("idPost").equalTo(postID).orderByChild("idUser").equalTo(user_Current_Id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot itemData : dataSnapshot.getChildren()){
                            databaseReference.child("userrateposts").child(itemData.getKey()).child("rate").setValue(rating);
                            break;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }



    private void pushUserpost(double rating) {
        DatabaseReference mDatabaseReference;

        mDatabaseReference = FirebaseDatabase.getInstance()
                .getReference()
                .child("userrateposts");
        String key = mDatabaseReference.push().getKey();

        mStorageReference = FirebaseStorage.getInstance()
                .getReference()
                .child(key);

        databaseReference = mDatabaseReference.push();

        databaseReference.child("id").setValue(mDatabaseReference.push().getKey());
        databaseReference.child("idUser").setValue(user_Current_Id);
        databaseReference.child("idPost").setValue(post.getId());
        databaseReference.child("rate").setValue(rating);

    }

    private synchronized void updatePost() {

        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("post").orderByChild("id").equalTo(postID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public synchronized void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot itemData : dataSnapshot.getChildren()){
                            databaseReference.child("post").child(itemData.getKey()).child("numberRatePeople").setValue(post.getNumberRatePeople());
                            databaseReference.child("post").child(itemData.getKey()).child("rateAvgRating").setValue(post.getRateAvgRating());

                            break;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
       // databaseReference.child("post").child(post.getId()).child("numberRatePeople").setValue(post.getNumberRatePeople());
       // databaseReference.child("post").child(post.getId()).child("rateAvgRating").setValue(post.getRateAvgRating());

    }

//    private void updateUser(){
//         final ArrayList listSum = new ArrayList<>();
//        databaseReference = FirebaseDatabase.getInstance().getReference();
//        databaseReference.child("userrateposts").addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//
//                        for(DataSnapshot itemData : dataSnapshot.getChildren()){
//
//                            UserRatePost userRatePost = itemData.getValue(UserRatePost.class);
//
//                            databaseReference.child("post").orderByChild("id ").equalTo(userRatePost.getIdPost())
//                                    .addListenerForSingleValueEvent(new ValueEventListener() {
//                                        @Override
//                                        public void onDataChange(DataSnapshot dataSnapshot) {
//                                            for(DataSnapshot itemData : dataSnapshot.getChildren()){
//                                                Post temPost = itemData.getValue(Post.class);
//                                                if(temPost.getUserID().equals(post.getUserID())){
//                                                    listSum.add(temPost.getRateAvgRating());
//                                                }
//
//                                                break;
//                                            }
//                                        }
//
//                                        @Override
//                                        public void onCancelled(DatabaseError databaseError) {
//
//                                        }
//                                    });
//
//
//
//
//                        }
//
//
//
//
//
//                            float sum = 0 ;
//
//                            for (Object a: listSum) {
//                                sum = + (Float) a;
//                            }
//
//                            databaseReference.child("users").child(user_Current_Id).setValue(sum/listSum.size())
//                            break;
//
//
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
//
//    }


    private void getPostUserCurrent(final Context context, String postID, String userId) {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("userrateposts").orderByChild("idPost").equalTo(postID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot itemData : dataSnapshot.getChildren()){
                            UserRatePost userRatePost = itemData.getValue(UserRatePost.class);
                            if(userRatePost.getIdUser().equals(user_Current_Id)){
                                cur_userRatepost = userRatePost;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm =
                (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

}
