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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import greenlife.com.vn.greenfood.R;
import greenlife.com.vn.greenfood.activities.AddFoodActivity;
import greenlife.com.vn.greenfood.models.Post;
import greenlife.com.vn.greenfood.models.User;
import greenlife.com.vn.greenfood.models.UserPost;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;


/**
 * A simple {@link Fragment} subclass.
 */
public class RatingDialogFragment extends DialogFragment {

    RatingBar ratingBar;
    Button getRating;

    private  DatabaseReference databaseReference;
    private Post post ;
    private String user_Current_Id;

    private UserPost cur_userpost;

    private User userSeller;

    public static RatingDialogFragment  ratingDialogFragment;

    public static RatingDialogFragment getInstant(){
        if(ratingDialogFragment == null){
            ratingDialogFragment = new RatingDialogFragment();
        }
        return  ratingDialogFragment;
    }

    public RatingDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rating_dialog, container, false);
        getDialog().setTitle("Rate us");
        ratingBar = (RatingBar) view.findViewById(R.id.rating_bar);
        getRating = (Button) view.findViewById(R.id.get_rating);
        getRating.setOnClickListener(new OnGetRatingClickListener());
        return view;
    }

    public void setData(Post post , String currentuserId){
        this.post = post;
        this.user_Current_Id = currentuserId;
    }

    private class OnGetRatingClickListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            //RatingBar$getRating() returns float value, you should cast(convert) it to string to display in a view
            double rating = ratingBar.getRating();

            if(!isNetworkAvailable()){
                Toast.makeText(getContext(), "Không có Internet", Toast.LENGTH_SHORT).show();
            }else {
                //HANDLE UPDATE RATE

                //step 1 : GET POST
                getPostCurrent(getContext(), post.getId());

                getPostUserCurrent(getContext(), post.getId(), user_Current_Id);

                if (cur_userpost == null) {
                    double sum = post.getNumberRatePeople() * post.getRateAvgRating() + rating;
                    double avrRate = sum / (post.getNumberRatePeople() + 1);

                    post.setRateAvgRating(avrRate);
                    post.setNumberRatePeople(post.getNumberRatePeople() + 1);

                    //add userpost

                    updatePost();
                    pushUserpost();
                    updateUserseller();



                } else {
                    //set userseller
                    getUser(getContext(), user_Current_Id);

                    cur_userpost.setRate(rating);
                    //push lên

                    //getall userpost chia trung binh
                    //add userpost

                        updatePost();
                        pushUserpost();
                        updateUserseller();

                }

                //UPDATE POST


                //UPDATE USER

            }
                ratingDialogFragment.dismiss();

        }
    }

    private void updateUserseller() {
    }

    private void pushUserpost() {

    }

    private void updatePost() {
    }


    private void getUser(final Context context, String userID) {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("users")
                .child(userID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        userSeller = dataSnapshot.getValue(User.class);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }

    private void getPostCurrent(final Context context, String postID) {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("post")
                .child(postID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        post = dataSnapshot.getValue(Post.class);
                        
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void getPostUserCurrent(final Context context, String postID , String userId) {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("userposts").child(postID).child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        cur_userpost = dataSnapshot.getValue(UserPost.class);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm =
                (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

}
