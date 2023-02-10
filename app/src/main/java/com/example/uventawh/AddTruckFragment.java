package com.example.uventawh;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import org.json.JSONArray;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddTruckFragment extends Fragment {

    public AddTruckFragment() {
        // Required empty public constructor
    }


    DelayAutoCompleteTextView actvNumber, actvFIO;
    String refTransport, descTransport = "", refDriver, descDriver = "";

    EditText et1,et2, et3, et4, et5, et6, et7, et8, et9;

    ProgressBar progressBar;

    Handler h = new Handler();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_add_truck, container, false);

        DB db = new DB(getContext());
        refTransport = db.emptyRef;
        refDriver = db.emptyRef;

        String url = db.getRequestUserProg();

        progressBar = root.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        actvNumber = root.findViewById(R.id.actvNumber);
        actvNumber.setThreshold(3);

        actvNumber.setAdapter(new RefDescAutoCompleteAdapter(getContext(), url, new HttpClientSync(getContext())));
        actvNumber.setLoadingIndicator((ProgressBar) root.findViewById(R.id.progress_bar));

        actvNumber.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                RefDesc refDesc = (RefDesc) adapterView.getItemAtPosition(position);

                refTransport = refDesc.ref;
                descTransport = refDesc.desc;

                actvNumber.setText(descTransport);
            }
        });

        actvFIO = root.findViewById(R.id.actvFIO);
        actvFIO.setThreshold(3);

        actvFIO.setAdapter(new DriverAutoCompleteAdapter(getContext(), url, new HttpClientSync(getContext())));
        actvFIO.setLoadingIndicator((ProgressBar) root.findViewById(R.id.progress_barFio));

        actvFIO.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                RefDesc refDesc = (RefDesc) adapterView.getItemAtPosition(position);

                refDriver = refDesc.ref;
                descDriver = refDesc.desc;

                actvFIO.setText(descDriver);

                Thread t = new Thread(new Runnable() {
                    public void run() {
                        h.post(hideKeyboard);
                    }
                });
                t.start();



            }
        });

        root.findViewById(R.id.fabOk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Boolean validated = true;

                if(et1.getText().length() == 0 || et2.getText().length() == 0
                        || et3.getText().length() == 0 || et4.getText().length() == 0
                        || et5.getText().length() == 0 || et6.getText().length() == 0
                        || et7.getText().length() == 0 || et8.getText().length() == 0
                ){

                    validated = false;

                    Toast toast = Toast.makeText(getContext(), "Не заполнен автомобиль", Toast.LENGTH_SHORT);
                    toast.show();


                }

                if(actvFIO.getText().length() < 6){

                    validated = false;

                    Toast toast = Toast.makeText(getContext(), "Не заполнен водитель", Toast.LENGTH_SHORT);
                    toast.show();


                }

                if (validated){

                    addTransport();

                }

            }
        });

        et1 = root.findViewById(R.id.et1);
        et2 = root.findViewById(R.id.et2);
        et3 = root.findViewById(R.id.et3);
        et4 = root.findViewById(R.id.et4);
        et5 = root.findViewById(R.id.et5);
        et6 = root.findViewById(R.id.et6);
        et7 = root.findViewById(R.id.et7);
        et8 = root.findViewById(R.id.et8);
        et9 = root.findViewById(R.id.et9);

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

        Thread t = new Thread(new Runnable() {
            public void run() {
                h.post(showKeyboard);
            }
        });
        t.start();

        return root;
    }

    final Runnable showKeyboard = new Runnable() {
        public void run() {

            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(et1, 0);

        }
    };

    final Runnable hideKeyboard = new Runnable() {
        public void run() {

            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(actvFIO.getWindowToken(), 0);

        }
    };


    protected void addTransport(){

        descTransport = et1.getText().toString() + et2.getText().toString()
                + et3.getText().toString() + et4.getText().toString() + et5.getText().toString()
                + et6.getText().toString() + et7.getText().toString() + et8.getText().toString() + et9.getText().toString();

        final HttpClient httpClient = new HttpClient(getContext());
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

        final HttpClient httpClient = new HttpClient(getContext());
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

                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).popBackStack();

//                Intent intent = new Intent();
//
//                intent.putExtra("refTransport", httpClient.getStringFromJSON(response, "RefTransport"));
//                intent.putExtra("descTransport", descTransport);
//                intent.putExtra("refDriver", httpClient.getStringFromJSON(response, "RefDriver"));
//                intent.putExtra("descDriver", descDriver);
//
//                setResult(RESULT_OK, intent);
//
//                finish();

            }
        });



    }



}
