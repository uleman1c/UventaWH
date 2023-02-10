package com.example.uventawh;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

public class AddTransportActivity extends AppCompatActivity {

    DelayAutoCompleteTextView actvNumber, actvFIO;
    String refTransport, descTransport = "", refDriver, descDriver = "";

    EditText et1,et2, et3, et4, et5, et6, et7, et8, et9;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transport);

        DB db = new DB(this);
        refTransport = db.emptyRef;
        refDriver = db.emptyRef;

        String url = db.getRequestUserProg();

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        actvNumber = findViewById(R.id.actvNumber);
        actvNumber.setThreshold(3);

        actvNumber.setAdapter(new RefDescAutoCompleteAdapter(this, url, new HttpClientSync(this)));
        actvNumber.setLoadingIndicator((ProgressBar) findViewById(R.id.progress_bar));

        actvNumber.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                RefDesc refDesc = (RefDesc) adapterView.getItemAtPosition(position);

                refTransport = refDesc.ref;
                descTransport = refDesc.desc;

                actvNumber.setText(descTransport);
            }
        });

        actvFIO = findViewById(R.id.actvFIO);
        actvFIO.setThreshold(3);

        actvFIO.setAdapter(new DriverAutoCompleteAdapter(this, url, new HttpClientSync(this)));
        actvFIO.setLoadingIndicator((ProgressBar) findViewById(R.id.progress_barFio));

        actvFIO.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                RefDesc refDesc = (RefDesc) adapterView.getItemAtPosition(position);

                refDriver = refDesc.ref;
                descDriver = refDesc.desc;

                actvFIO.setText(descDriver);
            }
        });

        findViewById(R.id.fabOk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Boolean validated = true;

                if(et1.getText().length() == 0 || et2.getText().length() == 0
                        || et3.getText().length() == 0 || et4.getText().length() == 0
                        || et5.getText().length() == 0 || et6.getText().length() == 0
                        || et7.getText().length() == 0 || et8.getText().length() == 0
                ){

                    validated = false;

                    Toast toast = Toast.makeText(AddTransportActivity.this, "Не заполнен автомобиль", Toast.LENGTH_SHORT);
                    toast.show();


                }

                if(actvFIO.getText().length() < 6){

                    validated = false;

                    Toast toast = Toast.makeText(AddTransportActivity.this, "Не заполнен водитель", Toast.LENGTH_SHORT);
                    toast.show();


                }

                if (validated){

                    addTransport();
                    
                }

            }
        });

        et1 = findViewById(R.id.et1);
        et2 = findViewById(R.id.et2);
        et3 = findViewById(R.id.et3);
        et4 = findViewById(R.id.et4);
        et5 = findViewById(R.id.et5);
        et6 = findViewById(R.id.et6);
        et7 = findViewById(R.id.et7);
        et8 = findViewById(R.id.et8);
        et9 = findViewById(R.id.et9);

        String allRus = "абвгдежзийклмнопрстуфхцчшщьыъэюя";
        String allNum = "0123456789";

        et2.setOnKeyListener(new OnKeyListenerBefore(et1));
        et3.setOnKeyListener(new OnKeyListenerBefore(et2));
        et4.setOnKeyListener(new OnKeyListenerBefore(et3));
        et5.setOnKeyListener(new OnKeyListenerBefore(et4));
        et6.setOnKeyListener(new OnKeyListenerBefore(et5));
        et7.setOnKeyListener(new OnKeyListenerBefore(et6));
        et8.setOnKeyListener(new OnKeyListenerBefore(et7));
        et9.setOnKeyListener(new OnKeyListenerBefore(et8));

        et1.addTextChangedListener(new FocusAfterChange(et2, allRus));
        et2.addTextChangedListener(new FocusAfterChange(et3, allNum));
        et3.addTextChangedListener(new FocusAfterChange(et4, allNum));
        et4.addTextChangedListener(new FocusAfterChange(et5, allNum));
        et5.addTextChangedListener(new FocusAfterChange(et6, allRus));
        et6.addTextChangedListener(new FocusAfterChange(et7, allRus));
        et7.addTextChangedListener(new FocusAfterChange(et8, allNum));
        et8.addTextChangedListener(new FocusAfterChange(et9, allNum));
        et9.addTextChangedListener(new FocusAfterChange(actvFIO, allNum));

        et1.requestFocus();


    }

    protected void addTransport(){

        descTransport = et1.getText().toString() + et2.getText().toString()
                + et3.getText().toString() + et4.getText().toString() + et5.getText().toString()
                + et6.getText().toString() + et7.getText().toString() + et8.getText().toString() + et9.getText().toString();

        final HttpClient httpClient = new HttpClient(this);
        httpClient.addParam("filter", descTransport);

        httpClient.postProc("getTransportByFilter", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {

            }

            @Override
            public void processResponse(JSONObject response) {

                JSONArray jsonTransports = httpClient.getJsonArrayFromJsonObject(response, "TransportByFilter");

                if (jsonTransports.length() > 0){

                    JSONObject jsonTransport = httpClient.getItemJSONArray(jsonTransports, 0);

                    refTransport = httpClient.getStringFromJSON(jsonTransport, "RefTransport");
                    descTransport = httpClient.getStringFromJSON(jsonTransport, "DescTransport");

                }

                descDriver = descDriver.isEmpty() ? actvFIO.getText().toString() : descDriver;

                addTransportEnd();

            }
        });



    }

    protected void addTransportEnd(){

        final HttpClient httpClient = new HttpClient(this);
        httpClient.addParam("filter", descTransport);
        httpClient.addParam("refTransport", refTransport);
        httpClient.addParam("descTransport", descTransport);
        httpClient.addParam("refDriver", refDriver);
        httpClient.addParam("descDriver", descDriver);

        httpClient.postProc("addTransport", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {

            }

            @Override
            public void processResponse(JSONObject response) {

                Intent intent = new Intent();

                intent.putExtra("refTransport", httpClient.getStringFromJSON(response, "RefTransport"));
                intent.putExtra("descTransport", descTransport);
                intent.putExtra("refDriver", httpClient.getStringFromJSON(response, "RefDriver"));
                intent.putExtra("descDriver", descDriver);

                setResult(RESULT_OK, intent);

                finish();

            }
        });



    }

}
