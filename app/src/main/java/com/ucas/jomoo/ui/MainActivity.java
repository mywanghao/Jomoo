package com.ucas.jomoo.ui;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ucas.jomoo.JommoApp;
import com.ucas.jomoo.R;
import com.ucas.jomoo.com.ucas.pojo.RemoteDeviceModel;
import com.ucas.jomoo.com.ucas.storage.DeviceListDatabaseHelper;
import com.ucas.jomoo.tools.BluetoothUtils;
import com.ucas.jomoo.tools.ControlPagerAdapter;
import com.ucas.jomoo.tools.SPUtils;
import com.ucas.jomoo.tools.SampleGattAttributes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends BaseActivity implements Button.OnClickListener{

    public int[] statues = {0, 0, 0, 0, 0};

    private ProgressBar bar;
    private int progress = 0;

    public JommoApp jommoApp;

    public ViewPager mViewPager;
    private ControlPagerAdapter mAdapter;

    private String DATA;
    private int i;
    private ArrayList<BluetoothGattCharacteristic> charas;

    public RemoteDeviceModel remoteDeviceModel;
    public BluetoothLeService mBluetoothLeService;

    public BluetoothGattCharacteristic mNotifyCharacteristic;

    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

    private  Context context;

    private static Context mainContext;

    private int stateRunloop;

    boolean firstTimeStart = true;

    boolean proccessOnResult = false;

    // 代码管理服务生命周期。
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName,
                                       IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service)
                    .getService();

            PublicKind.mBluetoothLeService = mBluetoothLeService ;


            Log.e("a", "初始化蓝牙服务");
            if (!mBluetoothLeService.initialize()) {
                Log.e("a", "无法初始化蓝牙");
                //finish();
            }
            // 自动连接到装置上成功启动初始化。
            //result = mBluetoothLeService.connect(mDeviceAddress);

            //把蓝牙控制器交由全局应用对象
            //JommoApp app = (JommoApp)MainActivity.this.getApplication();
            app.bluetoothLeService = mBluetoothLeService;

            if (mNotifyCharacteristic != null) {
                mBluetoothLeService.setmCharacteristic(mNotifyCharacteristic);
            }

            try {
                //绑定服务后，发起一个现有的连接
                String lastDeviceAddress = BluetoothUtils.readLastAddress(MainActivity.this);
                DeviceListDatabaseHelper helper = new DeviceListDatabaseHelper(MainActivity.this);
                RemoteDeviceModel model = helper.getDevice(lastDeviceAddress);
                helper.close();
                if (model != null&&!isConnectAuto) {//智能链接关闭
                    //boolean result = mBluetoothLeService.connect(model);
                    remoteDeviceModel = model;
                    Intent intent = new Intent();
                    intent.putExtra("deviceName", model.name);
                    intent.putExtra("address", lastDeviceAddress);
                    MainActivity.this.onActivityResult(1, RESULT_OK, intent);
                    Toast.makeText(MainActivity.this, "智能链接开启，搜索中。。", Toast.LENGTH_SHORT).show();

                }else{
                    Toast.makeText(MainActivity.this, "智能链接关闭，停止搜索", Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e) {
                Log.e("liuyu", "" + e);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService.disconnect();
            mBluetoothLeService = null;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
   //     requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//隐藏状态栏


        setContentView(R.layout.activity_main);


        bar = (ProgressBar)findViewById(R.id.progressBar);
        bar.setVisibility(View.INVISIBLE);


        context = this;
        mainContext = this;
       /* DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);*/

        jommoApp = JommoApp.getInstance();

        isConnectAuto = SPUtils.getInstance(MainActivity.this).getBoolean("isconnectauto", false);

        //

        initViewpager();

        DATA = "";
        i = 0;
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

//        //左侧关闭右侧修改密码
//        ImageView closeButton = (ImageView)findViewById(R.id.nav_close);
//        closeButton.setOnClickListener(this);
//        Button rightButton = (Button)findViewById(R.id.nav_right);
//        rightButton.setOnClickListener(this);

        //BluetoothUtils.storeLastAddress(this, "12-34-56-78-9A-BC");
        haoInit();
    }

    private void haoInit() {
//        findViewById(R.id.btn_setter).setOnClickListener(this);
//        findViewById(R.id.btn_help).setOnClickListener(this);

    }

    public static Context getMainContext(){
        return mainContext;
    }
    private void initViewpager() {
        mViewPager = (ViewPager) findViewById(R.id.info_viewpager);

        mAdapter = new ControlPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                List<Fragment> fragments = getSupportFragmentManager().getFragments();
                if (position == 0) {
                    ((GestureControlFragment) fragments.get(position)).refresh();
                    ((GestureControlFragment) fragments.get(position)).gestureOnResumeMethod();
                } else if (position == 1) {
                    ((ButtonControlFragment) fragments.get(position)).refresh();
                    ((ButtonControlFragment) fragments.get(position)).buttonResumeMethod();
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }




    // 我们是注定的expandablelistview数据结构
    //  在UI。
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null)
            return;
        String uuid = null;
        String unknownServiceString = "service_UUID";
        String unknownCharaString = "characteristic_UUID";
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
                = new ArrayList<ArrayList<HashMap<String, String>>>();
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        // 循环遍历服务
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(
                    "NAME", SampleGattAttributes.lookup(uuid, unknownServiceString));
            currentServiceData.put("UUID", uuid);
            gattServiceData.add(currentServiceData);

            ArrayList<HashMap<String, String>> gattCharacteristicGroupData =
                    new ArrayList<HashMap<String, String>>();
            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();
            charas =
                    new ArrayList<BluetoothGattCharacteristic>();

            // 循环遍历特征
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                uuid = gattCharacteristic.getUuid().toString();
                currentCharaData.put(
                        "NAME", SampleGattAttributes.lookup(uuid, unknownCharaString));
                currentCharaData.put("UUID", uuid);
                gattCharacteristicGroupData.add(currentCharaData);
            }
            mGattCharacteristics.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);
        }

        final BluetoothGattCharacteristic characteristic = charas.get(charas.size() - 1);
        final int charaProp = characteristic.getProperties();
        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
            if (mNotifyCharacteristic != null) {
                mBluetoothLeService.setCharacteristicNotification(
                        mNotifyCharacteristic, false);
                mNotifyCharacteristic = null;
            }
            mBluetoothLeService.readCharacteristic(characteristic);

        }
        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
            mNotifyCharacteristic = characteristic;
            mBluetoothLeService.setCharacteristicNotification(
                    characteristic, true);

            if (mBluetoothLeService != null) {
                mBluetoothLeService.setmCharacteristic(mNotifyCharacteristic);
            }
            if (remoteDeviceModel != null) {
                Toast.makeText(MainActivity.this, "成功连接: "
                        + remoteDeviceModel.name, Toast.LENGTH_SHORT).show();
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                }
                ((MainActivity) context).doOpt(BluetoothUtils.RETURN_DATA,1);
            }
        }
    }

    /**
     * 注册广播
     *
     * @return
     */
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter
                .addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothLeService.ACTION_RSSI);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_RSSI);
        intentFilter.addAction(ModifyPasswordActivity.MODIFY_PASSWORD_ACTION);
        return intentFilter;
    }

    // 处理各种事件的服务了。
    // action_gatt_connected连接到服务器：关贸总协定。
    // action_gatt_disconnected：从关贸总协定的服务器断开。
    // action_gatt_services_discovered：关贸总协定的服务发现。
    // action_data_available：从设备接收数据。这可能是由于阅读
    // 或通知操作。
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                if (mNotifyCharacteristic != null && remoteDeviceModel != null && mBluetoothLeService != null){
                    //mBluetoothLeService.connect(remoteDeviceModel.address);
                }
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED
                    .equals(action)) {
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED
                    .equals(action)) {
                // 显示所有的支持服务的特点和用户界面。
                Log.e("a", "来了广播3");
                List<BluetoothGattService> supportedGattServices = mBluetoothLeService
                        .getSupportedGattServices();
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
                for (int i = 0; i < supportedGattServices.size(); i++) {
                    Log.e("a", "1:BluetoothGattService UUID=:" + supportedGattServices.get(i).getUuid());
                    List<BluetoothGattCharacteristic> cs = supportedGattServices.get(i).getCharacteristics();
                    for (int j = 0; j < cs.size(); j++) {
                        Log.e("a", "2:   BluetoothGattCharacteristic UUID=:" + cs.get(j).getUuid());


                        List<BluetoothGattDescriptor> ds = cs.get(j).getDescriptors();
                        for (int f = 0; f < ds.size(); f++) {
                            Log.e("a", "3:      BluetoothGattDescriptor UUID=:" + ds.get(f).getUuid());

                            byte[] value = ds.get(f).getValue();

                            Log.e("a", "4:     			value=:" + Arrays.toString(value));
                            Log.e("a", "5:     			value=:" + Arrays.toString(ds.get(f).getCharacteristic().getValue()));
                        }
                    }
                }

            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                Log.e("a", "来了广播4--->data:" + intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
//				displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
                i++;
                DATA = "" + DATA + "\n第" + i + "条：" + intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                Log.e("a4", "" + DATA);
            } else if (BluetoothLeService.ACTION_RSSI.equals(action)) {
                Log.e("a", "来了广播5");
            } else if (ModifyPasswordActivity.MODIFY_PASSWORD_ACTION.equals(action)) {
                //重置密码
                try {
                    String password = intent.getStringExtra("password");
                    String oldPassword = intent.getStringExtra("oldPassword");
                    if (mBluetoothLeService != null && mNotifyCharacteristic != null) {
                        byte[] data = BluetoothUtils.intobyte(BluetoothUtils.MODIFY_PWD, password, oldPassword,1);
                        mBluetoothLeService.write(mNotifyCharacteristic, data);

                        //保存入数据库
                        DeviceListDatabaseHelper helper = new DeviceListDatabaseHelper(MainActivity.this);
                        helper.modifyPassword(remoteDeviceModel.address, password);
                        helper.close();
                    }
                } catch (Exception e) {

                }

            }
        }
    };


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
    }


    private boolean  isConnectAuto ;

    @Override
    protected void onResume() {
        super.onResume();


        isConnectAuto = SPUtils.getInstance(MainActivity.this).getBoolean("isconnectauto", false);

        /**
         * 注册广播
         */
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if(!isConnectAuto) {
            if (!proccessOnResult && !firstTimeStart && mBluetoothLeService != null && remoteDeviceModel != null && mBluetoothLeService != null && mBluetoothLeService.getmConnectionState() != BluetoothProfile.STATE_CONNECTED) {
                //mBluetoothLeService.disconnect();
                mBluetoothLeService.connect(remoteDeviceModel);
            }
            Toast.makeText(MainActivity.this, "主页智能链接开启", Toast.LENGTH_SHORT).show();

        }else{
            Toast.makeText(MainActivity.this, "主页智能链接关闭", Toast.LENGTH_SHORT).show();

        }
        firstTimeStart = false;
        proccessOnResult = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        //unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && data != null && resultCode == RESULT_OK) {
            RemoteDeviceModel model = new RemoteDeviceModel();
            model.name = data.getStringExtra("deviceName");
            model.address = data.getStringExtra("address");
            remoteDeviceModel = model;
            PublicKind.remoteDeviceModel = remoteDeviceModel;
            mBluetoothLeService.disconnect();
            boolean result = mBluetoothLeService.connect(model);
            mNotifyCharacteristic = null;
            proccessOnResult = true;
            //连接蓝牙设备后，返回“99”指令
            //蓝牙4.0的连接是需要时间的，所以延时1s
            //int count = 0;

            final Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if(msg.what == 0x11){
                        bar.setVisibility(View.VISIBLE);
                        bar.setProgress(progress);
                        //setProgressBarVisibility(true);
                        //bar.setVisibility(View.VISIBLE );
                        if(progress == 200){
                            bar.setVisibility(View.INVISIBLE );
                        }
                    }
                }
            };
            if(result) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (progress < 200) {
                            progress += 20;
                            SystemClock.sleep(500);
                            Message msg = new Message();
                            msg.what = 0x11;
                            handler.sendMessage(msg);
                        }
                    }
                }).start();
                //setProgressBarVisibility(false);

                /*
                stateRunloop = 0;
                final Timer timer = new Timer();
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        stateRunloop ++;
                        if (stateRunloop == 6) {
                            timer.cancel();
                        }
                        if (mBluetoothLeService.getmConnectionState() ==
                                BluetoothLeService.STATE_CONNECTED && mNotifyCharacteristic != null) {
                            timer.cancel();
                            ((MainActivity) context).doOpt(BluetoothUtils.RETURN_DATA);
                            //Thread.yield();

                            MainActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    if (remoteDeviceModel != null) {
                                        Toast.makeText(MainActivity.this, "成功连接: "
                                                + remoteDeviceModel.name, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }
                    }
                };
                timer.schedule(task, 1000, 1000);
                */
            }
        }
    }

    public void doOpt(int opt,int order) {
        if (mBluetoothLeService != null && mNotifyCharacteristic != null) {

            try {
                DeviceListDatabaseHelper helper = new DeviceListDatabaseHelper(this);
                RemoteDeviceModel model = helper.getDevice(remoteDeviceModel.address);
                helper.close();
               byte[] data = BluetoothUtils.intobyte(opt, model.password, null,order);
           //     byte[] data = hexStringToByte(opt+"");

                mBluetoothLeService.write(mNotifyCharacteristic, data);

                Log.e("TAG", "-------> " + opt);
            } catch (Exception e) {
                Log.e("liuyu", "e " + e.toString());
            }
        }
    }

    /**
     * 把16进制字符串转换成字节数组
     * @return byte[]
     */
    public static byte[] hexStringToByte(String hex) {
        int len = (hex.length() / 2);
        byte[] result = new byte[len];
        char[] achar = hex.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
        }
        return result;
    }

    private static int toByte(char c) {
        byte b = (byte) "0123456789ABCDEF".indexOf(c);
        return b;
    }


    public void closeAll() {
        for(int i = 0; i < statues.length; i++) {
            statues[i] = 0;
        }

        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        ((GestureControlFragment) fragments.get(0)).refresh();
        ((ButtonControlFragment) fragments.get(1)).refresh();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nav_close:
                if (mBluetoothLeService != null) {
                    mBluetoothLeService.disconnect();
                }
                this.finish();
                break;
//            case R.id.nav_right:
//                if (remoteDeviceModel != null) {
//                    try {
//                        if (remoteDeviceModel != null && mBluetoothLeService.getmConnectionState() == BluetoothProfile.STATE_CONNECTED) {
//                            Intent intent = new Intent(this, ModifyPasswordActivity.class);
//                            intent.putExtra("deviceAddress", remoteDeviceModel.address);
//                            intent.putExtra("password", remoteDeviceModel.password);
//                            this.startActivity(intent);
//                        } else {
//                            Toast.makeText(this, "蓝牙连接未成功", Toast.LENGTH_LONG).show();
//                        }
//                    } catch (Exception e) {
//                        Toast.makeText(this, "蓝牙连接未成功", Toast.LENGTH_LONG).show();
//                    }
//                } else {
//                    Toast.makeText(this, "蓝牙连接未成功", Toast.LENGTH_LONG).show();
//                }
//                break;
            case R.id.btn_help:
             //   mViewPager.setCurrentItem(ControlPagerAdapter.HELP_FRAGMENT);
                startActivity(new Intent(MainActivity.this,HelpActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;

            case R.id.btn_setter:
                gotoBTListActivity();
                break;


        }
    }

    protected void gotoBTListActivity() {
        if (mBluetoothLeService.getmConnectionState() == BluetoothProfile.STATE_CONNECTING){
            Toast.makeText(this, "蓝牙繁忙", Toast.LENGTH_SHORT).show();
        }
        try {
            ((MainActivity)context).mBluetoothLeService.lightDisconnect();
        } catch (Exception e) {
        }
        this.startActivityForResult(new Intent(context, BTListActivity.class), 1);
    }
}
