package com.ucas.jomoo.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.ucas.jomoo.JommoApp;
import com.ucas.jomoo.R;
import com.ucas.jomoo.com.ucas.pojo.RemoteDeviceModel;
import com.ucas.jomoo.com.ucas.storage.DeviceListDatabaseHelper;
import com.ucas.jomoo.tools.BluetoothUtils;
import com.ucas.jomoo.tools.SPUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class BTListActivity extends Activity {

    private BluetoothAdapter mBluetoothAdapter;       //蓝牙适配器CCCCCFZ
    private boolean mScanning;
    private Handler mHandler;

    private ListView btlist;
    private ArrayList<HashMap<String, Object>> listItem;
    //  private SimpleAdapter adapter;                    //ListView资源适配器

    private BTListAdapter adapter;

    private ImageView blist_auto;

    /**
     * fase 为未连接
     * true 为已连接
     */
    private boolean isConnect;

    /**
     * true 版本1
     * false 版本2
     */
    private boolean boo_version;


    private View tempView;
    private int tempPosition;
    private BluetoothDevice tempDevice;
    private String tempdeviceAddress;
    private RemoteDeviceModel tempModel;

    boolean result = false;

    public BluetoothLeService mBluetoothLeService;

    public RemoteDeviceModel remoteDeviceModel;

    public BluetoothGattCharacteristic mNotifyCharacteristic;


    protected JommoApp app;

    //  代码管理服务生命周期。
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName,
                                       IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service)
                    .getService();

            PublicKind.mBluetoothLeService = mBluetoothLeService;


            Log.e("a", "初始化蓝牙服务");
            if (!mBluetoothLeService.initialize()) {
//                Log.e("a", "无法初始化蓝牙");
                Toast.makeText(BTListActivity.this, "无法初始化蓝牙", Toast.LENGTH_LONG).show();

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
                String lastDeviceAddress = BluetoothUtils.readLastAddress(BTListActivity.this);
                DeviceListDatabaseHelper helper = new DeviceListDatabaseHelper(BTListActivity.this);
                RemoteDeviceModel model = helper.getDevice(lastDeviceAddress);
                helper.close();
                if (model != null) {
                    //boolean result = mBluetoothLeService.connect(model);
                    remoteDeviceModel = model;
                    Intent intent = new Intent();
                    intent.putExtra("deviceName", model.name);
                    intent.putExtra("address", lastDeviceAddress);
                    ResultMethod(intent);
                }
            } catch (Exception e) {
                Log.e("liuyu", "" + e);
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService.disconnect();
            mBluetoothLeService = null;
        }
    };

    //是否自动连接 true 不自动  fase 为自动
    private boolean isConnectAuto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_btlist);
        mHandler = new Handler();
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        blist_auto = (ImageView) findViewById(R.id.blist_auto);
        app = JommoApp.getInstance();
      //  bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);


        isConnectAuto = SPUtils.getInstance(BTListActivity.this).getBoolean("isconnectauto", isConnectAuto);

        blist_auto.setSelected(isConnectAuto);

        blist_auto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isConnectAuto) {
                    blist_auto.setSelected(false);

                    Toast.makeText(BTListActivity.this, "开启智能链接", Toast.LENGTH_SHORT).show();
                }else{
                    blist_auto.setSelected(true);

                    Toast.makeText(BTListActivity.this, "关闭智能链接", Toast.LENGTH_SHORT).show();
                }

                isConnectAuto = !isConnectAuto;

//                blist_auto.setSelected(isConnect);

                SPUtils.getInstance(BTListActivity.this).save("isconnectauto",isConnectAuto);

            }
        });


//
//// 为了确保设备上蓝牙能使用, 如果当前蓝牙设备没启用,弹出对话框向用户要求授予权限来启用
//        if (!mBluetoothAdapter.isEnabled()) {
//            if (!mBluetoothAdapter.isEnabled()) {
//                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//            }
//        }
        boo_version = SPUtils.getInstance(BTListActivity.this).getBoolean("version", false);
        if (!boo_version) {//1.0
            SPUtils.getInstance(BTListActivity.this).save("version", false);
            findViewById(R.id.version_text).setSelected(false);
        } else {//2.0
            findViewById(R.id.version_text).setSelected(true);
            SPUtils.getInstance(BTListActivity.this).save("version", true);

        }


//        findViewById(R.id.blist_auto).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if (v.getTag() == null) {
//                    v.setSelected(true);
//                    v.setTag("111");
//                    Toast.makeText(BTListActivity.this, "关闭智能链接", Toast.LENGTH_SHORT).show();
//                } else {
//                    v.setSelected(false);
//                    v.setTag(null);
//                    Toast.makeText(BTListActivity.this, "开启智能链接", Toast.LENGTH_SHORT).show();
//
//                }
//
//
//            }
//        });

        findViewById(R.id.version_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!boo_version) {
                    v.setSelected(true);
                    SPUtils.getInstance(BTListActivity.this).save("version", true);
                    Toast.makeText(BTListActivity.this, "当前已切换到2.0版本", Toast.LENGTH_SHORT).show();
                    boo_version = true;
                } else {
                    v.setSelected(false);
                    SPUtils.getInstance(BTListActivity.this).save("version", false);
                    Toast.makeText(BTListActivity.this, "当前已切换到1.0版本", Toast.LENGTH_SHORT).show();
                    boo_version = false;
                }
            }
        });


        Context context = this;
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            // 初始化一个蓝牙适配器。对API 18级以上，可以参考 bluetoothmanager。
            final BluetoothManager bluetoothManager =
                    (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();
            //  检查是否支持蓝牙的设备。
            if (mBluetoothAdapter != null) {
                this.mBluetoothAdapter = mBluetoothAdapter;
            }
        }


        if (!LoginActivity.bluetoothIsEnabled(this)) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("打开蓝牙，连接设备").setCancelable(false);
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    LoginActivity.enableBluetooth(BTListActivity.this);
                    if (LoginActivity.bluetoothIsEnabled(BTListActivity.this)) {
                        BTListActivity.this.scanLeDevice(true);
                    } else {
                        BTListActivity.this.examinBluetoothDialog();
                    }
                }
            });
            AlertDialog dialog = builder.create();
            dialog.setCancelable(false);
            dialog.show();
        } else {
            this.scanLeDevice(true);
        }

        btlist = (ListView) findViewById(R.id.bt_lv);
        listItem = new ArrayList<HashMap<String, Object>>();
        // adapter = new SimpleAdapter(this, listItem, android.R.layout.simple_expandable_list_item_2,
//                new String[]{"name", "andrass"}, new int[]{android.R.id.text1, android.R.id.text2});
        adapter = new BTListAdapter(listItem);


        btlist.setAdapter(adapter);

        this.registerForContextMenu(btlist);

        btlist.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, final View view,
                                    int arg2, long arg3) {

                isConnect = false;

                final BluetoothDevice device = (BluetoothDevice) listItem.get(arg2).get("device");

                BluetoothUtils.storeLastAddress(BTListActivity.this, device.getAddress());

                final String deviceAddress = device.getAddress();
                DeviceListDatabaseHelper helper = new DeviceListDatabaseHelper(BTListActivity.this);
                final RemoteDeviceModel model = helper.getDevice(deviceAddress);
                helper.close();
//
//
                if (mScanning && mBluetoothAdapter != null) {
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    mScanning = false;
                }

                adapter.setConnectState(arg2, view, btlist, "链接中..");

//                Toast.makeText(BTListActivity.this,"已开始弹出按钮",1).show();
                //   if (arg1.findViewById(R.id.password_edit).getVisibility() == View.GONE) {

                tempView = view;
                tempDevice = device;
                tempdeviceAddress = deviceAddress;
                tempModel = model;

//                connectInitMethod(view, device, deviceAddress, model);


                /**
                 * 自动连接
                 */
                //   model.name = device.getName();
                //  model.address = device.getAddress();
                //   PublicKind.remFoteDeviceModel = model;
                //      PublicKind.remoteDeviceModel = remoteDeviceModel;


//                String lastDeviceAddress = BluetoothUtils.readLastAddress(BTListActivity.this);

//                if (model != null) {
                //boolean result = mBluetoothLeService.connect(model);
//                    remoteDeviceModel = model;
//                    Intent intent = new Intent();
//                    intent.putExtra("deviceName", model.name);
//                    intent.putExtra("address", lastDeviceAddress);
//
//                ResultMethod(intent);

//                if(mBluetoothLeService!=null) {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(BTListActivity.this,"蓝牙服务 正常运行_____1",Toast.LENGTH_LONG).show();
//                            mBluetoothLeService.disconnect();
//                            Toast.makeText(BTListActivity.this,"蓝牙服务 正常运行_____2",Toast.LENGTH_LONG).show();
//                            result = mBluetoothLeService.connect(model);
//                            Toast.makeText(BTListActivity.this,"蓝牙服务 正常运行",Toast.LENGTH_LONG).show();
//                        }
//                    });
//
//
//                }else{
//                    Toast.makeText(BTListActivity.this,"蓝牙服务 未运行——",Toast.LENGTH_LONG).show();
//
//                }

//                if(result) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                            isConnect = true;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    connectInitMethod(tempView, tempDevice, tempdeviceAddress, tempModel);
                                }
                            });

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
//                }
                //mNotifyCharacteristic = null;
                // proccessOnResult = true;


//                Intent intent = BTListActivity.this.getIntent();
//                intent.putExtra("address", device.getAddress());
//                intent.putExtra("deviceName", device.getName());
//                BTListActivity.this.setResult(RESULT_OK, intent);
                // BTListActivity.this.finish();
//                BluetoothUtils.storeLastAddress(BTListActivity.this, deviceAddress);


//                if (model == null || model.password == null || model.password.length() == 0) {
                //   Intent inputPwdIntent = new Intent(BTListActivity.this, InputPwdActivity.class);

                //  inputPwdIntent.putExtra("address", device.getAddress());
                //   inputPwdIntent.putExtra("deviceName", device.getName());
                //   BTListActivity.this.startActivityForResult(inputPwdIntent, 1);
                //              } else {
//                    Intent intent = BTListActivity.this.getIntent();
//                    intent.putExtra("address", device.getAddress());
//                    intent.putExtra("deviceName", device.getName());
//                    BTListActivity.this.setResult(RESULT_OK, intent);
//                   // BTListActivity.this.finish();
//                    BluetoothUtils.storeLastAddress(BTListActivity.this, deviceAddress);
//                }

                // } else {
//                    arg1.findViewById(R.id.password_edit).setVisibility(View.GONE);
//                    ViewGroup.LayoutParams layoutParams = arg1.getLayoutParams();
//                    layoutParams.height = Utils.dp2px(40);
                // }

                //   btlist.addView(View.inflate(BTListActivity.this,R.layout.layout_btlist_contonller,null),arg2+1);
                //        ((LinearLayout)arg1).addView(View.inflate(BTListActivity.this,R.layout.layout_btlist_contonller,null));
//                adapter.notifyDataSetChanged();
//                //若正在搜索蓝牙设备则停止搜索

//                //尝试获取这个设备的密码，如果无法获取该密码进入登录界面。否则直接连接。
//                String deviceAddress = device.getAddress();
//                try {
//                    DeviceListDatabaseHelper helper = new DeviceListDatabaseHelper(BTListActivity.this);
//                    RemoteDeviceModel model = helper.getDevice(deviceAddress);
//                    helper.close();
//                    if (model == null || model.password == null || model.password.length() == 0) {
//                        Intent inputPwdIntent = new Intent(BTListActivity.this, InputPwdActivity.class);
//
//                        inputPwdIntent.putExtra("address", device.getAddress());
//                        inputPwdIntent.putExtra("deviceName", device.getName());
//                        BTListActivity.this.startActivityForResult(inputPwdIntent, 1);
//                    } else {
//                        Intent intent = BTListActivity.this.getIntent();
//                        intent.putExtra("address", device.getAddress());
//                        intent.putExtra("deviceName", device.getName());
//                        BTListActivity.this.setResult(RESULT_OK, intent);
//                        BTListActivity.this.finish();
//                        BluetoothUtils.storeLastAddress(BTListActivity.this, deviceAddress);
//                    }
//                } catch (Exception e) {
//                    Log.e("liuyu", "获取地址失败 " + e.toString());
//                }

            }
        });
    }

    private void connectInitMethod(final View view, final BluetoothDevice device, final String deviceAddress, final RemoteDeviceModel model) {
        if (isConnect) {

            view.findViewById(R.id.password_edit).setVisibility(View.VISIBLE);
            ((LinearLayout) view).getChildAt(0).setVisibility(View.GONE);


            ViewTreeObserver viewTreeObserver = view.getViewTreeObserver();

            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {

                    int finalHeight = Utils.dp2px(30);
                    ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                    layoutParams.height = finalHeight;

                    view.setLayoutParams(layoutParams);
                    // Utils.setListViewHeight((ListView) parent,LAD_SysNotification.this);

                }
            });
            view.findViewById(R.id.input_password).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //跳入输入密码
                    if (model == null || model.password == null || model.password.length() == 0) {
                        Intent inputPwdIntent = new Intent(BTListActivity.this, InputPwdActivity.class);

                        inputPwdIntent.putExtra("address", device.getAddress());
                        inputPwdIntent.putExtra("deviceName", device.getName());
                        BTListActivity.this.startActivityForResult(inputPwdIntent, 1);
//                        BTListActivity.this.finish();
                    } else {
                        Intent intent = BTListActivity.this.getIntent();
                        intent.putExtra("address", device.getAddress());
                        intent.putExtra("deviceName", device.getName());
                        BTListActivity.this.setResult(RESULT_OK, intent);
                        BluetoothUtils.storeLastAddress(BTListActivity.this, deviceAddress);
                        BTListActivity.this.finish();

                    }

                }
            });
            view.findViewById(R.id.un_password).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //跳入修改密码

//                    if (model != null) {
//                        try {
//                            if (model != null) {
                                Intent intent = new Intent(BTListActivity.this, ModifyPasswordActivity.class);
                                intent.putExtra("devicesName", device.getName());
                                intent.putExtra("deviceAddress", device.getAddress());
                                intent.putExtra("password",1234+"" );
                                BTListActivity.this.startActivity(intent);
                                BTListActivity.this.finish();
//                            } else {
//                                Toast.makeText(BTListActivity.this, "蓝牙连接未成功", Toast.LENGTH_LONG).show();
//                            }
//                        } catch (Exception e) {
//                            Toast.makeText(BTListActivity.this, "蓝牙连接未成功_ERROR", Toast.LENGTH_LONG).show();
//                        }
//                    } else {
//                        Toast.makeText(BTListActivity.this, "蓝牙连接未成功", Toast.LENGTH_LONG).show();
//                    }

                }
            });
        }
    }


    public void examinBluetoothDialog() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("正在打开蓝牙");
        dialog.show();

        final Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            public void run() {
                if (LoginActivity.bluetoothIsEnabled(BTListActivity.this)) {
                    dialog.dismiss();
                    BTListActivity.this.scanLeDevice(true);
                    timer.cancel();
                }
            }
        };
        timer.schedule(task, 1000, 1000); //延时1000ms后执行，1000ms执行一次
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        Log.v("Liuyu", "populate context menu");
        // set context menu title
        menu.setHeaderTitle("对该设备进行操作");
        // add context menu item
        menu.add(0, 0, Menu.NONE, "取消保存密码");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // 得到当前被选中的item信息
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int id = (int) menuInfo.id;
        switch (item.getItemId()) {
            case 0:
                //取消保存密
                BluetoothDevice device = (BluetoothDevice) listItem.get(id).get("device");
                String address = device.getAddress();
                try {
                    DeviceListDatabaseHelper helper = new DeviceListDatabaseHelper(BTListActivity.this);
                    helper.deleteDevice(address);
                    helper.close();

                    BluetoothUtils.storeLastAddress(BTListActivity.this, null);
                } catch (Exception e) {
                    Log.e("liuyu", "无法删除设备密码");
                }
                BluetoothUtils.storeLastAddress(BTListActivity.this, null);
                break;
            default:
                return super.onContextItemSelected(item);
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isConnect = SPUtils.getInstance(BTListActivity.this).getBoolean("isconnectauto", isConnectAuto);

        blist_auto.setSelected(isConnect);


    }


   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 2){

        }
    }*/

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            //停止后一个预定义的扫描周期扫描。
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
//                    bar.setVisibility(View.GONE);
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    invalidateOptionsMenu();
                }
            }, 10000);
//            bar.setVisibility(View.VISIBLE);
            mScanning = true;
            new Thread() {
                @Override
                public void run() {
                    mBluetoothAdapter.startLeScan(mLeScanCallback);
                }
            }.start();
        } else {
//            bar.setVisibility(View.GONE);
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
        invalidateOptionsMenu();
    }

    // 扫描装置的回调。
    //将扫描的蓝牙设备放入adapter
    private final BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            HashMap<String, Object> map = new HashMap<String, Object>();

                            Log.e("a", "RSSI=:" + rssi + "");

                            map.put("name", device.getName());
                            map.put("andrass", device.getAddress());
                            map.put("device", device);
                            boolean isExist = false;
                            for (int i = 0; i < listItem.size(); i++) {
                                HashMap<String, Object> m = listItem.get(i);
                                if (m.get("name").equals(device.getName())) {
                                    isExist = true;
                                }
                                if (m.get("andrass").equals(device.getAddress())) {
                                    isExist = true;
                                }
                                if (m.get("device").equals(device)) {
                                    isExist = true;
                                }
                            }
                            if (!isExist) {
                                listItem.add(map);
                                adapter.notifyDataSetChanged();
                                Log.e("a", "发现蓝牙" + device.getAddress() + "状态" + device.getBondState() + "type" + device.getType() + device.describeContents());

                            }
                        }
                    });
                }
            };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //用户输入了密码，确认连接
        if (requestCode == 1) {
            try {
             //   Toast.makeText(BTListActivity.this, "现在已连接设置", Toast.LENGTH_LONG).show();
                String password = data.getStringExtra("password");

                DeviceListDatabaseHelper helper = new DeviceListDatabaseHelper(this);
                helper.insertDevice(data.getStringExtra("address"), data.getStringExtra("deviceName"), password);
                helper.close();

                Intent intent = this.getIntent();
                intent.putExtra("address", data.getStringExtra("address"));
                intent.putExtra("deviceName", data.getStringExtra("deviceName"));
                intent.putExtra("password", password);
                BTListActivity.this.setResult(RESULT_OK, intent);
                //保存最近的设备
                BluetoothUtils.storeLastAddress(this, data.getStringExtra("address"));

                /**
                 * 可以点击
                 */
                isConnect = true;
                connectInitMethod(tempView, tempDevice, tempdeviceAddress, tempModel);


                 this.finish();
            } catch (Exception e) {

            }
        }
    }


    public void ResultMethod(Intent data) {


//        if (requestCode == 1 && data != null && resultCode == RESULT_OK) {
        RemoteDeviceModel model = new RemoteDeviceModel();
        model.name = data.getStringExtra("deviceName");
        model.address = data.getStringExtra("address");
        remoteDeviceModel = model;
        // PublicKind.remoteDeviceModel = remoteDeviceModel;
        mBluetoothLeService.disconnect();
        boolean result = mBluetoothLeService.connect(model);
        mNotifyCharacteristic = null;
//            proccessOnResult = true;
        //连接蓝牙设备后，返回“99”指令
        //蓝牙4.0的连接是需要时间的，所以延时1s
        //int count = 0;

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
//                    if(msg.what == 0x11){

                Toast.makeText(BTListActivity.this, "现在已连接设置", Toast.LENGTH_LONG).show();


//                    }
//                        bar.setVisibility(View.VISIBLE);
//                        bar.setProgress(progress);
                //setProgressBarVisibility(true);
                //bar.setVisibility(View.VISIBLE );
//                        if(progress == 200){
//                            bar.setVisibility(View.INVISIBLE );
//                        }
            }
        };

        if (result) {

            new Thread(new Runnable() {
                @Override
                public void run() {
//                        while (progress < 200) {
//                            progress += 20;
                    SystemClock.sleep(500);
                    Message msg = new Message();
                    msg.what = 0x11;
                    handler.sendMessage(msg);
//                        }
                }
            }).start();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
     //   unbindService(mServiceConnection);
    }

}
