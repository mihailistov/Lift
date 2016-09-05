package ca.mihailistov.lift;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

/**
 * Created by mihai on 16-09-05.
 */
public class LiftPagerAdapter extends SmartFragmentStatePagerAdapter {
    private static int NUM_ITEMS = 7;

    public LiftPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    // Returns total number of pages
    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    // Returns the fragment to display for that page
    @Override
    public Fragment getItem(int position) {
        return LiftFragmentPage.newInstance(position);
    }
}
