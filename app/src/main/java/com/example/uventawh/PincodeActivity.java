package com.example.uventawh;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.util.UUID;

public class PincodeActivity extends AppCompatActivity {

    private LinearLayout linearLayout;
    private ProgressBar progressBar;
    private TextView tvPincode;

    private boolean debug = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pincode);

        String [] permissions = {"android.permission.WRITE_EXTERNAL_STORAGE",
                "android.permission.ACCESS_FINE_LOCATION",
                "android.permission.ACCESS_COARSE_LOCATION",
                "android.permission.READ_PHONE_STATE",
                "android.permission.SYSTEM_ALERT_WINDOW",
                "android.permission.CAMERA"};

        int requestCode = 200;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode);
        }

        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        tvPincode = (TextView) findViewById(R.id.tvPincode);

        Button.OnClickListener btnOCL = new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                setFilter((String) ((Button) v).getText());
            }
        };

        ((Button) findViewById(R.id.btn0)).setOnClickListener(btnOCL);
        ((Button) findViewById(R.id.btn1)).setOnClickListener(btnOCL);
        ((Button) findViewById(R.id.btn2)).setOnClickListener(btnOCL);
        ((Button) findViewById(R.id.btn3)).setOnClickListener(btnOCL);
        ((Button) findViewById(R.id.btn4)).setOnClickListener(btnOCL);
        ((Button) findViewById(R.id.btn5)).setOnClickListener(btnOCL);
        ((Button) findViewById(R.id.btn6)).setOnClickListener(btnOCL);
        ((Button) findViewById(R.id.btn7)).setOnClickListener(btnOCL);
        ((Button) findViewById(R.id.btn8)).setOnClickListener(btnOCL);
        ((Button) findViewById(R.id.btn9)).setOnClickListener(btnOCL);
        ((Button) findViewById(R.id.btnBS)).setOnClickListener(btnOCL);
        ((Button) findViewById(R.id.btnEnter)).setOnClickListener(btnOCL);

        ImageButton ibSettings = findViewById(R.id.ibSettings);
        ibSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getBaseContext(), SettingsActivity.class);

                intent.putExtra("settings", 1);

                startActivityForResult(intent, 1);

            }
        });

        ibSettings.setVisibility(debug ? View.VISIBLE : View.GONE);

        DB db = new DB(this);
        db.open();

        String prog_id = db.getConstant("prog_id");
        if (prog_id == null) {

            prog_id = UUID.randomUUID().toString();
            db.updateConstant("prog_id", prog_id);

        }

        if (!debug){

            db.updateConstant("test", "false");

        }

        db.close();



    }

    private void setFilter(String s){

        String curText = (String) tvPincode.getText();

        if(s.equals("<")){

            if(curText.length() > 0){

                curText = curText.substring(0, curText.length()-1);
            }

        }
        else if(s.equals("OK")){

            if(!curText.isEmpty()) {

                   verifyUserIdByPinCode(curText);
            }
        }
        else{
            curText = curText + s;
        }

        tvPincode.setText(curText);



    }


    protected void verifyUserIdByPinCode(final String pinCodeStr) {

        final HttpClient client1 = new HttpClient(this);
        client1.addParam("pinCode", pinCodeStr);

        client1.postProc("authorization", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {
                progressBar.setVisibility(visibility);
            }

            @Override
            public void processResponse(JSONObject response) {

                JSONObject authorizationJSON = client1.getJsonObjectFromJsonObject(response, "authorization");

                if (client1.getBooleanFromJSON(authorizationJSON, "Success")) {

                    DB db = new DB(PincodeActivity.this);

                    db.open();

                    db.updateConstant("user_id", client1.getStringFromJSON(authorizationJSON, "Ref"));
                    db.updateConstant("user_description", client1.getStringFromJSON(authorizationJSON, "Description"));

                    db.close();

                    Intent mainIntent = new Intent(PincodeActivity.this, MainActivity.class);
                    PincodeActivity.this.startActivity(mainIntent);
                    PincodeActivity.this.finish();

                }
                else {

                    Toast toast = Toast.makeText(PincodeActivity.this, "Пинкод не найден", Toast.LENGTH_SHORT);
                    toast.show();
                }

            }
        });

    }


}

