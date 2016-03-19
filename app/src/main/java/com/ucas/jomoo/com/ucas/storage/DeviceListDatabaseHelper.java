package com.ucas.jomoo.com.ucas.storage;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ucas.jomoo.com.ucas.pojo.RemoteDeviceModel;

/**
 * Created by Dalink on 15/10/26.
 */
public class DeviceListDatabaseHelper extends SQLiteOpenHelper {

    public final static String DEVICE_LIST_TABLE_NAME = "devicelist";

    public DeviceListDatabaseHelper(Context context) {
        super(context, "device_list", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists " + DEVICE_LIST_TABLE_NAME + " (deviceAddress varchar(80) primary key, deviceName varchar(20), password varchar(4))");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //根据一个地址删除一个设备
    public boolean insertDevice(String address, String name, String password) {
        boolean result = false;

        if (address != null && name != null && password != null && !this.isDeviceExists(address)) {
            SQLiteDatabase db = this.getWritableDatabase();
            this.getWritableDatabase().execSQL("insert into " + DEVICE_LIST_TABLE_NAME + " values (?, ?, ?)", new String[]{address, name, password});
            db.close();

            result = true;
        }
        return result;
    }
    //根据地址获取一个设备
    public RemoteDeviceModel getDevice(String address) {
        RemoteDeviceModel model = null;
        if (address != null && this.isDeviceExists(address)) {
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery("select * from " + DEVICE_LIST_TABLE_NAME + " where deviceAddress == ?", new String[]{address});
            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                model = new RemoteDeviceModel();
                model.address = address;
                model.name = cursor.getString(cursor.getColumnIndex("deviceName"));
                model.password = cursor.getString(cursor.getColumnIndex("password"));
            }
            cursor.close();
            db.close();
        }
        return model;
    }

    //根据地址修改密码
    public void modifyPassword(String address, String newPassword) {
        RemoteDeviceModel model = null;
        if (address != null && newPassword != null && this.isDeviceExists(address)) {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("update " + DEVICE_LIST_TABLE_NAME + " set 'password' = ?  where deviceAddress == ?", new String[]{newPassword,address});
        }
    }

    //根据一个地址删除一个设备
    public boolean deleteDevice(String address) {
        boolean result = false;

        if (address != null && this.isDeviceExists(address)) {
            SQLiteDatabase db = this.getWritableDatabase();
            this.getWritableDatabase().execSQL("delete from " + DEVICE_LIST_TABLE_NAME + " where deviceAddress == ?", new String[]{address});
            db.close();
        }
        return result;
    }

    //该设备是否存在于记录中
    public boolean isDeviceExists(String address) {
        if (address == null) {
            return false;
        }
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select count(*) from " + DEVICE_LIST_TABLE_NAME + " where deviceAddress = ?", new String[]{address});
        cursor.moveToFirst();
        // 获取数据中的LONG类型数据
        long count = cursor.getLong(0);

        cursor.close();
        db.close();

        return count == 1;
    }
}
