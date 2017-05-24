package org.wind.projectwindb;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;


public class WindMePagerAdapter extends FragmentStatePagerAdapter {
        int mNumOfTabs;

        public WindMePagerAdapter(FragmentManager fm, int NumOfTabs) {
            super(fm);
            this.mNumOfTabs = NumOfTabs;
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    WindMeFragment1 windMeFragment1 = new WindMeFragment1();
                    return windMeFragment1;
                case 1:
                    WindMeFragment2 windMeFragment2 = new WindMeFragment2();
                    return windMeFragment2;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }
}
