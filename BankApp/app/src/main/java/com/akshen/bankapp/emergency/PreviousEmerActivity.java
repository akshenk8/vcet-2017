package com.akshen.bankapp.emergency;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.akshen.bankapp.R;
import com.akshen.bankapp.misc.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

public class PreviousEmerActivity extends AppCompatActivity {
    JSONObject type1,type2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.previous_emer_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("User Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        Integer count=0;
//        while (cursor.moveToNext()) {
//            String date = formatdate(cursor.getString(2));// get the first variable
//            Double weight_kg = roundTwoDecimals(cursor.getDouble(4));// get the second variable
//// Create the table row
//            TableRow tr = new TableRow(this);
//            if(count%2!=0) tr.setBackgroundColor(Color.GRAY);
//            tr.setId(100+count);
//            tr.setLayoutParams(new TableRow.LayoutParams(
//                    TableRow.LayoutParams.FILL_PARENT,
//                    TableRow.LayoutParams.WRAP_CONTENT));
//
////Create two columns to add as table data
//            // Create a TextView to add date
//            TextView labelDATE = new TextView(this);
//            labelDATE.setId(200+count);
//            labelDATE.setText(date);
//            labelDATE.setPadding(2, 0, 5, 0);
//            labelDATE.setTextColor(Color.WHITE);
//            tr.addView(labelDATE);
//            TextView labelWEIGHT = new TextView(this);
//            labelWEIGHT.setId(200+count);
//            labelWEIGHT.setText(weight_kg.toString());
//            labelWEIGHT.setTextColor(Color.WHITE);
//            tr.addView(labelWEIGHT);
//
//// finally add this to the table row
//            tl.addView(tr, new TableLayout.LayoutParams(
//                    TableRow.LayoutParams.FILL_PARENT,
//                    LayoutParams.WRAP_CONTENT));
//            count++;
//        }


        SharedPreferences sp = new Utils().getApplicationPreference(this);

        TableLayout tl = (TableLayout) findViewById(R.id.main_table);

        TableRow tr_head = new TableRow(this);

        tr_head.setBackgroundColor(Color.GRAY);
        tr_head.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.FILL_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));

        TextView label_date = new TextView(this);

        label_date.setText("BANK NAME");
        label_date.setTextColor(Color.WHITE);
        label_date.setPadding(12, 12, 12, 12);
        tr_head.addView(label_date);// add the column to the table row here

        TextView label_weight_kg = new TextView(this);

        label_weight_kg.setText("Status"); // set the text for the header
        label_weight_kg.setTextColor(Color.WHITE); // set the color
        label_weight_kg.setPadding(5, 5, 5, 5); // set the padding (if required)
        tr_head.addView(label_weight_kg); // add the column to the table row here

        tl.addView(tr_head, new TableLayout.LayoutParams(
                TableRow.LayoutParams.FILL_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));

        //Type 2

        TableLayout t2 = (TableLayout) findViewById(R.id.main_table2);

        TableRow tr_head2 = new TableRow(this);

        tr_head2.setBackgroundColor(Color.GRAY);
        tr_head2.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.FILL_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));

        TextView label_date2 = new TextView(this);

        label_date2.setText("User NAME");
        label_date2.setTextColor(Color.WHITE);
        label_date2.setPadding(12, 12, 12, 12);
        tr_head2.addView(label_date2);// add the column to the table row here

        TextView label_weight_kg2 = new TextView(this);

        label_weight_kg2.setText("Status"); // set the text for the header
        label_weight_kg2.setTextColor(Color.WHITE); // set the color
        label_weight_kg2.setPadding(5, 5, 5, 5); // set the padding (if required)
        tr_head2.addView(label_weight_kg2); // add the column to the table row here

        t2.addView(tr_head2, new TableLayout.LayoutParams(
                TableRow.LayoutParams.FILL_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));



        try {
            if (sp.contains("type1")) {
                type1 = new JSONObject(sp.getString("type1", ""));

                if(type1.getString("msg").equalsIgnoreCase("success")){
                    JSONArray jsonArray = type1.getJSONArray("banks");

                    for(int i=0;i<jsonArray.length();i++){

                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        TableRow tr = new TableRow(this);
                        tr.setBackgroundColor(Color.WHITE);
                        tr.setId(100+i);
                        tr.setLayoutParams(new TableRow.LayoutParams(
                                TableRow.LayoutParams.FILL_PARENT,
                                TableRow.LayoutParams.WRAP_CONTENT));


                        TextView labelDATE = new TextView(this);
                        labelDATE.setId(200+i);
                        labelDATE.setText(jsonObject.getString("name"));
                        labelDATE.setPadding(2, 0, 5, 0);
                        labelDATE.setTextColor(Color.BLACK);
                        tr.addView(labelDATE);

                        ImageView labelWEIGHT = new ImageView(this);
                        labelWEIGHT.setId(200+i);
                        if(jsonObject.getString("status").equalsIgnoreCase("2")){
                            //Accecpting
                            labelWEIGHT.setImageResource(R.drawable.completed);
                        }
                        else if(jsonObject.getString("status").equalsIgnoreCase("1")){
                            //Rejecting
                            labelWEIGHT.setImageResource(R.drawable.rejecting);
                        }
                        else {
                            //pending
                            labelWEIGHT.setImageResource(R.drawable.pending);
                        }

                        tr.addView(labelWEIGHT);

                        // finally add this to the table row
                        tl.addView(tr, new TableLayout.LayoutParams(
                                TableRow.LayoutParams.FILL_PARENT,
                                TableRow.LayoutParams.WRAP_CONTENT));
                    }
                }


            }
            if (sp.contains("type2")) {
                type2 = new JSONObject(sp.getString("type2", ""));

                if(type2.getString("msg").equalsIgnoreCase("success")){
                    JSONArray jsonArray = type2.getJSONArray("users");

                    for(int i=0;i<jsonArray.length();i++){

                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        TableRow tr = new TableRow(this);
                        tr.setBackgroundColor(Color.WHITE);
                        tr.setId(100+i);
                        tr.setLayoutParams(new TableRow.LayoutParams(
                                TableRow.LayoutParams.FILL_PARENT,
                                TableRow.LayoutParams.WRAP_CONTENT));


                        TextView labelDATE = new TextView(this);
                        labelDATE.setId(200+i);
                        labelDATE.setText(jsonObject.getString("name"));
                        labelDATE.setPadding(2, 0, 5, 0);
                        labelDATE.setTextColor(Color.BLACK);
                        tr.addView(labelDATE);

                        ImageView labelWEIGHT = new ImageView(this);
                        labelWEIGHT.setId(200+i);
                        if(jsonObject.getString("status").equalsIgnoreCase("2")){
                            //Accecpting
                            labelWEIGHT.setImageResource(R.drawable.completed);
                        }
                        else if(jsonObject.getString("status").equalsIgnoreCase("1")){
                            //Rejecting
                            labelWEIGHT.setImageResource(R.drawable.rejecting);
                        }
                        else {
                            //pending
                            labelWEIGHT.setImageResource(R.drawable.pending);
                        }

                        tr.addView(labelWEIGHT);

                        // finally add this to the table row
                        t2.addView(tr, new TableLayout.LayoutParams(
                                TableRow.LayoutParams.FILL_PARENT,
                                TableRow.LayoutParams.WRAP_CONTENT));
                    }
                }

            }
            Log.e("type1",type1.toString());
            Log.e("type2",type2.toString());
        }catch (Exception e){}
    }
}
