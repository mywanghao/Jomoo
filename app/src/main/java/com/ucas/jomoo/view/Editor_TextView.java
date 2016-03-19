package com.ucas.jomoo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ucas.jomoo.R;

/**
 * Created by hao on 2016/3/6.
 */

public class Editor_TextView extends LinearLayout {

    private TextView tx1, tx2, tx3, tx4;

    public Editor_TextView(Context context) {
        super(context);
     //   init();
    }

    public Editor_TextView(Context context, AttributeSet attrs) {
        super(context, attrs);
     //   init();
    }

    public Editor_TextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
   //     init();
    }

    public void init() {
        tx1 = (TextView) findViewById(R.id.text1);
        tx2 = (TextView) findViewById(R.id.text2);
        tx3 = (TextView) findViewById(R.id.text3);
        tx4 = (TextView) findViewById(R.id.text4);

    }


    /**
     * 输入内容
     */
    public void inputTextNum(String content) {

        String t1 = tx1.getText().toString()+"";
        String t2 = tx2.getText().toString()+"";
        String t3 = tx3.getText().toString()+"";
        String t4 = tx4.getText().toString()+"";

        if(t1.equals("")){
            tx1.setText(content);
            return;
        }else if(t2.equals("")){
            tx2.setText(content);
            return;
        }else if(t3.equals("")){
            tx3.setText(content);
            return;
        }else if(t4.equals("")){
            tx4.setText(content);
         //   (InputPwdActivity)getContext().
            if(overListener!=null){
                overListener.onOver();
            }

            return;
        }

    }


    /**
     * 清理内容
     */
    public void clearTextNum() {
        Log.e("TAA","开始清理");


        String t1 = tx1.getText().toString()+"";
        String t2 = tx2.getText().toString()+"";
        String t3 = tx3.getText().toString()+"";
        String t4 = tx4.getText().toString()+"";

        if(!t4.equals("")){
            tx4.setText("");
            return;
        }else if(!t3.equals("")){
            tx3.setText("");
            return;
        }else if(!t2.equals("")){
            tx2.setText("");
            return;
        }else if(!t1.equals("")){
            tx1.setText("");

            return;
        }

    }


    public String getPassword() {
        return  tx1.getText().toString()+tx2.getText().toString()+tx3.getText().toString()+tx4.getText().toString();
    }


    interface  OnPasswordOver{

        /**
         * 密码输入完毕以后
         */
        public void onOver();

    }

    public OnPasswordOver getOverListener() {
        return overListener;
    }

    public void setOverListener(OnPasswordOver overListener) {
        this.overListener = overListener;
    }

    public  OnPasswordOver overListener ;

}
