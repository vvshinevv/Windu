package org.wind.projectwindb;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;


public class WindyouPageAdapter extends FragmentStatePagerAdapter {
        int mNumOfTabs;

         public WindyouPageAdapter(FragmentManager fm, int NumOfTabs) {
            super(fm);
            this.mNumOfTabs = NumOfTabs;
         }

          @Override
          public Fragment getItem(int position) {
              switch (position) {
                  case 0:
                        WindYouFragment1 windYouFragment1 = new WindYouFragment1();
                        return windYouFragment1;
                    case 1:
                        WindYouFragment2 windYouFragment2 = new WindYouFragment2();
                        return windYouFragment2;
                    default:
                        return null;
              }
         }

        @Override
        public int getCount() {
        return mNumOfTabs;
    }
}
