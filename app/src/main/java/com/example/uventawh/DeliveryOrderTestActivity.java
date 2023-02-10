package com.example.uventawh;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class DeliveryOrderTestActivity extends AppCompatActivity {

    String ref, name, number, date, refContractor, company, refSender, sender, curShtrihCode, currentPhotoPath;
    Integer image;

    EditText editText;
    TextView error, filter;

    List<String> toScanStart = new ArrayList<>();
    List<String> toScan = new ArrayList<>();
    List<ScannedShtrihCode> scanned = new ArrayList<>();
    DataStringAdapter adapterToScan;
    ScannedShtrihCodeAdapter adapterScanned;

    ProgressBar progressBar;
    LinearLayout llMain;

    private Timer timer=new Timer();
    private final long DELAY = 1000; // milliseconds

    int cnt;
    final int max = 100;
    Handler h;

    Boolean manualInput = false, sendingInProgress = false;

    Uri outputFileUri;

    private static final int CAMERA_REQUEST = 20;

    private boolean shtrihCodeKeyboard = false;

    private InputMethodManager imm;

    DelayAutoCompleteTextView actvShtrihCode;

    private SoundPlayer soundPlayer;

    ProgressTextView progressTextView;

    Boolean inputSenderAddress, inputDelivererAddress, inputQuantity;

    androidx.appcompat.app.AlertDialog quantityDialog = null;

    EditText etQuantity;

    Double quantity = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_shipment_test);

        progressTextView = findViewById(R.id.scannedText);

        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        soundPlayer = new SoundPlayer(this, R.raw.hrn05);
        this.setVolumeControlStream(soundPlayer.streamType);

        h = new Handler();

        Intent intent = getIntent();
        ref = intent.getStringExtra("ref");
        name = intent.getStringExtra("name");
        number = intent.getStringExtra("number");
        date = intent.getStringExtra("date");
        refContractor = intent.getStringExtra("refContractor");
        company = intent.getStringExtra("company");
        refSender = intent.getStringExtra("refSender");
        sender = intent.getStringExtra("sender");
        image = (int) intent.getLongExtra("image", 0);

        inputSenderAddress = intent.getBooleanExtra("inputSenderAddress", false);
        inputDelivererAddress = intent.getBooleanExtra("inputDelivererAddress", false);
        inputQuantity = intent.getBooleanExtra("inputQuantity", false);

        progressBar = findViewById(R.id.progressBar);
        llMain = findViewById(R.id.llMain);

        actvShtrihCode = findViewById(R.id.actvShtrihCode);
        actvShtrihCode.setThreshold(3);

        actvShtrihCode.setAdapter(new StringAutoCompleteAdapter(getBaseContext(), toScan));
        actvShtrihCode.setLoadingIndicator((ProgressBar) findViewById(R.id.progress_bar0));

        actvShtrihCode.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                String strCatName = (String) adapterView.getItemAtPosition(position);

                actvShtrihCode.setText("");

                shtrihCodeKeyboard = false;

                imm.hideSoftInputFromWindow(actvShtrihCode.getWindowToken(), 0);

                scanShtrihCode(strCatName);

            }
        });

        actvShtrihCode.requestFocus();
        imm.hideSoftInputFromWindow(actvShtrihCode.getWindowToken(), 0);

        actvShtrihCode.setOnKeyListener(new View.OnKeyListener()
                                        {
                                            public boolean onKey(View v, int keyCode, KeyEvent event)
                                            {
            if(event.getAction() == KeyEvent.ACTION_DOWN &&
                    (keyCode == KeyEvent.KEYCODE_ENTER))
            {

                String strCatName = actvShtrihCode.getText().toString();

                actvShtrihCode.setText("");

                shtrihCodeKeyboard = false;

                imm.hideSoftInputFromWindow(actvShtrihCode.getWindowToken(), 0);

                scanShtrihCode(strCatName);

                return true;
            }
            return false;
        }
    }
        );





        ((TextView) findViewById(R.id.header)).setText(name + " №" + number + " от " + date + " " + company + ", " + sender);

        error = (TextView) findViewById(R.id.error);

        Thread t = new Thread(new Runnable() {
            public void run() {
                h.post(setFocus);
            }
        });
        t.start();


        adapterToScan = new DataStringAdapter(this, toScan);

        adapterToScan.setOnStringClickListener(new DataStringAdapter.OnStringClickListener(){
            @Override
            public void onStringClick(String str, View itemView) {

//                if (manualInput) {
//
//                    scanShtrihCode(str);
//
//                    filter.setText("");
//
//                    setFilter("");
//
//                    manualInput = false;
//                }
            }
        });



        RecyclerView recyclerViewToScan = findViewById(R.id.toScan);
        recyclerViewToScan.setAdapter(adapterToScan);

        RecyclerView recyclerViewScanned = findViewById(R.id.scanned);
        adapterScanned = new ScannedShtrihCodeAdapter(this, scanned);
        recyclerViewScanned.setAdapter(adapterScanned);

        adapterScanned.setOnStringClickListener(new ScannedShtrihCodeAdapter.OnStringClickListener(){
            @Override
            public void onStringClick(ScannedShtrihCode str, View itemView) {

//                askForRepeatCode(str);

            }
        });

        findViewById(R.id.ibKeyboard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                actvShtrihCode.requestFocus();

                if (shtrihCodeKeyboard){

                    imm.hideSoftInputFromWindow(actvShtrihCode.getWindowToken(), 0);

                } else {

                    imm.showSoftInput(actvShtrihCode, 0, null);
                }

                shtrihCodeKeyboard = !shtrihCodeKeyboard;


            }
        });

        getShtrihs();

    }

    protected void getShtrihs(){

        final HttpClient httpClient = new HttpClient(this);
        httpClient.addParam("refDeliveryOrder", ref);

        httpClient.postProc("getDeliveryOrderShtrihs", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {
                progressBar.setVisibility(visibility);
            }

            @Override
            public void processResponse(JSONObject response) {

                toScan.clear();
                scanned.clear();

                JSONArray routeShtrihs = httpClient.getJsonArrayFromJsonObject(response, "DeliveryOrderShtrihs");

                Integer iAdded = 0;

                for (int j = 0; j < routeShtrihs.length(); j++) {

                    JSONObject accept_item = httpClient.getItemJSONArray(routeShtrihs, j);

                    Boolean tested = httpClient.getBooleanFromJSON(accept_item, "Tested");
                    Boolean added = httpClient.getBooleanFromJSON(accept_item, "Added");
                    String date = httpClient.getStringFromJSON(accept_item, "Date");
                    String shtrih_code = httpClient.getStringFromJSON(accept_item, "ShtrihCode");
                    Double quantity = httpClient.getDoubleFromJSON(accept_item, "Quantity");

                    if (tested){

                        scanned.add(new ScannedShtrihCode(shtrih_code, date, added, quantity));
                    } else {

                        toScan.add(shtrih_code);
                    }

                    iAdded = iAdded + (added ? 1 : 0);

                }

                Integer scannedQ = scanned.size();
                Integer toAcceptQ = toScan.size() + scannedQ;

                Integer procent = toAcceptQ == 0 ? 100 : scannedQ * 100 / toAcceptQ;

                progressTextView.setText(scannedQ.toString() + " из " + toAcceptQ.toString() + ", " + procent.toString() + "%");
                progressTextView.setMax(toAcceptQ);
                progressTextView.setProgress(scannedQ);

                adapterToScan.notifyDataSetChanged();
                adapterScanned.notifyDataSetChanged();




            }
        });

    }

    private void scanShtrihCode(String strCatName) {

        if (!sendingInProgress){

            Integer index = toScan.indexOf(strCatName);

            if (index < 0){

                error.setText("Не найден штрихкод " + strCatName);

                soundPlayer.play();

                Boolean present = false;
                for (ScannedShtrihCode scannedShtrihCode:scanned){
                    present = scannedShtrihCode.shtrihCode.equals(strCatName);
                    if (present) break;
                }

                if (present){

                    askForRepeatCode(strCatName);

                } else {

                    askForNotFoundCode(strCatName);

                }


            }
            else {
                error.setText("");

                toScan.remove(strCatName);

                scanned.add(0, new ScannedShtrihCode(strCatName, "", false));
                adapterScanned.notifyDataSetChanged();

                setShtrihs(strCatName);
            }
        }
    }

    private void askForNotFoundCode(final String strShtrihCode) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(strShtrihCode + " не найден. Выбрать");

        alertDialogBuilder.setIcon(R.drawable.sklad96);

        LayoutInflater inflater = this.getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_ask_not_found_shtrihcode, null);

        alertDialogBuilder.setView(view);
        alertDialogBuilder.setIcon(R.drawable.sklad96);

        final AlertDialog alertDialog = alertDialogBuilder.create();

        view.findViewById(R.id.btnAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setShtrihs(strShtrihCode);

                alertDialog.cancel();


            }
        });

        view.findViewById(R.id.btnIgnore).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.cancel();

            }
        });

        alertDialog.show();

    }

    private void askForRepeatCode(final String strShtrihCode) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(strShtrihCode + " уже есть. Выбрать");

        alertDialogBuilder.setIcon(R.drawable.sklad96);

        LayoutInflater inflater = this.getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_ask_repeat_shtrihcode, null);

        alertDialogBuilder.setView(view);
        alertDialogBuilder.setIcon(R.drawable.sklad96);

        final AlertDialog alertDialog = alertDialogBuilder.create();

        view.findViewById(R.id.btnAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setShtrihs(strShtrihCode);

                alertDialog.cancel();


            }
        });

        view.findViewById(R.id.btnIgnore).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.cancel();

            }
        });

        alertDialog.show();


    }



    final Runnable setFocus = new Runnable() {
        public void run() {

            if (actvShtrihCode.isFocused() && !shtrihCodeKeyboard) {

                imm.hideSoftInputFromWindow(actvShtrihCode.getWindowToken(), 0);

            }

            h.postDelayed(setFocus, 500);

        }
    };

    protected void setShtrihs(final String shtrihcode){

        final HttpClient httpClient = new HttpClient(this);
        httpClient.addParam("refDeliveryOrder", ref);
        httpClient.addParam("shtrihcode", shtrihcode);

        httpClient.postProc("setDeliveryOrderShtrihCode", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {
                progressBar.setVisibility(visibility);
            }

            @Override
            public void processResponse(JSONObject response) {

                if(httpClient.getBooleanFromJSON(response, "Success")){

                    if (inputQuantity && httpClient.getBooleanFromJSON(response, "IsNew")){

                        inputQuantity(shtrihcode);

                    }
                    else {
                        getShtrihs();
                    }

                }
            }
        });

    }

    private void inputQuantity(final String strCatName) {

        androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Ввод количества");

        LayoutInflater inflater = getLayoutInflater();

        final View view = inflater.inflate(R.layout.dialog_input_quantity, null);

        ((TextView) view.findViewById(R.id.tvCode)).setText(strCatName);

        Button btnQuantity = view.findViewById(R.id.btnQuantity);

        etQuantity = view.findViewById(R.id.etQuantity);

        if(quantity == null){

            btnQuantity.setVisibility(View.GONE);
        }
        else {

            btnQuantity.setVisibility(View.VISIBLE);
            btnQuantity.setText("<<  " + quantity.toString());
        }

        btnQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                etQuantity.setText(quantity.toString());

                CloseScan(view, strCatName);

            }
        });

        alertDialogBuilder.setView(view);
        alertDialogBuilder.setIcon(R.drawable.sklad96);

        quantityDialog = alertDialogBuilder.create();

        view.findViewById(R.id.btnOK).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CloseScan(view, strCatName);

            }
        });

        quantityDialog.show();

        final Handler h2 = new Handler();
        h2.postDelayed(setFocus2, 500);

    }

    final Runnable setFocus2 = new Runnable() {
        public void run() {

            InputMethodManager imm = (InputMethodManager) quantityDialog.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

            etQuantity.requestFocus();

            imm.showSoftInput(etQuantity, 0, null);


        }
    };

    private void CloseScan(View view, String strCatName) {
        quantityDialog.cancel();

        quantity = Double.valueOf(((TextView) view.findViewById(R.id.etQuantity)).getText().toString());

        scanShtrihCode(strCatName, quantity);
    }

    private void scanShtrihCode(String strCatName, Double quantity) {

        if (!sendingInProgress){

            scanned.add(0, new ScannedShtrihCode(strCatName, "", false, quantity));
            adapterScanned.notifyDataSetChanged();

            setQuantityToShtrih(strCatName, quantity);

        }
    }

    private void setQuantityToShtrih(String shtrihcode, Double quantity) {

        final HttpClient httpClient = new HttpClient(this);
        httpClient.addParam("contractorRef", refContractor);
        httpClient.addParam("shtrihCode", shtrihcode);
        httpClient.addParam("quantity", quantity);

        httpClient.postForResult("setQuantityToShtrih", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {
                progressBar.setVisibility(visibility);
            }

            @Override
            public void processResponse(JSONObject response) {

                if (httpClient.getBooleanFromJSON(response, "Success")){

                    getShtrihs();

                };

            }
        });



    }


}

