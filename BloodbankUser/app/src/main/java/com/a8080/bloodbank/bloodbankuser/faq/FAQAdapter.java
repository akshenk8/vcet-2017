package com.a8080.bloodbank.bloodbankuser.faq;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.a8080.bloodbank.bloodbankuser.R;

import java.util.ArrayList;

public class FAQAdapter extends ArrayAdapter<FAQ> {

    private Animation animShow, animHide;
    private int faqopen=-1;
    private View currentlyOpen=null;
    private ArrayList<FAQ> words;
    public FAQAdapter(Context context, ArrayList<FAQ> words) {
        super(context, 0, words);
        this.words=words;
    }

    private void initAnimation(Context context)
    {
        animShow = AnimationUtils.loadAnimation( context, R.anim.view_show);
        animHide = AnimationUtils.loadAnimation( context, R.anim.view_hide);
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null)
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.faq_list, parent, false);
        FAQ current = getItem(position);
        final TextView question = (TextView) listItemView.findViewById(R.id.faq_question);
        question.setText(current.getQuestion());
        final TextView answer = (TextView) listItemView.findViewById(R.id.faq_answer);
        answer.setText(current.getAnswer());
        if(faqopen==position){
            answer.setVisibility(View.VISIBLE);
        }
        else{
            answer.setVisibility(View.GONE);
        }
        final LinearLayout container = (LinearLayout) listItemView.findViewById(R.id.faq_container_layout);
        initAnimation(container.getContext());
        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (answer.getVisibility() == View.GONE) {
                    answer.setVisibility(View.VISIBLE);
                    answer.startAnimation( animShow );
                    if(faqopen!=-1) {
                        words.get(faqopen).setOpen();
                    }
                    faqopen = position;
                    words.get(faqopen).setOpen();
                    if(currentlyOpen!=null){
                        currentlyOpen.setVisibility(View.GONE);
                    }
                    currentlyOpen=answer;
                } else {
                    answer.startAnimation( animHide );
                    answer.postOnAnimationDelayed(new Runnable() {
                        @Override
                        public void run() {
                            answer.setVisibility(View.GONE);
                        }
                    },300);
                    words.get(faqopen).setOpen();
                    faqopen=-1;
                    currentlyOpen=null;
                }
            }
        });
        return listItemView;
    }
}
