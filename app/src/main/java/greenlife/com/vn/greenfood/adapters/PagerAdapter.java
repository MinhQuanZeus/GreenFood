package greenlife.com.vn.greenfood.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import greenlife.com.vn.greenfood.fragments.NormalNewFeedFragment;


public class PagerAdapter extends FragmentStatePagerAdapter {
    private int numOfTabs;

    public PagerAdapter(FragmentManager fm, int numberOfTabs) {
        super(fm);
        this.numOfTabs = numberOfTabs;
    }


    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new NormalNewFeedFragment();
//            case 1:
//                return new NormalNewFeedFragment();
            default:
                return new NormalNewFeedFragment();
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}
