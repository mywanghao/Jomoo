package com.ucas.jomoo.tools;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ucas.jomoo.ui.ButtonControlFragment;
import com.ucas.jomoo.ui.GestureControlFragment;
import com.ucas.jomoo.ui.HelpTextFragment;

/**
 * Created by ivanchou on 7/31/15.
 */
public class ControlPagerAdapter extends FragmentPagerAdapter {
    FragmentManager fm;

    public final static int HELP_FRAGMENT =  2 ;

    public ControlPagerAdapter(FragmentManager fm) {
        super(fm);
        this.fm = fm;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return GestureControlFragment.newInstance(0);
            case 1:
                return ButtonControlFragment.newInstance(1);
            case 2:
                return HelpTextFragment.newInstance();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
