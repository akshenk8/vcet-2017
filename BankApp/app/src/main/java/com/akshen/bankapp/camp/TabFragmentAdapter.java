package com.akshen.bankapp.camp;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.akshen.bankapp.R;


public class TabFragmentAdapter extends FragmentPagerAdapter {
    private Context mContext;
    private CampNewFragment campNewFragment;
    private CampHistoryFragment campHistoryFragment;

    public TabFragmentAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext=context;
        campNewFragment = new CampNewFragment();
        campHistoryFragment=new CampHistoryFragment();
    }

    public void createSuccessful(){
        campNewFragment=new CampNewFragment();
        campHistoryFragment.loadData();
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return campNewFragment;
            case 1:
                return campHistoryFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return mContext.getString(R.string.createNewCamp);
            case 1:
                return mContext.getString(R.string.pastCamps);
            default:
                return "";
        }
    }
}
