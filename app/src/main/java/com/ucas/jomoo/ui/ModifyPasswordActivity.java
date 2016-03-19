package com.ucas.jomoo.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import com.ucas.jomoo.R;
import com.ucas.jomoo.com.ucas.storage.DeviceListDatabaseHelper;
import com.ucas.jomoo.view.Editor_TextView;
import com.ucas.jomoo.view.NumRelative;

/**
 * Created by Dalink on 15/10/21.
 */
public class ModifyPasswordActivity extends Activity implements Button.OnClickListener {

    public final static String MODIFY_PASSWORD_ACTION = "ModifyPasswordAction";

    private String deviceAddress;
    private String oldPassword;
    private Editor_TextView eidtorbar;
    private NumRelative numRelative;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifypwd);


        eidtorbar = (Editor_TextView) findViewById(R.id.input_line1);

        numRelative = (NumRelative) findViewById(R.id.input_num_console);

        numRelative.initNumRelative(eidtorbar);

        deviceAddress = this.getIntent().getStringExtra("deviceAddress");
        oldPassword = this.getIntent().getStringExtra("password");
        findViewById(R.id.num_pan).setOnClickListener(null);
        findViewById(R.id.num_pan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(ModifyPasswordActivity.this, "提交确认中..", Toast.LENGTH_SHORT).show();

                unPassWord();
            }
        });

        findViewById(R.id.num_edit).setOnClickListener(null);
        findViewById(R.id.num_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eidtorbar.clearTextNum();
            }
        });



//        Button submitButton = (Button) this.findViewById(R.id.submit_button);
//        submitButton.setOnClickListener(this);

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
//
//                break;
            case R.id.background:
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                break;

            case R.id.num_pan:


                unPassWord();

                break;

        }
    }

    private void unPassWord() {
        String inputText1 = eidtorbar.getPassword();
//        String verifyResult1 = StringUtil.verifyPassword(inputText1, "原密码");
//        if (verifyResult1 != null) {
            //通过蓝牙修改密码

            //写入本地数据
//                    try {

                DeviceListDatabaseHelper helper = new DeviceListDatabaseHelper(this);
                helper.modifyPassword(deviceAddress, eidtorbar.getPassword());
                helper.close();

                Intent intent = new Intent(ModifyPasswordActivity.MODIFY_PASSWORD_ACTION);
                intent.putExtra("password", eidtorbar.getPassword());
                intent.putExtra("oldPassword", oldPassword);
                ModifyPasswordActivity.this.sendBroadcast(intent);
                Toast.makeText(this, "您已经修改密码请用新密码连接设备", Toast.LENGTH_SHORT).show();

                this.setResult(0, intent);
                this.finish();

//                    } catch (Exception e) {

//                    }

//        }else{
//            Toast.makeText(this, "verifyResult1数据为空", Toast.LENGTH_SHORT).show();
//
//        }
    }
}
