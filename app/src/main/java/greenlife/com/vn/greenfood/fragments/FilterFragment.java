package greenlife.com.vn.greenfood.fragments;

import android.app.Dialog;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.allattentionhere.fabulousfilter.AAH_FabulousFragment;
import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import greenlife.com.vn.greenfood.R;
import greenlife.com.vn.greenfood.models.FilterTag;


public class FilterFragment extends AAH_FabulousFragment {
    HashMap<String, Integer> applied_filters = new HashMap<>();
    List<TextView> textviews = new ArrayList<>();

    TabLayout tabs_types;
    ImageButton imgbtn_refresh, imgbtn_apply;
    SectionsPagerAdapter mAdapter;
    private DisplayMetrics metrics;
    private SensorManager sensorManager;

    private TextView currentTv = null;
    private TextView currentTimeTv = null;

    public static FilterFragment newInstance() {
        FilterFragment mff = new FilterFragment();
        return mff;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  applied_filters = ((NewFeedFragment)getContext()).getApplied_filters();
        metrics = this.getResources().getDisplayMetrics();

//        for (Map.Entry<String, List<String>> entry : applied_filters.entrySet()) {
//            Log.d("k9res", "from activity: "+entry.getKey());
//            for(String s: entry.getValue()){
//                Log.d("k9res", "from activity val: "+s);
//
//            }
//        }
    }

    @Override

    public void setupDialog(Dialog dialog, int style) {
        View contentView = View.inflate(getContext(), R.layout.view_filter, null);

        RelativeLayout rl_content = (RelativeLayout) contentView.findViewById(R.id.rl_content);
        LinearLayout ll_buttons = (LinearLayout) contentView.findViewById(R.id.ll_buttons);
        imgbtn_refresh = (ImageButton) contentView.findViewById(R.id.imgbtn_refresh);
        imgbtn_apply = (ImageButton) contentView.findViewById(R.id.imgbtn_apply);

        ViewPager vp_types = (ViewPager) contentView.findViewById(R.id.vp_types);
        tabs_types = (TabLayout) contentView.findViewById(R.id.tabs_types);

        imgbtn_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFilter(applied_filters);
            }
        });
        imgbtn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (TextView tv : textviews) {
                    tv.setTag("unselected");
                    tv.setBackgroundResource(R.drawable.chip_unselected);
                    tv.setTextColor(ContextCompat.getColor(getContext(), R.color.filters_chips));
                }
                applied_filters.clear();
            }
        });

        mAdapter = new SectionsPagerAdapter();
        vp_types.setOffscreenPageLimit(2);
        vp_types.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        tabs_types.setupWithViewPager(vp_types);


        //params to set
        setAnimationDuration(600); //optional; default 500ms
        setPeekHeight(300); // optional; default 400dp

        //get NormalNewFeedFragment and setCallback
        List<Fragment> fragments = getActivity().getSupportFragmentManager().getFragments();
        for (Fragment fragment : fragments) {
            if (fragment instanceof NewFeedFragment) {
                List<Fragment> childFragments = fragment.getChildFragmentManager().getFragments();
                for (Fragment cf : childFragments) {
                    if (cf instanceof NormalNewFeedFragment) {
                        setCallbacks((Callbacks) cf);
                        break;
                    }
                }
                break;
            }
        }


        setViewgroupStatic(ll_buttons); // optional; layout to stick at bottom on slide
        setViewPager(vp_types); //optional; if you use viewpager that has scrollview
        setViewMain(rl_content); //necessary; main bottomsheet view
        setMainContentView(contentView); // necessary; call at end before super
        super.setupDialog(dialog, style); //call super at last
    }

    public class SectionsPagerAdapter extends PagerAdapter {

        @Override
        public Object instantiateItem(ViewGroup collection, int position) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.view_filters_sorters, collection, false);
            FlexboxLayout fbl = (FlexboxLayout) layout.findViewById(R.id.fbl);
//            LinearLayout ll_scroll = (LinearLayout) layout.findViewById(R.id.ll_scroll);
//            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (metrics.heightPixels-(104*metrics.density)));
//            ll_scroll.setLayoutParams(lp);
            switch (position) {
                case 0:
                    inflateLayoutWithFilters("rating", fbl);
                    break;
                case 1:
                    inflateLayoutWithFilters("time", fbl);
                    break;
            }
            collection.addView(layout);
            return layout;

        }

        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((View) view);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Đánh giá";
                case 1:
                    return "Thời gian";
            }
            return "";
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }

    private void inflateLayoutWithFilters(final String filter_category, FlexboxLayout fbl) {
        List<FilterTag> keys = new ArrayList<>();
        switch (filter_category) {
            case "rating":
                keys = getUniqueRatingKeys();
                break;
            case "time":
                keys = getUniqueTimeKeys();
                break;
        }
        for (int i = 0; i < keys.size(); i++) {
            View subchild = getActivity().getLayoutInflater().inflate(R.layout.single_chip, null);
            final TextView tv = ((TextView) subchild.findViewById(R.id.txt_title));
            tv.setText(keys.get(i).getKey());
            final int finalI = i;
            final List<FilterTag> finalKeys = keys;
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (tv.getTag() != null && tv.getTag().equals("selected")) {
                        tv.setTag("unselected");
                        tv.setBackgroundResource(R.drawable.chip_unselected);
                        tv.setTextColor(ContextCompat.getColor(getContext(), R.color.filters_chips));
                        applied_filters.remove(filter_category);
                    } else {
                        tv.setTag("selected");
                        tv.setBackgroundResource(R.drawable.chip_selected);
                        tv.setTextColor(ContextCompat.getColor(getContext(), R.color.filters_header));

                        applied_filters.put(filter_category, finalKeys.get(finalI).getValue());

                        if (currentTv != null) {
                            currentTv.setTag("unselected");
                            currentTv.setBackgroundResource(R.drawable.chip_unselected);
                            currentTv.setTextColor(ContextCompat.getColor(getContext(), R.color.filters_chips));
                        }
                        currentTv =tv;
                    }
                }
            });
            if (applied_filters != null && applied_filters.get(filter_category) != null && applied_filters.get(filter_category).equals(keys.get(finalI))) {
                tv.setTag("selected");
                tv.setBackgroundResource(R.drawable.chip_selected);
                tv.setTextColor(ContextCompat.getColor(getContext(), R.color.filters_header));
            } else {
                tv.setBackgroundResource(R.drawable.chip_unselected);
                tv.setTextColor(ContextCompat.getColor(getContext(), R.color.filters_chips));
            }
            textviews.add(tv);

            fbl.addView(subchild);
        }


    }


    public List<FilterTag> getUniqueRatingKeys() {
        List<FilterTag> rating = new ArrayList<>();
        rating.add(new FilterTag("<= 1", 0));
        rating.add(new FilterTag("<= 2", 1));
        rating.add(new FilterTag("<= 3", 2));
        rating.add(new FilterTag("<= 4", 3));
        rating.add(new FilterTag("<= 5", 4));
        return rating;
    }

    public List<FilterTag> getUniqueTimeKeys() {
        List<FilterTag> rating = new ArrayList<>();

        rating.add(new FilterTag("<= 1", 1));
        rating.add(new FilterTag("<= 3", 3));
        rating.add(new FilterTag("<= 12", 12));
        rating.add(new FilterTag("> 12", 13));
        return rating;
    }

}
