package com.ucas.jomoo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.ucas.jomoo.R;

/**
 * Created by hao on 2016/3/6.
 */

public class NumRelative extends RelativeLayout implements View.OnClickListener {

    private Editor_TextView editInstance;

    public NumRelative(Context context) {
        super(context);
    }

    public NumRelative(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NumRelative(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void initNumRelative(Editor_TextView editInstance) {
        this.editInstance = editInstance;
        this.editInstance.init();
        init();

    }


    private void init() {

        for (int x = 0; x < getChildCount(); x++) {

            if(getChildAt(x).getId()==R.id.num_pan){
                return;
            }

            getChildAt(x).setOnClickListener(this);
        }

    }


    @Override
    public void onClick(View v) {

        int id = v.getId();
        String num = id == R.id.num0 ? 0 + "" : id == R.id.num1 ? 1 + "" : id == R.id.num2 ? 2 + "" : id == R.id.num3 ?
                3 + "" : id == R.id.num4 ? 4 + "" : id == R.id.num5 ? 5 + "" : id == R.id.num6 ? 6 + "" : id == R.id.num7 ? 7 + "" : id == R.id.num8 ? 8 + "" : id == R.id.num9 ? 9 + "" : null;

        if(num!=null&&editInstance!=null){//设置属性
            editInstance.inputTextNum(num);
        }

        switch (v.getId()){

            case R.id.num_pan:

                break;

            case R.id.num_edit:
                editInstance.clearTextNum();
                break;


        }




    }
}
