package com.akshen.bankapp.inventory;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.akshen.bankapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TabFragmentAdapter extends FragmentPagerAdapter {
    private Context mContext;
    private ArrayList<BloodDataFragment> fragments;

    public TabFragmentAdapter(Context context, FragmentManager fm, JSONObject downloadedData) throws JSONException {
        super(fm);
        mContext = context;
        fragments = new ArrayList<>();

        //tabs=mContext.getResources().getStringArray(R.array.inventory_items);
        String[] tabs = new String[]{mContext.getString(R.string.whole_blood), mContext.getString(R.string.packed_red_blood_cells),
                mContext.getString(R.string.fresh_frozen_plasma), mContext.getString(R.string.platelets_concentrates),
                mContext.getString(R.string.cryoprecipitate)};
        BloodDataFragment.InventoryType[] inventoryTypes = {BloodDataFragment.InventoryType.WBC, BloodDataFragment.InventoryType.RBC,
                BloodDataFragment.InventoryType.FFP, BloodDataFragment.InventoryType.PC, BloodDataFragment.InventoryType.CRY};

        int size = 0;
        while (size < tabs.length) {
            BloodDataFragment bdf = new BloodDataFragment();
            String type=downloadedData.getString(inventoryTypes[size].getInventoryType());
            JSONObject data;
            if(type.equals("")){
                data=null;
            }else{
                data=new JSONObject(type);
            }
            bdf.setMetaData(tabs[size], inventoryTypes[size],data,mContext);
            fragments.add(bdf);
            size++;
        }
    }

    /*public void setDownloadedData(JSONObject res){
        try{
            for(BloodDataFragment b:fragments){
                String type=b.getInventoryType().getInventoryType();
                if(res.has(type)){
                    JSONObject data=new JSONObject(res.getString(type));
                    b.setDownloadedData(data);
                }
            }
        }catch (Exception e){
            Log.e("error",e.toString());
        }
    }*/

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return fragments.get(position).getPageTitle();
    }
}
