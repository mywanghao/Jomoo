package com.ucas.jomoo;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.ucas.jomoo.common.Constants;
import com.ucas.jomoo.ui.BluetoothLeService;

import java.util.Stack;

/**
 * 应用全局控制
 * Created by ivanchou on 1/15/2015.
 */
public class JommoApp extends Application {
    private static JommoApp instance;
    private static Stack<Activity> activityStack;


    public BluetoothLeService bluetoothLeService;

    public JommoApp() {
    }

    public synchronized static JommoApp getInstance() {
        if (null == instance) {
            instance = new JommoApp();
        }
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SpeechUtility.createUtility(this, SpeechConstant.APPID + Constants.iFLYTEK_APP_ID);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }

    /**
     * 添加Activity到堆栈
     */
    public void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
        activityStack.add(activity);
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        for (Activity activity : activityStack) {
            if (activity != null) {
                activity.finish();
            }
        }
        activityStack.clear();
    }

    /**
     * 退出应用程序
     */
    public void exitApp(Context context) {
        try {
            finishAllActivity();
            ActivityManager activityMgr = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            activityMgr.killBackgroundProcesses(context.getPackageName());
            System.exit(0);
        } catch (Exception e) {
        }
    }
}
