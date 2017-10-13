/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.akshen.bankapp.camp;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.akshen.bankapp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class HistoryListAdapter extends ArrayAdapter<HistoryHolder> {
    CampMainActivity cp;
    public HistoryListAdapter(Context context, ArrayList<HistoryHolder> events,CampMainActivity cp) {
        super(context, 0, events);
        this.cp=cp;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.camp_past_list_item, parent, false);
        }

        HistoryHolder currentEvent = getItem(position);
        final int pos = position;
        listItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HistoryHolder currentEvent = getItem(pos);
                Intent i = new Intent(cp, HistoryCampViewActivity.class);
                i.putExtra("data", currentEvent);
                cp.startActivity(i);
            }
        });

        GradientDrawable background = (GradientDrawable) ContextCompat.getDrawable(getContext(), R.drawable.home_list_item_radius).mutate();
        int color = ContextCompat.getColor(getContext(), R.color.listItemBackgroundColor);
        background.setColor(color);
        listItemView.setBackground(background);

        TextView name_of_event = (TextView) listItemView.findViewById(R.id.name_of_event);
        name_of_event.setText(currentEvent.get_nameofevent());

        TextView start_date = (TextView) listItemView.findViewById(R.id.start_date);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        start_date.setText(getContext().getString(R.string.starts) + sdf.format(currentEvent.get_startdate()));

        TextView end_date = (TextView) listItemView.findViewById(R.id.end_date);
        end_date.setText(getContext().getString(R.string.ends) + sdf.format(currentEvent.get_enddate()));

        TextView status = (TextView) listItemView.findViewById(R.id.status);
        status.setText(getContext().getString(currentEvent.get_status().statusString()));

        return listItemView;
    }
}