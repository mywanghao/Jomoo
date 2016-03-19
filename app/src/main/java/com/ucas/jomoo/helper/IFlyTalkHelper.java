package com.ucas.jomoo.helper;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.ucas.jomoo.model.IFlyTalkVoiceModel;

/**
 * Created by Rrtoyewx on 16/2/21.
 */
public class IFlyTalkHelper {
    private SpeechRecognizer mIat;
    private StringBuilder mVoiceString = new StringBuilder();
    private IFlyTalkListener mIFlyTalkListener;
    private boolean mIsTalking;
    public static final String CLOSE_POWER = "关闭";
    public static final String UP = "上升";
    public static final String DOWN = "下降";
    public static final String STOP = "停止";
    public static final String OPEN_LIGHTING = "照明";
    public static final String CLOSE_LIGHTING = "停止照明";
    public static final String OPEN_CLEARN = "消毒";
    public static final String CLOSE_CLEAN = "停止消毒";
    public static final String OPEN_FAN = "吹风";
    public static final String CLOSE_FAN = "停止吹风";
    public static final String DRYING_OPEN = "烘干";
    public static final String DRYING_CLOSE = "停止烘干";
//    //电源关闭 11
//    上升 21
//    下降 22
//    暂停 28
//    照明打开 31
//    照明关闭 32
//    风干打开 51
//    风干关闭52
//    消毒打开 61
//    消毒关闭 62
//    烘干打开 71
//    烘干关闭 72


    public static final String[] CONTROLLER_WORDS = {
            "关闭", "上升", "下降", "停止", "照明", "停止照明", "消毒", "停止消毒", "吹风", "停止吹风","烘干","停止烘干",
    };

    private RecognizerListener mRecoListener = new RecognizerListener() {
        public void onResult(RecognizerResult results, boolean isLast) {

            try {
                Gson gson = new Gson();
                IFlyTalkVoiceModel voiceModel = gson.fromJson(results.getResultString(), IFlyTalkVoiceModel.class);

                Log.e("TAG:", results.getResultString());
                for (int i = 0; i < voiceModel.getWs().size(); i++) {
                    mVoiceString.append(voiceModel.getWs().get(i).getCw().get(0).getW());
                }

                if (!mIsTalking) {

                    String voiceString = mVoiceString.toString();
                    if (mVoiceString.length() > 12) {
                        voiceString = mVoiceString.substring(mVoiceString.length() - 10);
                    }

                    if (voiceString != null) {
                        voiceString = checkIfInControllWords(voiceString);
                    }
                    if (mIFlyTalkListener != null) {
                        mIFlyTalkListener.onGetRecognizerWord(mVoiceString.toString());
                    }
                }

                Log.e("TAG", "mVoiceString" + mVoiceString);

            } catch (Exception e) {
                e.printStackTrace();
                onError(new SpeechError(new Exception("无法解析你说的话,请重试")));
            }

        }


        public void onError(SpeechError error) {
            mIsTalking = false;
            error.getPlainDescription(true);
            Log.e("TAG", "onError");
            Log.e("TAG", "onError" + error.getErrorCode());
            Log.e("TAG", "onError" + error.getErrorDescription());
            if (mIFlyTalkListener != null) {
                mIFlyTalkListener.onGetRecognizerWordError(error.getErrorDescription());
            }
        }


        public void onVolumeChanged(int volume) {

        }

        @Override
        public void onBeginOfSpeech() {
            Log.e("TAG", "onBeginOfSpeech");
            mIsTalking = true;
        }


        public void onEndOfSpeech() {
            Log.e("TAG", "onEndOfSpeech");
            mIsTalking = false;
        }


        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {

        }
    };


    public IFlyTalkHelper(Context context) {
        mIat = SpeechRecognizer.createRecognizer(context, null);
        setSpeechRecognizerParams();
    }


    public void setSpeechRecognizerParams() {

        mIat.setParameter(SpeechConstant.DOMAIN, "iat");
        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        mIat.setParameter(SpeechConstant.ACCENT, "mandarin ");
        // mIat.setParameter(SpeechConstant.ASR_NBEST, "3 ");
        mIat.setParameter(SpeechConstant.ASR_PTT, "0");
    }


    public void startListening() {
        startListening(mRecoListener);
    }


    public void startListening(RecognizerListener recoListener) {
        mVoiceString = new StringBuilder();
        mIat.startListening(recoListener);
    }


    public void endlistening() {
        mIat.stopListening();
    }


    private String checkIfInControllWords(String voiceString) {
        Log.e("TAG", "checkIfInControllWords" + voiceString);
        String controllWord = null;
        int position = -1;
        for (int i = 0; i < CONTROLLER_WORDS.length; i++) {
            if (voiceString.contains(CONTROLLER_WORDS[i])) {
                int index = voiceString.lastIndexOf(CONTROLLER_WORDS[i]);
                if (position < index) {
                    controllWord = CONTROLLER_WORDS[i];
                    position = index;
                }
            }
        }
        return controllWord;
    }


    public void setIFlyTalkListener(IFlyTalkListener listener) {
        mIFlyTalkListener = listener;

    }


    public interface IFlyTalkListener {
        void onGetRecognizerWord(String words);

        void onGetRecognizerWordError(String errorWords);
    }

}
