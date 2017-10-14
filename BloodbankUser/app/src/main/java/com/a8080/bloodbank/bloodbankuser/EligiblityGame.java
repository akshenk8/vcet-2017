package com.a8080.bloodbank.bloodbankuser;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class EligiblityGame extends AppCompatActivity {

    private ImageView imageView;
    private TextView t1;
    private Button btnyes;
    private Button btnno;
    int count;
    final int[] listviewImage = new int[]{
            R.drawable.under16, R.drawable.over70, R.drawable.img1,R.drawable.birth,R.drawable.heart,
            R.drawable.iron,R.drawable.atrisk,R.drawable.drugs,R.drawable.img2,
            R.drawable.index1,R.drawable.img3,R.drawable.img4, R.drawable.img5
    };
    final int[] Question = new int[]
            {
                    R.string.eq1,R.string.eq2,R.string.eq3,R.string.eq4,R.string.eq5,R.string.eq6,R.string.eq7,R.string.eq8,
                    R.string.eq9,R.string.eq10,R.string.eq11,R.string.eq12,R.string.eq13

            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eligible_activity);

        count=0;
        imageView = (ImageView) findViewById(R.id.imageview);
        t1 = (TextView) findViewById(R.id.question);
        btnyes = (Button) findViewById(R.id.yes);
        btnno = (Button) findViewById(R.id.no);


        imageView.setImageResource(listviewImage[count]);
        t1.setText(getString(Question[count])+"");

        btnyes.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(),getString(R.string.notEligible),Toast.LENGTH_SHORT).show();
                    finish();
                }
            });

        btnno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(count==listviewImage.length-1)
                {
                    setContentView(R.layout.eligible_success);
                }

                else {
                    count++;
                    imageView.setImageResource(listviewImage[count]);
                    Animation animation1 =
                            AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move);
                    imageView.startAnimation(animation1);
                    t1.setText(getString(Question[count]) + "");
                }

            }
        });
    }

    public void success(View view){
        finish();
    }
}
