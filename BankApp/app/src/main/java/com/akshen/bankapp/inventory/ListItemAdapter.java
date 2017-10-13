package com.akshen.bankapp.inventory;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.akshen.bankapp.R;

import java.util.ArrayList;

public class ListItemAdapter extends ArrayAdapter<BloodGroupListItem> {

    public ListItemAdapter(Context context, ArrayList<BloodGroupListItem> words) {
        super(context, 0, words);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null)
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.inventory_list_item, parent, false);
        final View tmpView = listItemView;
        final BloodGroupListItem currentBloodGroupListItem = getItem(position);

        TextView bg = (TextView) tmpView.findViewById(R.id.blood_group_text_view);
        bg.setText(currentBloodGroupListItem.getBloodGroupDisplayString());

        if(currentBloodGroupListItem.getQuantity()==0){
            bg.setTextColor(Color.RED);
        }else{
            bg.setTextColor(Color.BLACK);
        }

        TextView qty = (TextView) tmpView.findViewById(R.id.value);
        qty.setText(currentBloodGroupListItem.getQuantity() + "");

        Button plus = (Button) tmpView.findViewById(R.id.plusbutton);
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentBloodGroupListItem.incrementQuantity();
                TextView qty = (TextView) tmpView.findViewById(R.id.value);
                qty.setText(currentBloodGroupListItem.getQuantity() + "");
            }
        });
        Button minus = (Button) tmpView.findViewById(R.id.minusbutton);
        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentBloodGroupListItem.decrementQuantity();
                TextView qty = (TextView) tmpView.findViewById(R.id.value);
                qty.setText(currentBloodGroupListItem.getQuantity() + "");
            }
        });
        return tmpView;
    }
}

