package com.a8080.bloodbank.bloodbankuser.home;

/**
 * Created by DELL_PC on 10/13/2017.
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.a8080.bloodbank.bloodbankuser.R;
import com.a8080.bloodbank.bloodbankuser.history.HistoryActivity;
import com.a8080.bloodbank.bloodbankuser.nearby.NearbyActivity;

import java.util.ArrayList;



public class HomeAdapter extends ArrayAdapter<HomeItemHolder> {



    private int mColorResourceId;



    public HomeAdapter(Context context, ArrayList<HomeItemHolder> homeItemHolders, int colorResourceId) {
        super(context, 0, homeItemHolders);
        mColorResourceId = colorResourceId;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.home_list_item, parent, false);

        }



        HomeItemHolder currentHomeItemHolder = getItem(position);
        TextView headingTextView = (TextView) listItemView.findViewById(R.id.home_heading_text_view);
        headingTextView.setText(currentHomeItemHolder.getHeading());

        TextView contentTextView = (TextView) listItemView.findViewById(R.id.home_content_text_view);
        contentTextView.setText(currentHomeItemHolder.getContent());





        ImageView imageView = (ImageView) listItemView.findViewById(R.id.image);
        if (currentHomeItemHolder.hasImage()) {
            imageView.setImageResource(currentHomeItemHolder.getImageResourceId());
            imageView.setVisibility(View.VISIBLE);

        } else {
            imageView.setVisibility(View.GONE);
        }



        View textContainer = listItemView.findViewById(R.id.text_container);
        GradientDrawable background = (GradientDrawable) ContextCompat.getDrawable(getContext(), R.drawable.home_list_item_radius).mutate();

        int color = ContextCompat.getColor(getContext(), mColorResourceId);
        background.setColor(color);
        textContainer.setBackground(background);

        //textContainer.setBackgroundColor(color);



        if (currentHomeItemHolder.getHeading().equals(getContext().getString(R.string.homeListString1))) {
            textContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getContext(), NearbyActivity.class);

                    getContext().startActivity(i);

                }
            });

        } else if (currentHomeItemHolder.getHeading().equals(getContext().getString(R.string.homeListString2))) {

            textContainer.setOnClickListener(new View.OnClickListener() {

                @Override

                public void onClick(View v) {

                    //Intent i = new Intent(getContext(), CampMainActivity.class);

                    //getContext().startActivity(i);

                }

            });

        } else if (currentHomeItemHolder.getHeading().equals(getContext().getString(R.string.homeListString3))) {

            textContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getContext(), HistoryActivity.class);
                    getContext().startActivity(i);
                }
            });

        } else if (currentHomeItemHolder.getHeading().equals(getContext().getString(R.string.homeListString4))) {
            textContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent i = new Intent(getContext(), DonorActivity.class);

//                    getContext().startActivity(i);

                }
            });

        } else if (currentHomeItemHolder.getHeading().equals(getContext().getString(R.string.homeListString5))) {
            textContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent i = new Intent(getContext(), InventoryActivity.class);

                    //getContext().startActivity(i);

                }
            });
        }
        return listItemView;
    }

}