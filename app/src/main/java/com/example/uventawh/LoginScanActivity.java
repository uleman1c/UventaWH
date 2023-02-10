package com.example.uventawh;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONObject;

public class LoginScanActivity extends AppCompatActivity {

//    private InputMethodManager imm;

    private Handler h;

    private boolean shtrihCodeKeyboard = false;

    private ProgressBar progressBar;
    private ImageButton ibSettings, ibKeyboard;

    private EditText etShtrihCode;
    private TextView tvAuth;

    private static final int RECORD_REQUEST_CODE  = 101;
    private static final int STORAGE_REQUEST_CODE = 102;
    private static final int AUDIO_REQUEST_CODE   = 103;

    private MediaProjectionManager projectionManager;
    private MediaProjection mediaProjection;
    private RecordService recordService;
    private Button startBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            projectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        }

        setContentView(R.layout.activity_login_scan);

        String [] permissions = {"android.permission.WRITE_EXTERNAL_STORAGE",
                "android.permission.ACCESS_FINE_LOCATION",
                "android.permission.ACCESS_COARSE_LOCATION",
                "android.permission.READ_PHONE_STATE",
                "android.permission.SYSTEM_ALERT_WINDOW",
                "android.permission.CAMERA",
                "android.permission.RECORD_AUDIO"
        };

        int requestCode = 200;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode);
        }

//        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        etShtrihCode = findViewById(R.id.etShtrihCode);

        ibSettings = findViewById(R.id.ibSettings);

        progressBar = findViewById(R.id.progressBar);
        tvAuth = findViewById(R.id.tvAuth);

        ibKeyboard = findViewById(R.id.ibKeyboard);
        ibKeyboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                etShtrihCode.requestFocus();
//
//                if (shtrihCodeKeyboard){
//
//                    imm.hideSoftInputFromWindow(etShtrihCode.getWindowToken(), 0);
//
//                } else {
//
//                    imm.showSoftInput(etShtrihCode, 0, null);
//                }
//
//                shtrihCodeKeyboard = !shtrihCodeKeyboard;


            }
        });

        etShtrihCode.setOnKeyListener(new View.OnKeyListener()
                                        {
                                            public boolean onKey(View v, int keyCode, KeyEvent event)
                                            {
                                                if(event.getAction() == KeyEvent.ACTION_DOWN &&
                                                        (keyCode == KeyEvent.KEYCODE_ENTER))
                                                {

                                                    String strCatName = etShtrihCode.getText().toString();

//                                                    TakeScreenShot.Do(getBaseContext(), findViewById(R.id.clMain));

                                                    etShtrihCode.setText("");

                                                    shtrihCodeKeyboard = false;

//                                                    imm.hideSoftInputFromWindow(etShtrihCode.getWindowToken(), 0);

                                                    verifyUserIdByPinCode(strCatName);

                                                    return true;
                                                }
                                                return false;
                                            }
                                        }
        );


        ibSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getBaseContext(), SettingsActivity.class);

                intent.putExtra("settings", 1);

                startActivityForResult(intent, 1);

            }
        });


        h = new Handler();
        Thread t = new Thread(new Runnable() {
            public void run() {
                h.post(setFocus);
            }
        });
        t.start();

        etShtrihCode.requestFocus();

        if (ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE")
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] {"android.permission.WRITE_EXTERNAL_STORAGE"}, STORAGE_REQUEST_CODE);
        }

        if (ContextCompat.checkSelfPermission(this, "android.permission.RECORD_AUDIO")
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] {"android.permission.RECORD_AUDIO"}, AUDIO_REQUEST_CODE);
        }

//        Intent intent = new Intent(this, RecordService.class);
//        bindService(intent, connection, BIND_AUTO_CREATE);



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (recordService != null && recordService.isRunning()) {
            recordService.stopRecord();
        }

        try {

            unbindService(connection);

        } catch (Exception e) {

        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RECORD_REQUEST_CODE && resultCode == RESULT_OK) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mediaProjection = projectionManager.getMediaProjection(resultCode, data);
            }
            recordService.setMediaProject(mediaProjection);
            recordService.startRecord();
//            startBtn.setText(R.string.stop_record);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_REQUEST_CODE || requestCode == AUDIO_REQUEST_CODE) {
            if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
               // finish();
            }
        }
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            RecordService.RecordBinder binder = (RecordService.RecordBinder) service;
            recordService = binder.getRecordService();
            recordService.setConfig(metrics.widthPixels, metrics.heightPixels, metrics.densityDpi);


            Intent captureIntent = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                captureIntent = projectionManager.createScreenCaptureIntent();
            }
            startActivityForResult(captureIntent, RECORD_REQUEST_CODE);



//            startBtn.setEnabled(true);
//            startBtn.setText(recordService.isRunning() ? R.string.stop_record : R.string.start_record);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {}
    };

    final Runnable setFocus = new Runnable() {
        public void run() {

            if (etShtrihCode.isFocused() && !shtrihCodeKeyboard) {

//                imm.hideSoftInputFromWindow(etShtrihCode.getWindowToken(), 0);

            }

            h.postDelayed(setFocus, 500);


        }
    };

    protected void verifyUserIdByPinCode(final String pinCodeStr) {

        final HttpClient client1 = new HttpClient(this);
        client1.addParam("pinCode", pinCodeStr.equals("123456") ? "1" : (pinCodeStr.equals("1111111") ? "123456" : pinCodeStr));

        client1.postProc("authorization", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {

                progressBar.setVisibility(visibility);

                etShtrihCode.setEnabled(visibility == View.GONE);
                ibKeyboard.setEnabled(visibility == View.GONE);
                ibSettings.setEnabled(visibility == View.GONE);

            }

            @Override
            public void processResponse(JSONObject response) {

                JSONObject authorizationJSON = client1.getJsonObjectFromJsonObject(response, "authorization");

                if (client1.getBooleanFromJSON(authorizationJSON, "Success")) {

                    etShtrihCode.setVisibility(View.GONE);
                    ibKeyboard.setVisibility(View.GONE);
                    ibSettings.setVisibility(View.GONE);

                    progressBar.setVisibility(View.VISIBLE);

                    tvAuth.setText("Загрузка...");

                    DB db = new DB(getBaseContext());

                    db.open();

                    String user_description = client1.getStringFromJSON(authorizationJSON, "Description");

                    db.updateConstant("user_id", client1.getStringFromJSON(authorizationJSON, "Ref"));
                    db.updateConstant("user_description", user_description);

                    db.updateConstant("screenShots", client1.getBooleanFromJSON(authorizationJSON, "ScreenShots") ? "true" : "false");
                    db.updateConstant("exchanging", "false");

                    db.close();

                    Intent mainIntent = new Intent(getBaseContext(), MainWareHouseActivity.class);
                    mainIntent.putExtra("user_description", user_description);

                    startActivity(mainIntent);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            etShtrihCode.setVisibility(View.VISIBLE);
                            ibKeyboard.setVisibility(View.VISIBLE);
                            ibSettings.setVisibility(View.VISIBLE);

                            progressBar.setVisibility(View.GONE);

                            tvAuth.setText("Авторизация");

                            etShtrihCode.requestFocus();

                        }
                    }, 15000);

                }
                else {

                    Toast toast = Toast.makeText(getBaseContext(), "Пинкод не найден", Toast.LENGTH_SHORT);
                    toast.show();
                }

            }
        });

    }



}
