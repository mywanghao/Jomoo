package com.ucas.jomoo.tools;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * Created by ivanchou on 7/29/15.
 */
public class BluetoothUtils {
    /**
     * 1.0
     */
    public static int POWER = 11;           //开关机
    public static int UP = 21;              //上升
    public static int DOWN = 22;            //下降
    public static int PAUSE = 28;           //暂停
    public static int BULB_ON = 31;         //照明开
    public static int BULB_OFF = 32;        //照明关
    public static int FAN_ON = 51;          //风机开
    public static int FAN_OFF = 52;         //风机关
    public static int CLEAN_ON = 61;        //消毒开
    public static int CLEAN_OFF = 62;       //消毒关
    public static int RETURN_DATA = 99;     //
    public static int DRYING_ON = 71;   //烘干
    public static int DRYING_OFF = 72;  //停止烘干


    /**
     * 2.0
     */
    public static int POWER2 = 0x0001;           //开关机
    public static int UP2 = 0x0002;              //上升
    public static int DOWN2 = 0x0004;            //下降
    public static int PAUSE2 = 0x0008;           //暂停
    public static int BULB_ON2 = 0x0010;         //照明开
    public static int BULB_OFF2 = 0x0020;        //照明关
 //   public static int FAN_ON2 = 0x0100;          //风机开
 //   public static int FAN_OFF2 = 0x0200;         //风机关
    public static int FAN_ON2 = 0x000a;          //风机开
    public static int FAN_OFF2 = 0x000b;         //风机关
    public static int CLEAN_ON2 = 0x0040;        //消毒开
    public static int CLEAN_OFF2 = 0x0080;       //消毒关
    public static int RETURN_DATA2 = 99;     //
//    public static int DRYING_ON2 = 0x0400;   //烘干
//    public static int DRYING_OFF2 = 0x0800;  //停止烘干
    public static int DRYING_ON2 = 0x000c;   //烘干
    public static int DRYING_OFF2 = 0x000d;  //停止烘干


    public static int MODIFY_PWD = Integer.MAX_VALUE;   //修改密码

    private BluetoothUtils() {

    }

    public static void power() {
    }

    public static void up() {

    }

    public static void down() {

    }

    public static void pause() {

    }

    public static void bulbOn() {
    }

    public static void bulbOff() {
    }

    public static void fanOn() {

    }

    public static void fanOff() {

    }

    public static void cleanOn() {

    }

    public static void cleanOff() {

    }

    public static byte[] intobyte(int i, String password, String oldPassword,int order) {
        byte[] result;
        //修改密码
        if (BluetoothUtils.MODIFY_PWD == i) {

            byte[] bytes = new byte[6];
            bytes[0] = ((Integer)0xa1).byteValue();         //固定
            bytes[1] = BluetoothUtils.bytesBy2Decimal(oldPassword.substring(0, 2));//密码的前两位
            bytes[2] = BluetoothUtils.bytesBy2Decimal(oldPassword.substring(2, 4));//密码的后两位
            bytes[3] = BluetoothUtils.bytesBy2Decimal(password.substring(0, 2));//密码的前两位
            bytes[4] = BluetoothUtils.bytesBy2Decimal(password.substring(2, 4));//密码的后两位
            bytes[5] = (byte)((bytes[0]) ^ (bytes[1]) ^ (bytes[2]) ^ (bytes[3]) ^ (bytes[4]));

            result = bytes;
        } else {
            byte[] bytes =  null ;
            if(order==1) {//1.0
            bytes = new byte[5];
            bytes[0] = ((Integer)0xa2).byteValue();         //固定
            bytes[1] = BluetoothUtils.bytesBy2Decimal(password.substring(0, 2));//密码的前两位
            bytes[2] = BluetoothUtils.bytesBy2Decimal(password.substring(2, 4));//密码的后两位
            bytes[3] = (byte)i;//11 2.0
            bytes[4] = (byte)((bytes[0]) ^ (bytes[1]) ^ (bytes[2]) ^ (bytes[3]));

            }else {//2.0
                 bytes = new byte[6];
                bytes[0] = ((Integer) 0xa2).byteValue();         //固定
                bytes[1] = BluetoothUtils.bytesBy2Decimal(password.substring(0, 2));//密码的前两位
                bytes[2] = BluetoothUtils.bytesBy2Decimal(password.substring(2, 4));//密码的后两位
              //  bytes[3] = ((Integer) (i== 0BluetoothUtils.DRYING_ON2? 0x0: i==BluetoothUtils.DRYING_OFF2 ? 0x0: i==BluetoothUtils.FAN_OFF2? 0x0: i==BluetoothUtils.FAN_ON2?  0x0:0x00)).byteValue();
                bytes[3] =((Integer)0x00).byteValue();
                ;//0x00
                bytes[4] = (byte) i;//0x0001
                bytes[5] = (byte) ((bytes[0]) ^ (bytes[1]) ^ (bytes[2]) ^ (bytes[3]) ^ (bytes[4]));

            }
            result = bytes;

        }
        return result;
    }


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    //存储最近一次使用的设备
    public static void storeLastAddress(Context context, String address) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("jomoo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
        editor.putString("last_address", address);
        editor.commit();//提交修改
    }

    //读取最近一次使用的设备
    public static String readLastAddress(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("jomoo", Context.MODE_PRIVATE);
        return sharedPreferences.getString("last_address", "");
    }
    //数字分字节展示
    public static byte bytesBy2Decimal(String string) {

        String firstString = string.substring(0, 1);
        String secondString = string.substring(1, 2);
        int firstNum = Integer.parseInt(firstString);
        int secondNum = Integer.parseInt(secondString);

        byte result = ((Integer)(firstNum * 16 + secondNum)).byteValue();

        return result;
    }
}
