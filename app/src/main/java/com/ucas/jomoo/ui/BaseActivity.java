package com.ucas.jomoo.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.ucas.jomoo.JommoApp;

/**
 * Created by ivanchou on 7/29/15.
 */
public class BaseActivity extends ActionBarActivity {

    protected JommoApp app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (JommoApp) getApplication();
        app.addActivity(this);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        app.exitApp(this);
    }
}
