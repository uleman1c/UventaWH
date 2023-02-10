package com.example.uventawh;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ContainsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContainsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ContainsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ContainsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ContainsFragment newInstance(String param1, String param2) {
        ContainsFragment fragment = new ContainsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    DelayAutoCompleteTextView actvShtrihCode;

    private InputMethodManager imm;

    private boolean shtrihCodeKeyboard = false, inputSenderAddress, inputDelivererAddress, inputQuantity;

    List<String> toScan = new ArrayList<>();
    List<ScannedShtrihCode> scanned = new ArrayList<>();

    ScannedShtrihCodeAdapter adapterScanned;

    Handler h;

    String refAccept, refContractor, shtrihCode;

    ProgressBar progressBar;
    LinearLayout llMain;

    EditText etQuantity;

    androidx.appcompat.app.AlertDialog quantityDialog = null;

    Double quantity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_contains, container, false);

        actvShtrihCode = root.findViewById(R.id.actvShtrihCode);
        actvShtrihCode.setThreshold(3);

        actvShtrihCode.setAdapter(new StringAutoCompleteAdapter(getContext(), toScan));
        actvShtrihCode.setLoadingIndicator((ProgressBar) root.findViewById(R.id.progress_bar0));

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

        actvShtrihCode.requestFocus();

        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

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


        root.findViewById(R.id.ibKeyboard).setOnClickListener(new View.OnClickListener() {
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

        h = new Handler();

        Bundle bundle = getArguments();
        refAccept = bundle.getString("refAccept");
        shtrihCode = bundle.getString("shtrihCode");
        refContractor = bundle.getString("refContractor");

        progressBar = root.findViewById(R.id.progressBar);
        llMain = root.findViewById(R.id.llMain);

        ((TextView) root.findViewById(R.id.header)).setText("Состав: " + shtrihCode);

        RecyclerView recyclerViewScanned = (RecyclerView) root.findViewById(R.id.scanned);

        adapterScanned = new ScannedShtrihCodeAdapter(getContext(), scanned);
        recyclerViewScanned.setAdapter(adapterScanned);


        contractorSettings();



        return root;
    }


    private void contractorSettings() {

        final HttpClient httpClient = new HttpClient(getContext());
        httpClient.addParam("ref", refContractor);

        httpClient.postForResult("getContractorSettings", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {
                progressBar.setVisibility(visibility);
            }

            @Override
            public void processResponse(JSONObject response) {

                if (httpClient.getBooleanFromJSON(response, "Success")) {

                    inputSenderAddress = httpClient.getBooleanFromJSON(response, "InputSenderAddress");
                    inputDelivererAddress = httpClient.getBooleanFromJSON(response, "InputDelivererAddress");
                    inputQuantity = httpClient.getBooleanFromJSON(response, "InputQuantity");

                    getReceiptShtrihs();

                }
            }

        });

    }

    protected void getReceiptShtrihs(){

        final HttpClient httpClient = new HttpClient(getContext());
        httpClient.addParam("refAccept", refAccept);

        httpClient.postProc("getAcceptShtrihs", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {
                progressBar.setVisibility(visibility);
            }

            @Override
            public void processResponse(JSONObject response) {

                JSONArray accept = httpClient.getJsonArrayFromJsonObject(response, "AcceptShtrihs");

                toScan.clear();

                for (int i = 0; i < accept.length(); i++){

                    JSONObject accept_item = httpClient.getItemJSONArray(accept, i);

                    Boolean tested = httpClient.getBooleanFromJSON(accept_item, "Tested");
                    Boolean added = httpClient.getBooleanFromJSON(accept_item, "Added");
                    String date = httpClient.getStringFromJSON(accept_item, "Date");
                    String shtrih_code = httpClient.getStringFromJSON(accept_item, "ShtrihCode");
                    Double quantity = inputQuantity ? httpClient.getDoubleFromJSON(accept_item, "Quantity") : null;

                    toScan.add(shtrih_code);


                }

                getShtrihs();
//                adapterScanned.notifyDataSetChanged();
//                adapterToScan.notifyDataSetChanged();
//
//                Integer scannedQ = scanned.size();
//                Integer toAcceptQ = toScan.size() + scannedQ;
//
//                Integer procent = toAcceptQ == 0 ? 100 : scannedQ * 100 / toAcceptQ;
//
//                if (toScan.size() == 0){
//                    btnClose.setVisibility(View.VISIBLE);
//                }
//
//                progressTextView.setText(scannedQ.toString() + " из " + toAcceptQ.toString() + ", " + procent.toString() + "%");
//                progressTextView.setMax(toAcceptQ);
//                progressTextView.setProgress(scannedQ);

            }
        });

    }



    protected void getShtrihs(){

        final HttpClient httpClient = new HttpClient(getContext());
        httpClient.addParam("refContractor", refContractor);
        httpClient.addParam("shtrihCode", shtrihCode);

        httpClient.postForResult("getShtrihContains", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {
                progressBar.setVisibility(visibility);
            }

            @Override
            public void processResponse(JSONObject response) {

                JSONArray accept = httpClient.getJsonArrayFromJsonObject(response, "ShtrihContains");

//                toScan.clear();
                scanned.clear();

                for (int i = 0; i < accept.length(); i++){

                    JSONObject accept_item = httpClient.getItemJSONArray(accept, i);

                    Boolean tested = httpClient.getBooleanFromJSON(accept_item, "Tested");
                    Boolean added = httpClient.getBooleanFromJSON(accept_item, "Added");
                    String date = httpClient.getStringFromJSON(accept_item, "Date");
                    String shtrih_code = httpClient.getStringFromJSON(accept_item, "ShtrihCode");
                    Double quantity = inputQuantity ? httpClient.getDoubleFromJSON(accept_item, "Quantity") : null;

                    scanned.add(new ScannedShtrihCode(shtrih_code, date, added, quantity));
//                    if (tested){
//
//                    } else {
//
////                        toScan.add(shtrih_code);
//                    }


                }

                adapterScanned.notifyDataSetChanged();
//                adapterToScan.notifyDataSetChanged();
//
//                Integer scannedQ = scanned.size();
//                Integer toAcceptQ = toScan.size() + scannedQ;
//
//                Integer procent = toAcceptQ == 0 ? 100 : scannedQ * 100 / toAcceptQ;
//
//                if (toScan.size() == 0){
//                    btnClose.setVisibility(View.VISIBLE);
//                }
//
//                progressTextView.setText(scannedQ.toString() + " из " + toAcceptQ.toString() + ", " + procent.toString() + "%");
//                progressTextView.setMax(toAcceptQ);
//                progressTextView.setProgress(scannedQ);

            }
        });

    }

    private void inputQuantity(final String strCatName) {

        androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
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

        setShtrihs(strCatName, quantity);
    }

    protected void setShtrihs(final String shtrihcode1, final Double quantity){

        final HttpClient httpClient = new HttpClient(getContext());
        httpClient.addParam("refContractor", refContractor);
        httpClient.addParam("shtrihCode", shtrihCode);
        httpClient.addParam("shtrihCodeContains", shtrihcode1);
        httpClient.addParam("quantity", quantity);

        httpClient.postForResult("setShtrihContains", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {
                progressBar.setVisibility(visibility);
            }

            @Override
            public void processResponse(JSONObject response) {

                if (httpClient.getBooleanFromJSON(response, "Success")){

                    scanned.add(new ScannedShtrihCode(shtrihcode1, "", true, quantity));

                    adapterScanned.notifyDataSetChanged();

                    //                    if (inputQuantity && httpClient.getBooleanFromJSON(response, "IsNew")){
//
//                        setQuantityToShtrih(shtrihcode, quantity);
//
//                    }
//                    else {
//                        getShtrihs();
//                    }

                };

            }
        });

    }


    private void scanShtrihCode(String strCatName) {

        inputQuantity(strCatName);

    }


}