package com.example.tryauth;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

class SelectionPagerAdapter extends FragmentPagerAdapter {
    public SelectionPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){                      //if position = 0 then NotificationFragment if position = 2 the ProgressFragment
            case 0:
                NotificationFragment notificationFragment = new NotificationFragment();
                return notificationFragment;
            case 1:
                ProgressFragment progressFragment = new ProgressFragment();
                return progressFragment;
            default:
                    return null;
        }
    }

    @Override
    public int getCount() {
        return 2;       //No of Tabs
    }

    public CharSequence getPageTitle(int position){

        switch (position){                      //if position = 0 then NotificationFragment if position = 2 the ProgressFragment
            case 0:
                return "NOTIFICATIONS";
            case 1:
                return "PROGRESS";
            default:
                return null;
        }
    }
}
