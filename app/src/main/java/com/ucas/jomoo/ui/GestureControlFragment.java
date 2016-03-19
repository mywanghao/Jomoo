package com.ucas.jomoo.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.nineoldandroids.view.ViewHelper;
import com.ucas.jomoo.R;
import com.ucas.jomoo.helper.IFlyTalkHelper;
import com.ucas.jomoo.tools.BluetoothUtils;
import com.ucas.jomoo.tools.SPUtils;

import java.util.Timer;
import java.util.TimerTask;

import static com.ucas.jomoo.ui.ButtonControlFragment.State.BULB;
import static com.ucas.jomoo.ui.ButtonControlFragment.State.CLEAN;
import static com.ucas.jomoo.ui.ButtonControlFragment.State.DOWN;
import static com.ucas.jomoo.ui.ButtonControlFragment.State.DRYING;
import static com.ucas.jomoo.ui.ButtonControlFragment.State.FAN;
import static com.ucas.jomoo.ui.ButtonControlFragment.State.STOP;
import static com.ucas.jomoo.ui.ButtonControlFragment.State.UP;

//import android.support.annotation.Nullable;

/**
 * Created by ivanchou on 7/31/15.
 */
public class GestureControlFragment extends Fragment implements View.OnClickListener, GestureDetector.OnGestureListener, View.OnTouchListener {

    public static GestureControlFragment newInstance(int page) {
        GestureControlFragment gestureControlFragment = new GestureControlFragment();

        return gestureControlFragment;
    }
    private IFlyTalkHelper mIFlyTalkHelper;

    private static float DISTANCE ; //dp
    private static final int FLING_MIN_DISTANCE = 50;
    private static final int FLING_MIN_VELOCITY = 0;
    public boolean flag = false;
    public float moveDis = 0;
    GestureDetector mGestureDetector = null;
    public ButtonControlFragment.State mState;
    public boolean firstFlag = true;
    public float position;
    public DisplayMetrics metrics;

    float scale = (float) 0.1;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (firstFlag) {
                position = barIv.getY();
                firstFlag = false;
            }
            ViewHelper.setTranslationY(barIv, (float) msg.obj);
            float scale = barIv.getY() - position;
            ViewHelper.setScaleY(blackView, scale);
            ViewHelper.setTranslationY(blackView, scale/2);

        }
    };

    private Context context;
    int[] status;

    public ImageButton exitBtn;
    public ImageButton fanleftBtn;
    public ImageButton fanrightBtn;
    public ImageButton cleanleftBtn;
    public ImageButton cleanrightBtn;
    public ImageButton bulbBtn;
    public ImageButton power_btn;
    public ImageButton powerBtn;
    public Button connectBtn;

    public ImageView barIv;
    public View blackView;

    public Timer animationTimer;
    /**
     * @param direction －2-向上；2-向下
     */
    private int direction;
    private boolean boo_version,isPlay;
    private ImageButton voice_btn ;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        status = ((MainActivity) context).statues;
        metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        DISTANCE = BluetoothUtils.dip2px(context, 155);
    }


    boolean up=false,down=false,bulb = false,fan=false,clean=false,dryang=false;


    // @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_gesturecontrol, container, false);

        boo_version =  SPUtils.getInstance(getActivity()).getBoolean("version",false);
        voice_btn   = (ImageButton)  v.findViewById(R.id.voice_btn);
        exitBtn = (ImageButton) v.findViewById(R.id.left_exit_btn);
        fanleftBtn = (ImageButton) v.findViewById(R.id.fan_left_btn);
        fanrightBtn = (ImageButton) v.findViewById(R.id.fan_right_btn);
        cleanleftBtn = (ImageButton) v.findViewById(R.id.clean_left_btn);
        cleanrightBtn = (ImageButton) v.findViewById(R.id.clean_right_btn);
        bulbBtn = (ImageButton) v.findViewById(R.id.bulb_center_btn);
        powerBtn = (ImageButton) v.findViewById(R.id.power_btn);
        power_btn = (ImageButton) v.findViewById(R.id.power_btn);


        v.findViewById(R.id.btn_setter).setOnClickListener(this);
        v.findViewById(R.id.btn_help).setOnClickListener(this);
        v.findViewById(R.id.nav_close).setOnClickListener(this);

        //  connectBtn = (Button) v.findViewById(R.id.connect_btn);
        mIFlyTalkHelper = new IFlyTalkHelper(getActivity());

        mIFlyTalkHelper.setIFlyTalkListener(new IFlyTalkHelper.IFlyTalkListener() {
            @Override
            public void onGetRecognizerWord(String words) {
                int id  = 0 ;

                if (!TextUtils.isEmpty(words)) {
                   // //.makeText(getActivity(), words, //.LENGTH_SHORT).show();

                    View view = null;
                    switch (words) {
                        case IFlyTalkHelper.UP:
//                            ((MainActivity) context).doOpt(BluetoothUtils.UP,1);
                            id = R.id.up_btn;
                            setAnimation(-2);
                            mState = UP;
                            up = !up;
                        //    upBtn.setSelected(true);
                        //    downBtn.setSelected(false);
                        //    stopBtn.setSelected(false);
                            break;
                        case IFlyTalkHelper.DOWN:
  //                          ((MainActivity) context).doOpt(BluetoothUtils.DOWN,1);
                            id = R.id.down_btn;
                            setAnimation(2);
                            down = !down;
                            mState = DOWN;
                            //    upBtn.setSelected(false);
                        //    downBtn.setSelected(true);
                        //    stopBtn.setSelected(false);
                            break;
                        case IFlyTalkHelper.OPEN_CLEARN:
                            mState = CLEAN;
                            id = R.id.clean_btn;
                            break;
                        case IFlyTalkHelper.CLOSE_CLEAN:
                            clean = true;

                            mState = CLEAN;
                            id = R.id.clean_btn;

                            Log.e("TAA","———————停止消毒："+clean);
                            break;

                        case IFlyTalkHelper.OPEN_LIGHTING:
                            mState = BULB;
                            id =  R.id.bulb_btn;
                            break;
                        case IFlyTalkHelper.CLOSE_LIGHTING:
                            mState = BULB;
                                    id =  R.id.bulb_btn;
                            bulb = true;

                            break;
                        case IFlyTalkHelper.OPEN_FAN:
                            id = R.id.fan_btn;
                            mState = FAN;
                            break;
                        case IFlyTalkHelper.CLOSE_FAN:
                            id = R.id.fan_btn;
                            mState = FAN;
                            fan = true;
                            break;

                        case IFlyTalkHelper.STOP:
            //                ((MainActivity) context).doOpt(BluetoothUtils.PAUSE,1);
                           // upBtn.setSelected(false);
                           // downBtn.setSelected(false);
                           // stopBtn.setSelected(false);
                            mState = STOP;
                            status[0] = status[1] = 0;
                            break;
                        case IFlyTalkHelper.CLOSE_POWER:

                            if(boo_version){
                                ((MainActivity) context).doOpt(BluetoothUtils.POWER2,2);
                            }else{
                                ((MainActivity) context).doOpt(BluetoothUtils.POWER,1);

                            }

                   //         //.makeText(context, "暂不支持此功能", //.LENGTH_SHORT).show();
                            return;

                        case IFlyTalkHelper.DRYING_OPEN://烘干打开
                            mState  = DRYING;
                            dryang = false;
                            break;
                        case IFlyTalkHelper.DRYING_CLOSE://烘干关闭
                            mState  = DRYING;
                            dryang = true;
                            break;
                        default:

                            return;
                    }

                    view = voice_btn ;
                    view.setSelected(false);
                    final View finalView = view;
                    final int finalId = id;
                    isPlay = false;
                    view.post(new Runnable() {
                        @Override
                        public void run() {
                            if(boo_version){
                                version2(finalView, finalId);
                            }else{
                                version1(finalView, finalId);
                            }
                        }
                    });



                }
            }

            @Override
            public void onGetRecognizerWordError(String errorWords) {

                if (!TextUtils.isEmpty(errorWords)) {
                    //.makeText(getActivity(), errorWords, //.LENGTH_SHORT).show();
                }
            }
        });
        barIv = (ImageView) v.findViewById(R.id.bar_iv);
//        barIv.setOnClickListener(this);
        barIv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });

        blackView = v.findViewById(R.id.black_view);
        voice_btn.setOnClickListener(this);
        exitBtn.setOnClickListener(this);
        fanleftBtn.setOnClickListener(this);
        fanrightBtn.setOnClickListener(this);
        cleanleftBtn.setOnClickListener(this);
        cleanrightBtn.setOnClickListener(this);
        bulbBtn.setOnClickListener(this);
        powerBtn.setOnClickListener(this);
   //     connectBtn.setOnClickListener(this);

        View view = v.findViewById(R.id.rl_layout);
        mGestureDetector = new GestureDetector(this);
        view.setOnTouchListener(this);
        view.setFocusable(true);
        view.setClickable(true);
        view.setLongClickable(true);

//        startActivity(new Intent(getActivity(),ModifyPasswordActivity.class));

        return v;
    }



    @Override
    public void onResume() {
        super.onResume();
        boo_version =  SPUtils.getInstance(getActivity()).getBoolean("version",false);
        gestureOnResumeMethod();

    }


    public void gestureOnResumeMethod(){
        isPlay =  SPUtils.getInstance(getActivity()).getBoolean("isplay",isPlay);

        if (!isPlay){
            mIFlyTalkHelper.endlistening();
            voice_btn.setSelected(false);
            return;
        }

        mIFlyTalkHelper.startListening();
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(60 * 1000);
                    mIFlyTalkHelper.endlistening();
                    voice_btn.post(new Runnable() {
                        @Override
                        public void run() {
                            voice_btn.setSelected(false);
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                super.run();
            }
        }.start();
        voice_btn.setSelected(true);

    }


    @Override
    public void onClick(final View v) {
        switch (v.getId()) {

            case R.id.nav_close:
                if (  ((MainActivity)getActivity()).mBluetoothLeService != null) {
                    ((MainActivity)getActivity()). mBluetoothLeService.disconnect();
                }
                getActivity().finish();
                break;

            case R.id.btn_help:
                //   mViewPager.setCurrentItem(ControlPagerAdapter.HELP_FRAGMENT);
                startActivity(new Intent(getActivity(),HelpActivity.class));
                getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;

            case R.id.btn_setter:
                ((MainActivity)getActivity()).gotoBTListActivity();
                break;



            case R.id.left_exit_btn:
                ((MainActivity) context).jommoApp.exitApp(context);
                break;
            case R.id.fan_left_btn:
                mState = FAN;
                if (fanrightBtn.isSelected()) {
                    fanrightBtn.setSelected(false);
                    fanleftBtn.setSelected(false);
                    status[4] = 0;
                    if(boo_version)
                        ((MainActivity) context).doOpt(BluetoothUtils.FAN_OFF2,2);
                    else
                        ((MainActivity) context).doOpt(BluetoothUtils.FAN_OFF,1);

                } else {
                    fanrightBtn.setSelected(true);
                    fanleftBtn.setSelected(true);
                    status[4] = 1;
                    if(boo_version)
                        ((MainActivity) context).doOpt(BluetoothUtils.FAN_ON2,2);
                    else
                        ((MainActivity) context).doOpt(BluetoothUtils.FAN_ON,1);

                }
                break;
            case R.id.fan_right_btn:
                if (fanleftBtn.isSelected()) {
                    fanrightBtn.setSelected(false);
                    fanleftBtn.setSelected(false);
                    status[4] = 0;

                    if(boo_version)
                        ((MainActivity) context).doOpt(BluetoothUtils.FAN_OFF2,2);
                    else
                        ((MainActivity) context).doOpt(BluetoothUtils.FAN_OFF,1);


                } else {
                    fanrightBtn.setSelected(true);
                    fanleftBtn.setSelected(true);
                    status[4] = 1;
                    if(boo_version)
                        ((MainActivity) context).doOpt(BluetoothUtils.FAN_ON2,2);
                    else
                        ((MainActivity) context).doOpt(BluetoothUtils.FAN_ON,1);

                }
                mState = FAN;
                break;
            case R.id.clean_left_btn:
                if (cleanleftBtn.isSelected()) {
                    cleanleftBtn.setSelected(false);
                    cleanrightBtn.setSelected(false);
                    status[2] = 0;
                    if(boo_version)
                        ((MainActivity) context).doOpt(BluetoothUtils.CLEAN_OFF2,2);
                    else
                        ((MainActivity) context).doOpt(BluetoothUtils.CLEAN_OFF,1);


                } else {
                    cleanleftBtn.setSelected(true);
                    cleanrightBtn.setSelected(true);
                    status[2] = 1;

                    if(boo_version)
                        ((MainActivity) context).doOpt(BluetoothUtils.CLEAN_ON2,2);
                    else
                        ((MainActivity) context).doOpt(BluetoothUtils.CLEAN_ON,1);

                }
                mState = CLEAN;
                break;
            case R.id.clean_right_btn:
                if (cleanrightBtn.isSelected()) {
                    cleanleftBtn.setSelected(false);
                    cleanrightBtn.setSelected(false);
                    status[2] = 0;

                    if(boo_version)
                        ((MainActivity) context).doOpt(BluetoothUtils.CLEAN_OFF2,2);
                    else
                        ((MainActivity) context).doOpt(BluetoothUtils.CLEAN_OFF,1);

                } else {
                    cleanleftBtn.setSelected(true);
                    cleanrightBtn.setSelected(true);
                    status[2] = 1;

                    if(boo_version)
                        ((MainActivity) context).doOpt(BluetoothUtils.CLEAN_ON2,2);
                    else
                        ((MainActivity) context).doOpt(BluetoothUtils.CLEAN_ON,1);

                }
                mState = CLEAN;
                break;
            case R.id.bulb_center_btn:
                if (bulbBtn.isSelected()) {
                    bulbBtn.setSelected(false);
                    status[3] = 0;
                    if(boo_version)
                        ((MainActivity) context).doOpt(BluetoothUtils.BULB_OFF2,2);
                    else
                        ((MainActivity) context).doOpt(BluetoothUtils.BULB_OFF,1);

                } else {
                    bulbBtn.setSelected(true);
                    status[3] = 1;

                    if(boo_version)
                        ((MainActivity) context).doOpt(BluetoothUtils.BULB_ON2,2);
                    else
                        ((MainActivity) context).doOpt(BluetoothUtils.BULB_ON,1);

                }
                mState = BULB;
                break;
            case R.id.power_btn:
                //((MainActivity) context).doOpt(BluetoothUtils.POWER);
                //((MainActivity) context).closeAll();
                if(boo_version){
                    ((MainActivity) context).doOpt(BluetoothUtils.POWER2,2);
                }else{
                    ((MainActivity) context).doOpt(BluetoothUtils.POWER,1);
                }
                //.makeText(context, "开启/关闭", //.LENGTH_SHORT).show();
                break;
//            case R.id.connect_btn:
//                ((MainActivity)this.getActivity()).gotoBTListActivity();
//                break;

            case R.id.bar_iv:
                if (status[0] != 0 || status[1] != 0) {
                    status[0] = status[1] = 0;
                    flag = false;
                }

                if(boo_version){
                    ((MainActivity) context).doOpt(BluetoothUtils.PAUSE2,2);
                }else{
                    ((MainActivity) context).doOpt(BluetoothUtils.PAUSE,1);
                }

                break;

            case R.id.voice_btn:
           //     mIFlyTalkHelper.
                isPlay = SPUtils.getInstance(getActivity()).getBoolean("isplay", isPlay);
                isPlay = !isPlay;
                SPUtils.getInstance(getActivity()).save("isplay", isPlay);
                if (!isPlay){
                    mIFlyTalkHelper.endlistening();
                    v.setSelected(false);
                    return;
                }

                mIFlyTalkHelper.startListening();
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(60 * 1000);
                            mIFlyTalkHelper.endlistening();
                            v.post(new Runnable() {
                                @Override
                                public void run() {
                                    v.setSelected(false);
                                }
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        super.run();
                    }
                }.start();
                v.setSelected(true);
                return;
                //break;

        }
    }


    /**
     * @param direction －2-向上；2-向下
     */
    public void setAnimation(final int direction) {
        if (this.direction == direction) {
            return;
        }
        this.direction = direction;
        if (animationTimer != null) {
            return;
        }
        animationTimer = new Timer();
        animationTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (flag) {
                    //向上
                    if (GestureControlFragment.this.direction == -2 & (moveDis > - DISTANCE)) {
                        moveDis += -2;
                        Message msg = new Message();
                        msg.obj = moveDis;
                        handler.sendMessage(msg);
                    } else
                    //向下
                    if (GestureControlFragment.this.direction == 2 & (moveDis < 0)) {
                        moveDis += 2;
                        Message msg = new Message();
                        msg.obj = moveDis;
                        handler.sendMessage(msg);
                    } else {
                        flag = false;
                    }

                }
            }
        }, 0, 50);
        /*
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (flag) {
                    moveDis += direction;
                    if (moveDis > 0 || moveDis < 0-distance) {
                        //status[0] = status[1] = 0;
                        //上升或下降到一定高度就关闭电机
                        //((MainActivity) context).doOpt(BluetoothUtils.PAUSE);
                        flag = false;
                        return;
                    }
                    Message msg = new Message();
                    msg.obj = moveDis;
                    handler.sendMessage(msg);
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return;
            }
        }).start();
        */
    }


    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (e1.getY() - e2.getY() > FLING_MIN_DISTANCE
            && Math.abs(velocityX) > FLING_MIN_VELOCITY) {

            flag = false;
            //SystemClock.sleep(60);
            status[0] = 1;
            status[1] = 0;
            if(boo_version) {
                ((MainActivity) context).doOpt(BluetoothUtils.UP2, 2);
            }else{
                ((MainActivity) context).doOpt(BluetoothUtils.UP, 1);
            }

            flag = true;
            setAnimation(-2);
//            //.makeText(context, "上升", //.LENGTH_SHORT).show();
        }

        else if (e2.getY() - e1.getY() > FLING_MIN_DISTANCE
                && Math.abs(velocityX) > FLING_MIN_VELOCITY) {
            // Fling down
//            //.makeText(getActivity(), "向下手势", //.LENGTH_SHORT).show();
//            if (flag) {
//                flag = false;
//                ((MainActivity) context).doOpt(BluetoothUtils.PAUSE);
//                status[0] = 0;
//            } else {
                flag = false;
            //SystemClock.sleep(60);
            status[1] = 1;
                status[0] = 0;
            if(boo_version){
                ((MainActivity) context).doOpt(BluetoothUtils.DOWN2,2);
            }else{
                ((MainActivity) context).doOpt(BluetoothUtils.DOWN,1);
            }

            flag = true;
                setAnimation(2);
//            //.makeText(context, "下降", //.LENGTH_SHORT).show();

//            }
        }
        return false;
    }


    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        if (status[0] != 0 || status[1] != 0) {
            status[0] = status[1] = 0;
            flag = false;
        }
        if(boo_version){
            ((MainActivity) context).doOpt(BluetoothUtils.PAUSE2,2);
        }else{
            ((MainActivity) context).doOpt(BluetoothUtils.PAUSE,1);
        }

        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    public void refresh() {
        ImageButton[] buttons = {fanleftBtn, fanrightBtn, cleanleftBtn, cleanrightBtn, bulbBtn};

        if (status[2] == 1) {
            buttons[2].setSelected(true);
            buttons[3].setSelected(true);
        } else {
            buttons[2].setSelected(false);
            buttons[3].setSelected(false);
        }

        if (status[3] == 1) {
            buttons[4].setSelected(true);
        } else {
            buttons[4].setSelected(false);
        }

        if (status[4] == 1) {
            buttons[0].setSelected(true);
            buttons[1].setSelected(true);
        } else {
            buttons[0].setSelected(false);
            buttons[1].setSelected(false);
        }


        // up
        if (status[0] == 1) {
            if (!flag) {
                flag = true;
                setAnimation(-2);
            }
            return;
        } else if (status[1] == 1) {
            if (!flag) {
                flag = true;
                setAnimation(2);
            }
            return;
        } else {
            flag = false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (this.animationTimer != null) {
            this.animationTimer.cancel();
        }
    }








    private void version2(View view,int id) {
        if (view != null && id != R.id.stop_btn) {
            if (!view.isSelected()) {
                view.setSelected(false);
                for (int i = 0; i < resBtn.length; i++) {
                    if (id == resBtn[i]) {
                        status[i] = 1;
                    }
                }
                if (mState == UP && up) {
                    ((MainActivity) context).doOpt(BluetoothUtils.UP2, 2);
                    status[1] = 0;
                    //.makeText(getActivity(), "执行语音S2的上升命令", //.LENGTH_SHORT).show();

                    ((GestureControlFragment) ((MainActivity) context).getSupportFragmentManager().getFragments().get(0)).flag = false;
                } else if (mState == DOWN && down) {
                    ((MainActivity) context).doOpt(BluetoothUtils.DOWN2, 2);
                    status[0] = 0;
                    //.makeText(getActivity(), "执行语音S2的下降命令", //.LENGTH_SHORT).show();

                    ((GestureControlFragment) ((MainActivity) context).getSupportFragmentManager().getFragments().get(0)).flag = false;
                } else if (mState == BULB && !bulb) {
                    ((MainActivity) context).doOpt(BluetoothUtils.BULB_ON2, 2);
                    //.makeText(getActivity(), "执行语音S2的照明命令", //.LENGTH_SHORT).show();

                } else if (mState == FAN && !fan) {
                    ((MainActivity) context).doOpt(BluetoothUtils.FAN_ON2, 2);
                    //.makeText(getActivity(), "执行语音S2的吹风命令", //.LENGTH_SHORT).show();

                } else if (mState == CLEAN && !clean) {
                    //.makeText(getActivity(), "执行语音S2的消毒命令", //.LENGTH_SHORT).show();

                    ((MainActivity) context).doOpt(BluetoothUtils.CLEAN_ON2, 2);
                } else if (mState == DRYING && !dryang) {
                    //.makeText(getActivity(), "执行语音S2的烘干命令", //.LENGTH_SHORT).show();

                    ((MainActivity) context).doOpt(BluetoothUtils.DRYING_ON2, 2);
                } else {
                    view.setSelected(false);
                    for (int i = 0; i < resBtn.length; i++) {
                        if (id == resBtn[i]) {
                            status[i] = 0;
                        }
                    }
                    if (mState == UP && !up) {
                        ((MainActivity) context).doOpt(BluetoothUtils.PAUSE2, 2);
                        //.makeText(getActivity(), "执行语音S2的停止命令", //.LENGTH_SHORT).show();

                    } else if (mState == DOWN && !down) {
                        ((MainActivity) context).doOpt(BluetoothUtils.PAUSE2, 2);
                        //.makeText(getActivity(), "执行语音S2的停止命令", //.LENGTH_SHORT).show();

                    } else if (mState == BULB && bulb) {
                        ((MainActivity) context).doOpt(BluetoothUtils.BULB_OFF2, 2);
                        //.makeText(getActivity(), "执行语音S2的停止照明", //.LENGTH_SHORT).show();

                    } else if (mState == FAN && fan) {
                        //.makeText(getActivity(), "执行语音S2的停止吹风", //.LENGTH_SHORT).show();

                        ((MainActivity) context).doOpt(BluetoothUtils.FAN_OFF2, 2);
                    } else if (mState == CLEAN && clean) {
                        //.makeText(getActivity(), "执行语音S2的停止消毒", //.LENGTH_SHORT).show();

                        ((MainActivity) context).doOpt(BluetoothUtils.CLEAN_OFF2, 2);
                    } else if (mState == DRYING && dryang) {
                        //.makeText(getActivity(), "执行语音S2的停止烘干", //.LENGTH_SHORT).show();

                        ((MainActivity) context).doOpt(BluetoothUtils.DRYING_OFF2, 2);
                    }else if(mState ==STOP){
                        status[0] = status[1] = 0;
                        ((MainActivity) context).doOpt(BluetoothUtils.PAUSE2, 2);
                        //.makeText(getActivity(), "执行S2的语音停止命令——PAUSE2, 2", //.LENGTH_SHORT).show();
                    }
                }
            } else {

            }
        }else{
        }
    }


    private void version1(View view , int id) {
        if (view != null && id != R.id.stop_btn) {
            if (!view.isSelected()) {
                view.setSelected(false);
                for (int i = 0; i < resBtn.length; i++) {
                    if (id == resBtn[i]) {
                        status[i] = 1;
                    }
                }
                if (mState == UP &&up) {
                    ((MainActivity) context).doOpt(BluetoothUtils.UP, 1);
                    status[1] = 0;
                    ((GestureControlFragment) ((MainActivity) context).getSupportFragmentManager().getFragments().get(0)).flag = false;
                    //.makeText(getActivity(), "执行S1上升", //.LENGTH_SHORT).show();

                } else if (mState == DOWN&&down) {
                    ((MainActivity) context).doOpt(BluetoothUtils.DOWN, 1);
                    status[0] = 0;
                    ((GestureControlFragment) ((MainActivity) context).getSupportFragmentManager().getFragments().get(0)).flag = false;
                    //.makeText(getActivity(), "执行S1降", //.LENGTH_SHORT).show();

                } else if (mState == BULB&&!bulb) {
                    //.makeText(getActivity(), "执行S1照明", //.LENGTH_SHORT).show();

                    ((MainActivity) context).doOpt(BluetoothUtils.BULB_ON, 1);
                } else if (mState == FAN&&!fan) {
                    //.makeText(getActivity(), "执行S1风机", //.LENGTH_SHORT).show();

                    ((MainActivity) context).doOpt(BluetoothUtils.FAN_ON, 1);
                } else if (mState == CLEAN&&!clean) {
                    //.makeText(getActivity(), "执行S1消毒+"+clean, //.LENGTH_SHORT).show();

                    ((MainActivity) context).doOpt(BluetoothUtils.CLEAN_ON, 1);
                } else if (mState == DRYING&&!dryang) {
                    //.makeText(getActivity(), "执行S1烘干", //.LENGTH_SHORT).show();

                    ((MainActivity) context).doOpt(BluetoothUtils.DRYING_ON, 1);
                } else {
                      view.setSelected(false);
                    for (int i = 0; i < resBtn.length; i++) {
                        if (id == resBtn[i]) {
                            status[i] = 0;
                        }
                    }
                    if (mState == UP&&!up) {
                        //.makeText(getActivity(), "停止上升", //.LENGTH_SHORT).show();

                        ((MainActivity) context).doOpt(BluetoothUtils.PAUSE, 1);

                    } else if (mState == DOWN&&!down) {
                        //.makeText(getActivity(), "停止下降", //.LENGTH_SHORT).show();

                        ((MainActivity) context).doOpt(BluetoothUtils.PAUSE, 1);

                    } else if (mState == BULB&&bulb) {
                        //.makeText(getActivity(), "执行S1关闭照明", //.LENGTH_SHORT).show();

                        ((MainActivity) context).doOpt(BluetoothUtils.BULB_OFF, 1);
                        bulb=false;
                    } else if (mState == FAN&&fan) {
                        //.makeText(getActivity(), "执行S1风机关闭", //.LENGTH_SHORT).show();

                        ((MainActivity) context).doOpt(BluetoothUtils.FAN_OFF, 1);
                        fan  = false;
                    } else if (mState == CLEAN&&clean) {
                        //.makeText(getActivity(), "执行S1消毒关闭L:"+clean, //.LENGTH_SHORT).show();

                        ((MainActivity) context).doOpt(BluetoothUtils.CLEAN_OFF, 1);
                        clean = false;
                    } else if (mState == DRYING &&dryang) {
                        //.makeText(getActivity(), "执行S1烘干关闭", //.LENGTH_SHORT).show();

                        ((MainActivity) context).doOpt(BluetoothUtils.DRYING_OFF, 1);
                        dryang = false;
                    }else if(mState ==STOP){
                        ((MainActivity) context).doOpt(BluetoothUtils.PAUSE, 1);
                        //.makeText(getActivity(), "执行S1的语音停止命令", //.LENGTH_SHORT).show();
                    }
                }
            }
        } else {
       //     upBtn.setSelected(false);
       //     downBtn.setSelected(false);
        //    status[0] = status[1] = 0;
        //    ((MainActivity) context).doOpt(BluetoothUtils.PAUSE, 1);
        //    //.makeText(getActivity(), "执行S1的语音停止命令", //.LENGTH_SHORT).show();

        }
    }

    int[] resBtn = {R.id.up_btn, R.id.down_btn, R.id.clean_btn, R.id.bulb_btn, R.id.fan_btn};



}
