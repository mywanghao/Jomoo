package com.ucas.jomoo.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ucas.jomoo.R;
import com.ucas.jomoo.tools.StringUtil;
import com.ucas.jomoo.view.Editor_TextView;
import com.ucas.jomoo.view.NumRelative;

/**
 * Created by Dalink on 15/10/21.
 */
public class InputPwdActivity extends Activity implements Button.OnClickListener {

    private Editor_TextView eidtorbar;
    private NumRelative numRelative;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inputpassword);

        eidtorbar = (Editor_TextView) findViewById(R.id.input_line1);

        numRelative = (NumRelative) findViewById(R.id.input_num_console);

        numRelative.initNumRelative(eidtorbar);

        findViewById(R.id.num_pan).setOnClickListener(this);


        findViewById(R.id.num_edit).setOnClickListener(null);
        findViewById(R.id.num_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eidtorbar.clearTextNum();
            }
        });




//        //点击背景消退键盘
//        View backView = findViewById(R.id.background);
//        backView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nav_backbutton:
                this.finish();
                break;
//            case R.id.submit_button:
//                break;
//            case R.id.background:
//                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
//                break;
            case R.id.num_pan:
                commitPassword();
                Toast.makeText(InputPwdActivity.this,"已提交 稍候",Toast.LENGTH_LONG).show();

                break;

        }
    }

    public void commitPassword() {
        String inputText = ((Editor_TextView)findViewById(R.id.input_line1)).getPassword();
        String verifyResult = StringUtil.verifyPassword(inputText, "密码");
        if (verifyResult == null) {
            //通过蓝牙进行密码验证
            Intent intent = this.getIntent();
            intent.putExtra("password", inputText);
            this.setResult(RESULT_OK, intent);
            this.finish();
        } else {
            Toast.makeText(this, verifyResult, Toast.LENGTH_SHORT).show();
        }
    }
}
