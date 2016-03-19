package com.ucas.jomoo.ui;

import android.bluetooth.BluetoothDevice;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ucas.jomoo.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by hao on 2016/3/1.
 */
public class BTListAdapter extends BaseAdapter {

    ArrayList<HashMap<String, Object>> listitem;



    public BTListAdapter( ArrayList<HashMap<String, Object>> listitem){
        this.listitem=listitem;
    }

//    @Override
//    public int getCount() {
//        return 2;
//    }


    @Override
    public int getCount() {
        return listitem.size();
    }


    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView==null){
            BluetoothDevice device = (BluetoothDevice) listitem.get(position).get("device");
            convertView=View.inflate(parent.getContext(), R.layout.layout_btlist_item,null);
            ((TextView)convertView.findViewById(R.id.devices_name)).setText( device.getName() );

        }

        return convertView;
    }


    public void setConnectState(int position, View convertView, ViewGroup parent, String state){

        ((TextView) getView(position,convertView,parent).findViewById(R.id.connect_state)).setText(state);

    }

}
