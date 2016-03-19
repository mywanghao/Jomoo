package com.ucas.jomoo.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.ucas.jomoo.R;
import com.ucas.jomoo.helper.IFlyTalkHelper;
import com.ucas.jomoo.tools.BluetoothUtils;
import com.ucas.jomoo.tools.SPUtils;

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
public class ButtonControlFragment extends Fragment implements View.OnClickListener {
    public static ButtonControlFragment newInstance(int page) {
        ButtonControlFragment buttonControlFragment = new ButtonControlFragment();

        return buttonControlFragment;
    }

    private Context context;


    int[] status;

    public enum State {UP, CLEAN, STOP, BULB, FAN, DOWN,DRYING}

    public State mState;

    public ImageButton upBtn;
    public ImageButton cleanBtn;
    public ImageButton stopBtn;
    public ImageButton bulbBtn;
    public ImageButton fanBtn;
    public ImageButton downBtn;
    public ImageButton powerBtn;
    public ImageButton exitBtn;
    public ImageButton hongganBtn;
    public Button connectBtn;
    private ImageButton mVoiceBtn;

    private boolean boo_version,isPlay ;
    private IFlyTalkHelper mIFlyTalkHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();

        status = ((MainActivity) context).statues;
    }




    //@Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_buttoncontrol, container, false);

        boo_version =  SPUtils.getInstance(getActivity()).getBoolean("version",false);


        hongganBtn  = (ImageButton) view.findViewById(R.id.fan_honggan);
        upBtn = (ImageButton) view.findViewById(R.id.up_btn);
        cleanBtn = (ImageButton) view.findViewById(R.id.clean_btn);
        stopBtn = (ImageButton) view.findViewById(R.id.stop_btn);
        bulbBtn = (ImageButton) view.findViewById(R.id.bulb_btn);
        fanBtn = (ImageButton) view.findViewById(R.id.fan_btn);
        downBtn = (ImageButton) view.findViewById(R.id.down_btn);
        powerBtn = (ImageButton) view.findViewById(R.id.power_btn);
        exitBtn = (ImageButton) view.findViewById(R.id.back_btn);

        view.findViewById(R.id.btn_setter).setOnClickListener(this);
        view.findViewById(R.id.btn_help).setOnClickListener(this);
        view.findViewById(R.id.nav_close).setOnClickListener(this);

        // connectBtn = (Button) view.findViewById(R.id.connect_btn);
        mVoiceBtn = (ImageButton) view.findViewById(R.id.voice_btn);
        mIFlyTalkHelper = new IFlyTalkHelper(getActivity());

        upBtn.setOnClickListener(this);
        cleanBtn.setOnClickListener(this);
        stopBtn.setOnClickListener(this);
        bulbBtn.setOnClickListener(this);
        fanBtn.setOnClickListener(this);
        downBtn.setOnClickListener(this);
        powerBtn.setOnClickListener(this);
        exitBtn.setOnClickListener(this);
        hongganBtn.setOnClickListener(this);
   //     connectBtn.setOnClickListener(this);
        mVoiceBtn.setOnClickListener(this);


        mIFlyTalkHelper.setIFlyTalkListener(new IFlyTalkHelper.IFlyTalkListener() {
            @Override
            public void onGetRecognizerWord(String words) {
                if (!TextUtils.isEmpty(words)) {
                 //   //.makeText(getActivity(), words, //.LENGTH_SHORT).show();

                    View view = null;
                    switch (words) {
                        case IFlyTalkHelper.UP:
                       //     ((MainActivity) context).doOpt(BluetoothUtils.UP,1);
                            upBtn.setSelected(true);
                            downBtn.setSelected(false);
                            stopBtn.setSelected(false);
                            up = !up;
                            mState = UP;
                            break;
                        case IFlyTalkHelper.DOWN:
                           // ((MainActivity) context).doOpt(BluetoothUtils.DOWN,1);
                            upBtn.setSelected(false);
                            downBtn.setSelected(true);
                            stopBtn.setSelected(false);
                            down = !down;
                            mState = DOWN;
                            break;
                        case IFlyTalkHelper.OPEN_CLEARN:

                            mState = CLEAN;
                            view = cleanBtn;
                            clean = false;
                            break;
                        case IFlyTalkHelper.CLOSE_CLEAN:
                            clean = true;
                            mState = CLEAN;
                            view = cleanBtn;
                            break;

                        case IFlyTalkHelper.OPEN_LIGHTING:
                            view = bulbBtn;
                            mState = BULB;
                            bulb = false;
                             break;
                        case IFlyTalkHelper.CLOSE_LIGHTING:
                            view = bulbBtn;
                            mState = BULB;
                            bulb = true;
                            break;
                        case IFlyTalkHelper.OPEN_FAN:
                            view = fanBtn;
                            mState = FAN;
                            fan = false;
                            break;
                        case IFlyTalkHelper.CLOSE_FAN:
                            view = fanBtn;
                            mState = FAN;
                            fan = true;
                            break;

                        case IFlyTalkHelper.STOP:
                         //   ((MainActivity) context).doOpt(BluetoothUtils.PAUSE,1);
                            upBtn.setSelected(false);
                            downBtn.setSelected(false);
                            stopBtn.setSelected(false);
                            status[0] = status[1] = 0;
                            mState = STOP;
                            break;
                        case IFlyTalkHelper.CLOSE_POWER:

                            if(boo_version){
                                ((MainActivity) context).doOpt(BluetoothUtils.POWER2,2);
                            }else{
                                ((MainActivity) context).doOpt(BluetoothUtils.POWER,1);

                            }


//                            //.makeText(context, "暂不支持此功能", //.LENGTH_SHORT).show();

                            return;

                        case IFlyTalkHelper.DRYING_OPEN:
                            mState = DRYING;
                            dryang = false;
//                            ((MainActivity) context).doOpt(BluetoothUtils.DRYING_ON2, 2);
  //                          //.makeText(context, "2.0命令的烘干已发送", //.LENGTH_SHORT).show();

                            break;
                        case IFlyTalkHelper.DRYING_CLOSE:
                            mState = DRYING;
                            dryang = true;
                            break;

                        default:

                            return;
                    }


                    view = mVoiceBtn ;

                    view.setSelected(false);
                    isPlay = false;

                    final View finalView = view;


                    view.post(new Runnable() {
                        @Override
                        public void run() {
                            if(boo_version){
                                version2(finalView);
                            }else{
                                version1(finalView);
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

        return view;
    }

    boolean up=false,down=false,bulb = false,fan=false,clean=false,dryang=false;


    private void version1(View view) {
        if (view != null && view.getId() != R.id.stop_btn) {
            if (!view.isSelected()) {
                view.setSelected(false);
                for (int i = 0; i < resBtn.length; i++) {
                    if (view.getId() == resBtn[i]) {
                        status[i] = 1;
                    }
                }

                if (mState == UP&&up ) {
                    ((MainActivity) context).doOpt(BluetoothUtils.UP, 1);
                    status[1] = 0;
                    ((GestureControlFragment) ((MainActivity) context).getSupportFragmentManager().getFragments().get(0)).flag = false;
                    //.makeText(getActivity(), "执行S1上升", //.LENGTH_SHORT).show();

                } else if (mState == DOWN&&down ) {
                    ((MainActivity) context).doOpt(BluetoothUtils.DOWN, 1);
                    status[0] = 0;
                    ((GestureControlFragment) ((MainActivity) context).getSupportFragmentManager().getFragments().get(0)).flag = false;
                    //.makeText(getActivity(), "执行S1降", //.LENGTH_SHORT).show();

                } else if (mState == BULB&&!bulb ) {
                    //.makeText(getActivity(), "执行S1照明", //.LENGTH_SHORT).show();

                    ((MainActivity) context).doOpt(BluetoothUtils.BULB_ON, 1);
                } else if (mState == FAN &&!fan)  {
                    //.makeText(getActivity(), "执行S1风机", //.LENGTH_SHORT).show();

                    ((MainActivity) context).doOpt(BluetoothUtils.FAN_ON, 1);
                } else if (mState == CLEAN&&!clean ) {
                    //.makeText(getActivity(), "执行S1消毒+"+clean, //.LENGTH_SHORT).show();

                    ((MainActivity) context).doOpt(BluetoothUtils.CLEAN_ON, 1);
                } else if (mState == DRYING&&!dryang) {//烘干开启
                    //.makeText(getActivity(), "执行S1烘干", //.LENGTH_SHORT).show();

                    ((MainActivity) context).doOpt(BluetoothUtils.DRYING_ON, 1);
                }
            }else {

                   // view.setSelected(false);
                    for (int i = 0; i < resBtn.length; i++) {
                        if (view.getId() == resBtn[i]) {
                            status[i] = 0;
                        }
                    }
                    if (mState == UP&&!up) {
                        //.makeText(getActivity(), "停止上升", //.LENGTH_SHORT).show();

                        ((MainActivity) context).doOpt(BluetoothUtils.PAUSE, 1);

                    } else if (mState == DOWN&&!down) {
                        //.makeText(getActivity(), "停止下降", //.LENGTH_SHORT).show();


                        ((MainActivity) context).doOpt(BluetoothUtils.PAUSE, 1);

                    } else if (mState == BULB&&bulb ) {
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
                    } else if (mState == DRYING&&dryang) {//烘干关闭
                        //.makeText(getActivity(), "执行S1烘干关闭", //.LENGTH_SHORT).show();

                        ((MainActivity) context).doOpt(BluetoothUtils.DRYING_OFF, 1);
                        dryang = false;
                    }else if(mState ==STOP){
                        ((MainActivity) context).doOpt(BluetoothUtils.PAUSE, 1);
                        //.makeText(getActivity(), "执行S1的语音停止命令", //.LENGTH_SHORT).show();
                    }
                }
            } else {
                upBtn.setSelected(false);
                downBtn.setSelected(false);
                status[0] = status[1] = 0;
            ((MainActivity) context).doOpt(BluetoothUtils.PAUSE, 1);
            //.makeText(getActivity(), "执行语音S1的停止", //.LENGTH_SHORT).show();


        }
    }


    private void version2(View view) {
        if (view != null && view.getId() != R.id.stop_btn) {
            if (!view.isSelected()) {
                view.setSelected(false);
                for (int i = 0; i < resBtn.length; i++) {
                    if (view.getId() == resBtn[i]) {
                        status[i] = 1;
                    }
                }
                if (mState == UP && up) {
                    ((MainActivity) context).doOpt(BluetoothUtils.UP2, 2);
                    status[1] = 0;
                    ((GestureControlFragment) ((MainActivity) context).getSupportFragmentManager().getFragments().get(0)).flag = false;
                    //.makeText(getActivity(), "执行S2上升", //.LENGTH_SHORT).show();

                } else if (mState == DOWN && down) {
                    ((MainActivity) context).doOpt(BluetoothUtils.DOWN2, 2);
                    status[0] = 0;
                    ((GestureControlFragment) ((MainActivity) context).getSupportFragmentManager().getFragments().get(0)).flag = false;
                    //.makeText(getActivity(), "执行S2降", //.LENGTH_SHORT).show();


                } else if (mState == BULB&&!bulb ) {
                    ((MainActivity) context).doOpt(BluetoothUtils.BULB_ON2, 2);
                    //.makeText(getActivity(), "执行S2照明", //.LENGTH_SHORT).show();

                } else if (mState == FAN&&!fan) {
                    ((MainActivity) context).doOpt(BluetoothUtils.FAN_ON2, 2);
                    //.makeText(getActivity(), "执行S2风机", //.LENGTH_SHORT).show();

                } else if (mState == CLEAN&&!clean) {
                    ((MainActivity) context).doOpt(BluetoothUtils.CLEAN_ON2, 2);
                    //.makeText(getActivity(), "执行S2消毒+"+clean, //.LENGTH_SHORT).show();

                } else if (mState == DRYING&&!dryang) {
                    //.makeText(getActivity(), "执行S2烘干=-----------", //.LENGTH_SHORT).show();

                    ((MainActivity) context).doOpt(BluetoothUtils.DRYING_ON2, 2);
                } else {
                    view.setSelected(false);
                    for (int i = 0; i < resBtn.length; i++) {
                        if (view.getId() == resBtn[i]) {
                            status[i] = 0;
                        }
                    }
                    if (mState == UP && !up) {
                        ((MainActivity) context).doOpt(BluetoothUtils.PAUSE2, 2);
                        //.makeText(getActivity(), "停止上升", //.LENGTH_SHORT).show();

                    } else if (mState == DOWN && !down) {
                        ((MainActivity) context).doOpt(BluetoothUtils.PAUSE2, 2);
                        //.makeText(getActivity(), "停止下降", //.LENGTH_SHORT).show();

                    } else if (mState == BULB&&bulb) {
                        ((MainActivity) context).doOpt(BluetoothUtils.BULB_OFF2, 2);
                        //.makeText(getActivity(), "执行S2关闭照明", //.LENGTH_SHORT).show();
                        bulb=false;

                    } else if (mState == FAN&&fan) {
                        ((MainActivity) context).doOpt(BluetoothUtils.FAN_OFF2, 2);
                        //.makeText(getActivity(), "执行S2风机关闭", //.LENGTH_SHORT).show();
                        fan  = false;

                    } else if (mState == CLEAN&&clean) {
                        ((MainActivity) context).doOpt(BluetoothUtils.CLEAN_OFF2, 2);
                        //.makeText(getActivity(), "执行S2消毒关闭L:"+clean, //.LENGTH_SHORT).show();
                        clean = false;

                    } else if (mState == DRYING&&dryang) {
                        //.makeText(getActivity(), "执行S2烘干关闭", //.LENGTH_SHORT).show();
                        ((MainActivity) context).doOpt(BluetoothUtils.DRYING_OFF2, 2);
                        dryang = false;

                    }else if(mState ==STOP){
                        ((MainActivity) context).doOpt(BluetoothUtils.PAUSE2, 2);
                        //.makeText(getActivity(), "执行S2的语音停止命令", //.LENGTH_SHORT).show();
                    }
                }
            } else {
               // upBtn.setSelected(false);
               // downBtn.setSelected(false);
               // status[0] = status[1] = 0;
               // ((MainActivity) context).doOpt(BluetoothUtils.PAUSE2, 2);
               // //.makeText(getActivity(), "执行语音S2的停止", //.LENGTH_SHORT).show();

            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        boo_version =  SPUtils.getInstance(getActivity()).getBoolean("version",false);
        buttonResumeMethod();
    }

    public void buttonResumeMethod(){
        isPlay =  SPUtils.getInstance(getActivity()).getBoolean("isplay",isPlay);

        if (!isPlay){
            mIFlyTalkHelper.endlistening();
            mVoiceBtn.setSelected(false);
            return;
        }

        mIFlyTalkHelper.startListening();
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(60 * 1000);
                    mIFlyTalkHelper.endlistening();
                    mVoiceBtn.post(new Runnable() {
                        @Override
                        public void run() {
                            mVoiceBtn.setSelected(false);
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                super.run();
            }
        }.start();
        mVoiceBtn.setSelected(true);

    }


    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.fan_honggan://烘干

               // hongganBtn.setSelected(true);
                dryang = !dryang;
                mState  = DRYING;
                break;
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


            case R.id.up_btn:
              //  ((MainActivity) context).doOpt(BluetoothUtils.UP,1);
                upBtn.setSelected(false);
                downBtn.setSelected(false);
                stopBtn.setSelected(false);
                //return;
                //downBtn.setSelected(false);
                mState = UP;
                status[1] = 0;
                status[0] = 1;
                ((GestureControlFragment) ((MainActivity) context).getSupportFragmentManager().getFragments().get(0)).flag = false;
                break;
            case R.id.clean_btn:
                mState = CLEAN;
                break;
            case R.id.stop_btn:

               // ((MainActivity) context).doOpt(BluetoothUtils.PAUSE,1);
                upBtn.setSelected(false);
                downBtn.setSelected(false);
                stopBtn.setSelected(false);

                mState = STOP;
                status[0] = status[1] = 0;
                break;
            //return;
            /*
            mState = STOP;
                ((MainActivity) context).doOpt(BluetoothUtils.PAUSE);
                //((MainActivity) context).doOpt(BluetoothUtils.RETURN_DATA);
                break;
                */
            case R.id.bulb_btn:
                mState = BULB;
                break;
            case R.id.fan_btn:
                mState = FAN;
                break;
            case R.id.down_btn:

                //((MainActivity) context).doOpt(BluetoothUtils.DOWN,1);
                upBtn.setSelected(false);
                downBtn.setSelected(false);
                stopBtn.setSelected(false);

                mState = DOWN;
                status[0] = 0;
                status[1] = 1;
                ((GestureControlFragment) ((MainActivity) context).getSupportFragmentManager().getFragments().get(0)).flag = false;

                break;
            //return;
            /*
                upBtn.setSelected(false);
                mState = DOWN;
                break;
                */
            case R.id.back_btn://音乐
//                ((MainActivity) context).jommoApp.exitApp(context);
            //    ((MainActivity) context).mViewPager.setCurrentItem(0);
                //.makeText(context, "暂不支持此功能", //.LENGTH_SHORT).show();
                return;
            case R.id.power_btn:
                //((MainActivity) context).doOpt(BluetoothUtils.POWER);

                if(boo_version){
                    ((MainActivity) context).doOpt(BluetoothUtils.POWER2,2);
                }else{
                    ((MainActivity) context).doOpt(BluetoothUtils.POWER,1);
                }

                //.makeText(context, "开启/关闭", //.LENGTH_SHORT).show();
                return;
//            case R.id.connect_btn:
//                ((MainActivity) this.getActivity()).gotoBTListActivity();
//                return;
            case R.id.voice_btn:

//                isPlay =  SPUtils.getInstance(getActivity()).getBoolean("isplay",isPlay);

                isPlay = !isPlay;
                SPUtils.getInstance(getActivity()).save("isplay",isPlay);

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
        }

        if(boo_version){
            version22(v);
        }else{
            version11(v);
        }


        // 执行操作

    }

    private void version11(View v) {
        //处理事件的
        if (v.getId() != R.id.stop_btn) {
            if (!v.isSelected()) {
                v.setSelected(true);
                for (int i = 0; i < resBtn.length; i++) {
                    if (v.getId() == resBtn[i]) {
                        status[i] = 1;
                    }
                }
                if (mState == UP) {
                    ((MainActivity) context).doOpt(BluetoothUtils.UP,1);
                    status[1] = 0;
                    //.makeText(context, "上升S1", //.LENGTH_SHORT).show();
                    ((GestureControlFragment) ((MainActivity) context).getSupportFragmentManager().getFragments().get(0)).flag = false;

                } else if (mState == DOWN) {
                    //.makeText(context, "下降S1", //.LENGTH_SHORT).show();

                    ((MainActivity) context).doOpt(BluetoothUtils.DOWN,1);
                    status[0] = 0;
                    ((GestureControlFragment) ((MainActivity) context).getSupportFragmentManager().getFragments().get(0)).flag = false;
                } else if (mState == BULB) {
                    //.makeText(context, "照明S1", //.LENGTH_SHORT).show();
                    ((MainActivity) context).doOpt(BluetoothUtils.BULB_ON,1);
                } else if (mState == FAN) {
                    //.makeText(context, "吹风S1", //.LENGTH_SHORT).show();
                    ((MainActivity) context).doOpt(BluetoothUtils.FAN_ON,1);
                } else if (mState == CLEAN) {
                    //.makeText(context, "消毒S1", //.LENGTH_SHORT).show();
                    ((MainActivity) context).doOpt(BluetoothUtils.CLEAN_ON,1);
                }else if(mState == DRYING){
                    //.makeText(context, "烘干S1", //.LENGTH_SHORT).show();
                    ((MainActivity) context).doOpt(BluetoothUtils.DRYING_ON,1);
                }
            } else {
                v.setSelected(false);
                for (int i = 0; i < resBtn.length; i++) {
                    if (v.getId() == resBtn[i]) {
                        status[i] = 0;
                    }
                }
                if (mState == UP) {
                    //.makeText(context, "停止S1", //.LENGTH_SHORT).show();

                    ((MainActivity) context).doOpt(BluetoothUtils.PAUSE,1);

                } else if (mState == DOWN) {
                    //.makeText(context, "停止S1", //.LENGTH_SHORT).show();

                    ((MainActivity) context).doOpt(BluetoothUtils.PAUSE,1);

                } else if (mState == BULB) {
                    //.makeText(context, "停止照明S1", //.LENGTH_SHORT).show();

                    ((MainActivity) context).doOpt(BluetoothUtils.BULB_OFF,1);
                } else if (mState == FAN) {
                    //.makeText(context, "停止吹风S1", //.LENGTH_SHORT).show();

                    ((MainActivity) context).doOpt(BluetoothUtils.FAN_OFF,1);
                } else if (mState == CLEAN) {
                    //.makeText(context, "停止清理S1", //.LENGTH_SHORT).show();

                    ((MainActivity) context).doOpt(BluetoothUtils.CLEAN_OFF,1);
                }else if(mState == DRYING){
                    //.makeText(context, "停止烘干S1", //.LENGTH_SHORT).show();

                    //.makeText(context, "烘干", //.LENGTH_SHORT).show();
                    ((MainActivity) context).doOpt(BluetoothUtils.DRYING_OFF,1);
                }else if(mState == STOP){
                    ((MainActivity) context).doOpt(BluetoothUtils.PAUSE,1);
                    //.makeText(context, "停止", //.LENGTH_SHORT).show();
                }
            }
        } else {
            upBtn.setSelected(false);
            downBtn.setSelected(false);
            status[0] = status[1] = 0;
            ((MainActivity) context).doOpt(BluetoothUtils.PAUSE,1);
            //.makeText(context, "停止S1", //.LENGTH_SHORT).show();


        }
    }



    private void version22(View v) {
        //处理事件的
        if (v.getId() != R.id.stop_btn) {
            if (!v.isSelected()) {
                v.setSelected(true);
                for (int i = 0; i < resBtn.length; i++) {
                    if (v.getId() == resBtn[i]) {
                        status[i] = 1;
                    }
                }
                if (mState == UP) {
                    //.makeText(context, "上升S2", //.LENGTH_SHORT).show();

                    ((MainActivity) context).doOpt(BluetoothUtils.UP2,2);
                    status[1] = 0;
                    ((GestureControlFragment) ((MainActivity) context).getSupportFragmentManager().getFragments().get(0)).flag = false;
                } else if (mState == DOWN) {
                    //.makeText(context, "下降S2", //.LENGTH_SHORT).show();

                    ((MainActivity) context).doOpt(BluetoothUtils.DOWN2,2);
                    status[0] = 0;
                    ((GestureControlFragment) ((MainActivity) context).getSupportFragmentManager().getFragments().get(0)).flag = false;
                } else if (mState == BULB) {
                    //.makeText(context, "照明S2", //.LENGTH_SHORT).show();

                    ((MainActivity) context).doOpt(BluetoothUtils.BULB_ON2,2);
                } else if (mState == FAN) {
                    //.makeText(context, "吹风S2", //.LENGTH_SHORT).show();

                    ((MainActivity) context).doOpt(BluetoothUtils.FAN_ON2,2);
                } else if (mState == CLEAN) {
                    //.makeText(context, "清理S2", //.LENGTH_SHORT).show();

                    ((MainActivity) context).doOpt(BluetoothUtils.CLEAN_ON2,2);
                }else if(mState == DRYING){
                    //.makeText(context, "烘干S2", //.LENGTH_SHORT).show();

//                    //.makeText(context, "烘干", //.LENGTH_SHORT).show();
                    ((MainActivity) context).doOpt(BluetoothUtils.DRYING_ON2,2);
                }
            } else {
                v.setSelected(false);
                for (int i = 0; i < resBtn.length; i++) {
                    if (v.getId() == resBtn[i]) {
                        status[i] = 0;
                    }
                }
                if (mState == UP) {
                    ((MainActivity) context).doOpt(BluetoothUtils.PAUSE2,2);
                    //.makeText(context, "停止S2", //.LENGTH_SHORT).show();

                } else if (mState == DOWN) {
                    ((MainActivity) context).doOpt(BluetoothUtils.PAUSE2,2);
                    //.makeText(context, "停止S2", //.LENGTH_SHORT).show();

                } else if (mState == BULB) {
                    ((MainActivity) context).doOpt(BluetoothUtils.BULB_OFF2,2);
                    //.makeText(context, "停止照明S2", //.LENGTH_SHORT).show();

                } else if (mState == FAN) {
                    ((MainActivity) context).doOpt(BluetoothUtils.FAN_OFF2,2);
                    //.makeText(context, "停止吹风S2", //.LENGTH_SHORT).show();

                } else if (mState == CLEAN) {
                    ((MainActivity) context).doOpt(BluetoothUtils.CLEAN_OFF2,2);
                    //.makeText(context, "停止消毒S2", //.LENGTH_SHORT).show();

                }else if(mState == DRYING){
                    //.makeText(context, "停止烘干S2", //.LENGTH_SHORT).show();
                    ((MainActivity) context).doOpt(BluetoothUtils.DRYING_OFF2,2);
                }else if(mState ==STOP){
                    //.makeText(context, "停止S2", //.LENGTH_SHORT).show();
                    ((MainActivity) context).doOpt(BluetoothUtils.PAUSE2,2);

                }
            }
        } else {
            upBtn.setSelected(false);
            downBtn.setSelected(false);
            ((MainActivity) context).doOpt(BluetoothUtils.PAUSE2,2);
            //.makeText(context, "停止S2", //.LENGTH_SHORT).show();

            status[0] = status[1] = 0;
        }
    }


    public void refresh() {

        ImageButton[] buttons = {upBtn, downBtn, cleanBtn, bulbBtn, fanBtn};

        for (int i = 0; i < status.length; i++) {
            if (status[i] == 1) {
                buttons[i].setSelected(true);
            } else {
                buttons[i].setSelected(false);
            }
        }
    }

    int[] resBtn = {R.id.up_btn, R.id.down_btn, R.id.clean_btn, R.id.bulb_btn, R.id.fan_btn};
}
