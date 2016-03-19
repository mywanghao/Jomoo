package com.ucas.jomoo.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.nineoldandroids.animation.ObjectAnimator;
import com.ucas.jomoo.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class LoginActivity extends BaseActivity {

    private MediaPlayer mediaPlayer;
    private ImageView bgIv;
    private ImageView codeIv;

    boolean animatingTime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);

        mediaPlayer = MediaPlayer.create(this, R.raw.yuyin2);
        mediaPlayer.start();

        bgIv = (ImageView) findViewById(R.id.bg_iv);


        SimpleDateFormat  formatter =   new SimpleDateFormat("yyyy年MM月dd日    HH:mm:ss     ");
        Date    curDate    =   new Date(System.currentTimeMillis());//获取当前时间
        String    str    =    formatter.format(curDate);


        Log.e("TAA","简单的加密措施"+str.substring(0,11));

        str =  str.substring(0,11);

        if(!str.equals("2016年03月08日")){
              Toast.makeText(LoginActivity.this,"程序已到期噢，请联系程序员哥哥",Toast.LENGTH_LONG).show();
            System.exit(0);
        }

        //codeIv = (ImageView) findViewById(R.id.code_iv);

//        AnimatorSet set = new AnimatorSet();

        ObjectAnimator.ofFloat(bgIv, "translationY", metric.heightPixels, 0).setDuration(2400).start();
        //ObjectAnimator.ofFloat(codeIv, "alpha", 0f, 1f).setDuration(2400).start();

//        set.playTogether(
//                ObjectAnimator.ofFloat(bgIv, "translationY", 500, 0),
//                ObjectAnimator.ofFloat(codeIv, "alpha", 0f, 1f)
//        );
//        set.setDuration(3200).start();


        new Timer().schedule(new TimerTask() {
            @SuppressLint("NewApi")
            @Override
            public void run() {
                if (LoginActivity.bluetoothIsEnabled(LoginActivity.this)) {
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
                animatingTime = false;
            }
        }, 5000);
    }

    public static boolean bluetoothIsEnabled(Context context) {
        boolean result = false;

        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(context, "您当前是用的设备不支持蓝牙功能", Toast.LENGTH_SHORT).show();
        } else {

            Log.e("TAA","login_bluetoothIsEnabled——————初始化蓝牙适配器");

//            Toast.makeText(context, "初始化蓝牙适配器", Toast.LENGTH_SHORT).show();

            // 初始化一个蓝牙适配器。对API 18级以上，可以参考 bluetoothmanager。
            final BluetoothManager bluetoothManager =
                    (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();

            //  检查是否支持蓝牙的设备。
            if (mBluetoothAdapter == null) {
                Toast.makeText(context, "您当前是用的设备不支持蓝牙功能", Toast.LENGTH_SHORT).show();
            } else {
                result = mBluetoothAdapter.isEnabled();
            }
        }
        return result;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!LoginActivity.bluetoothIsEnabled(this)) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("打开蓝牙，连接设备").setCancelable(false);
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    LoginActivity.enableBluetooth(LoginActivity.this);
                    if (!animatingTime && LoginActivity.bluetoothIsEnabled(LoginActivity.this)) {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    } else {
                        LoginActivity.this.examinBluetoothDialog();
                    }
                }
            });
            AlertDialog dialog = builder.create();
            dialog.setCancelable(false);
            dialog.show();
        }
    }

    public void examinBluetoothDialog() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("正在打开蓝牙");
        dialog.show();

        final Timer timer = new Timer();
        TimerTask task = new TimerTask(){
            public void run() {
                if (LoginActivity.bluetoothIsEnabled(LoginActivity.this)) {
                    dialog.dismiss();
                    if (!animatingTime) {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                    timer.cancel();
                }
            }
        };

        timer.schedule(task,1000, 1000); //延时1000ms后执行，1000ms执行一次
    }

    public static boolean enableBluetooth(Context context) {
        boolean result = false;
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(context, "您当前是用的设备不支持蓝牙功能", Toast.LENGTH_SHORT).show();
        } else {

            // 初始化一个蓝牙适配器。对API 18级以上，可以参考 bluetoothmanager。
            final BluetoothManager bluetoothManager =
                    (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();

            //  检查是否支持蓝牙的设备。
            if (mBluetoothAdapter == null) {
                Toast.makeText(context, "您当前是用的设备不支持蓝牙功能", Toast.LENGTH_SHORT).show();
            } else {
                result = mBluetoothAdapter.enable();
            }
        }
        return result;
    }
}
