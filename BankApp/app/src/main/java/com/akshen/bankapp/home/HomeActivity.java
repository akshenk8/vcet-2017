package com.akshen.bankapp.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.akshen.bankapp.LoginActivity;
import com.akshen.bankapp.R;
import com.akshen.bankapp.misc.Utils;
import com.akshen.bankapp.notification.Config;

import java.util.ArrayList;

import static com.akshen.bankapp.notification.Config.BLOOD_BANK_ADMIN_FIREBASE_SENT;

public class HomeActivity extends AppCompatActivity {

    SharedPreferences sp;
    String bankid;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        toolbar = (Toolbar)findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("User Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        sp = new Utils().getApplicationPreference(this);
        if ((bankid = sp.getString(LoginActivity.BANK_ID_TAG, null)) == null) {
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            finish();
        }

        if(!sp.getBoolean(BLOOD_BANK_ADMIN_FIREBASE_SENT,false)){
            new Config().sendRegistrationToServer(this,null);
        }

        String[] listHeadingString=getResources().getStringArray(R.array.homeListHeadingStrings);
        String[] listContentString=getResources().getStringArray(R.array.homeListDescriptionStrings);
        TypedArray listImg=getResources().obtainTypedArray(R.array.homeListDrawable);
        ArrayList<HomeItemHolder> homeItems = new ArrayList<>();

        for(int i=0;i<listHeadingString.length;i++)
            homeItems.add(new HomeItemHolder(listHeadingString[i], listContentString[i], listImg.getResourceId(i,HomeItemHolder.NO_IMAGE_PROVIDED)));

        HomeAdapter adapter = new HomeAdapter(this, homeItems,R.color.listItemBackgroundColor);

        ListView listView = (ListView) findViewById(R.id.campHistoryList);

        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                sp.edit().remove(LoginActivity.BANK_ID_TAG).commit();
                sp.edit().putBoolean(BLOOD_BANK_ADMIN_FIREBASE_SENT, false).commit();
                Intent i = new Intent(this, LoginActivity.class);
                startActivity(i);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
