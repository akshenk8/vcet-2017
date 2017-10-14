package com.a8080.bloodbank.bloodbankuser.faq;

import android.view.View;

/**
 * Created by Vivek Vora on 04-Jan-17.
 */

public class FAQ {
    private String question,answer;
    private boolean open=false;
    private View aView=null;
    public FAQ(String q, String a){
        question=q;
        answer=a;
    }
    public String getQuestion(){
        return question;
    }
    public String getAnswer(){
        return answer;
    }
    public boolean isOpen(){return open;}
    public void setOpen(){open=!open;}
}
