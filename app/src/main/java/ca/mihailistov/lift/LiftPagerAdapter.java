package ca.mihailistov.lift;

import android.support.v4.app.FragmentManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by mihai on 16-09-05.
 */
public class LiftPagerAdapter extends SmartFragmentStatePagerAdapter {
    private static int NUM_ITEMS = 30;

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
    public LiftFragmentPage getItem(int position) {
        return LiftFragmentPage.newInstance(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dfEEE = new SimpleDateFormat("EEE");
        SimpleDateFormat dfd = new SimpleDateFormat("d");
        c.add(Calendar.DAY_OF_WEEK, position-10);

        return dfEEE.format(c.getTime()).toUpperCase() + "\n" + dfd.format(c.getTime());
    }
}
