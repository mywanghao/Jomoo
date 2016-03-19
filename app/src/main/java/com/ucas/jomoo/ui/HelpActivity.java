package com.ucas.jomoo.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.ucas.jomoo.R;

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_help_text);

         findViewById(R.id.nav_close).setVisibility(View.GONE);
//         findViewById(R.id.btn_setter).setOnClickListener(new View.OnClickListener() {
//             @Override
//             public void onClick(View v) {
//                 startActivity(new Intent(HelpActivity.this,BTListActivity.class));
//                 overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//                 finish();
//             }
//         });

    }
}
