package greenlife.com.vn.greenfood.fragments.rating_fragment;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;

import greenlife.com.vn.greenfood.R;
import greenlife.com.vn.greenfood.models.Post;
import greenlife.com.vn.greenfood.models.User;


/**
 * A simple {@link Fragment} subclass.
 */
public class RatingDialogFragment extends DialogFragment {

    RatingBar ratingBar;
    Button getRating;

    private Post post ;
    private String user_Current_Id;

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

    private class OnGetRatingClickListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            //RatingBar$getRating() returns float value, you should cast(convert) it to string to display in a view
            String rating = String.valueOf(ratingBar.getRating());
            double rateing = ratingBar.getRating();

            ratingDialogFragment.dismiss();
        }
    }

    public void setData(Post post , String currentuserId){
        this.post = post;
        this.user_Current_Id = currentuserId;
    }

}
