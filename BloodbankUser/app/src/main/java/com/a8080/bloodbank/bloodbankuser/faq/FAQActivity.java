package com.a8080.bloodbank.bloodbankuser.faq;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.a8080.bloodbank.bloodbankuser.R;

import java.util.ArrayList;

public class FAQActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
        //startActivity(new Intent(this,BloodBankRegForm.class));
        ArrayList<FAQ> faqs= new ArrayList<>();
        faqs.add(new FAQ(getString(R.string.q1), getString(R.string.a1)));
        faqs.add(new FAQ(getString(R.string.q2), getString(R.string.a2)));
        faqs.add(new FAQ(getString(R.string.q3), getString(R.string.a3)));
        faqs.add(new FAQ(getString(R.string.q4), getString(R.string.a4)));
        faqs.add(new FAQ(getString(R.string.q5), getString(R.string.a5)));
        faqs.add(new FAQ(getString(R.string.q6), getString(R.string.a6)));
        faqs.add(new FAQ(getString(R.string.q7), getString(R.string.a7)));
        faqs.add(new FAQ(getString(R.string.q8), getString(R.string.a8)));
        faqs.add(new FAQ(getString(R.string.q9), getString(R.string.a9)));
        faqs.add(new FAQ(getString(R.string.q10), getString(R.string.a10)));
        faqs.add(new FAQ(getString(R.string.q11), getString(R.string.a11)));
        faqs.add(new FAQ(getString(R.string.q12), getString(R.string.a12)));
        faqs.add(new FAQ(getString(R.string.q13), getString(R.string.a13)));
        faqs.add(new FAQ(getString(R.string.q14), getString(R.string.a14)));
        faqs.add(new FAQ(getString(R.string.q15), getString(R.string.a15)));
        faqs.add(new FAQ(getString(R.string.q16), getString(R.string.a16)));
        faqs.add(new FAQ(getString(R.string.q17), getString(R.string.a17)));
        faqs.add(new FAQ(getString(R.string.q18), getString(R.string.a18)));
        faqs.add(new FAQ(getString(R.string.q19), getString(R.string.a19)));
        faqs.add(new FAQ(getString(R.string.q20), getString(R.string.a20)));
        faqs.add(new FAQ(getString(R.string.q21), getString(R.string.a21)));
        faqs.add(new FAQ(getString(R.string.q22), getString(R.string.a22)));
        faqs.add(new FAQ(getString(R.string.q23), getString(R.string.a23)));
        FAQAdapter adapter = new FAQAdapter(this, faqs);
        ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(adapter);
    }
}
