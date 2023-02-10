package com.example.uventawh;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;

import org.json.JSONObject;

public class SettingsActivity extends Activity {

//    CheckBox cbTest;
//    RadioButton radioButton;
    EditText etServerAddress, etBaseName, etUser, etPwd;
//    Switch swUpdateScan;
    DB db;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

//        cbTest = findViewById(R.id.cbTest);

//        radioButton = findViewById(R.id.rbTest);
        etServerAddress = findViewById(R.id.etServerAddress);
        etBaseName = findViewById(R.id.etBaseName);
        etUser = findViewById(R.id.etUser);
        etPwd = findViewById(R.id.etPwd);
//        swUpdateScan = findViewById(R.id.swUpdateScan);

        db = new DB(this);
        db.open();

        String isTest = db.getConstant("test");
//        if (isTest == null || isTest.equals("true")) {
//
////            radioButton.setChecked(true);
//
//        }
//        else ((RadioButton)findViewById(R.id.rbWork)).setChecked(true);

        String serverAddress = db.getConstant("server_address");
        if (serverAddress == null) {

            serverAddress = "https://transtechn.ru";

        }

        String base_name = db.getConstant("base_name");
        if (base_name == null) {

            base_name = "knit_wms";

        }

        String user = db.getConstant("user");
        if (user == null) {

            user = "exch";

        }

        String pwd = db.getConstant("pwd");
        if (pwd == null) {

            pwd = "123456";

        }

        String isUpdateScan = db.getConstant("update_scan");
        if (isUpdateScan != null && isUpdateScan.equals("true")) {

//            swUpdateScan.setChecked(true);

        }

        etServerAddress.setText(serverAddress);
        etBaseName.setText(base_name);
        etUser.setText(user);
        etPwd.setText(pwd);

        ((Button) findViewById(R.id.btnSave)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                db.updateConstant("test", "false");

                db.updateConstant("server_address", etServerAddress.getText().toString());
                db.updateConstant("base_name", etBaseName.getText().toString());
                db.updateConstant("user", etUser.getText().toString());
                db.updateConstant("pwd", etPwd.getText().toString());
                db.updateConstant("update_scan", "false");

                String url = "request/" + db.getConstant("user_id") + "/" + db.getConstant("prog_id");

                HttpClient httpClient = new HttpClient(getBaseContext());
                httpClient.getProc(getBaseContext(), url, new HttpRequestInterface() {
                    @Override
                    public void setProgressVisibility(int visibility) {

                    }

                    @Override
                    public void processResponse(JSONObject response) {

                        setResult(RESULT_OK);

                        finish();

                    }
                });


            }
        });

    }
}
