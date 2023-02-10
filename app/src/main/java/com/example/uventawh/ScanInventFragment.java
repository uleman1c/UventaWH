package com.example.uventawh;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ScanInventFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScanInventFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ScanInventFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ScanInventFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ScanInventFragment newInstance(String param1, String param2) {
        ScanInventFragment fragment = new ScanInventFragment();
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

    String ref, contractorRef, contractorDescription, date;
    Boolean contractorInputQuantity;

    EditText actvShtrihCode;

    ProgressTextView progressTextView;

//    private InputMethodManager imm;

    private boolean shtrihCodeKeyboard = false;

    Boolean manualInput = false, sendingInProgress = false;

    List<ScannedShtrihCode> scanned = new ArrayList<>();

    ScannedShtrihCodeAdapter adapterScanned;

    ProgressBar progressBar;

    EditText etQuantity;

    Handler h;

    AlertDialog quantityDialog = null;

    Double quantity;

    Bundle bundle;

    TextView tvQuantity;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_scan_invent, container, false);

        bundle = getArguments();

        date = bundle.getString("inventDate");
        ref = bundle.getString("inventRef");
        contractorRef = bundle.getString("contractorRef");
        contractorDescription = bundle.getString("contractorDescription");
        contractorInputQuantity = bundle.getBoolean("contractorInputQuantity");

        String header = bundle.getString("header");

        if (header != null){

            ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
            actionBar.setTitle(header + " - " + actionBar.getTitle());

        }

        ((TextView) root.findViewById(R.id.header)).setText(contractorDescription + ", " + StrDateTime.strToDate(date) + " " + StrDateTime.strToTime(date));

        progressBar = root.findViewById(R.id.progressBar);
        tvQuantity = root.findViewById(R.id.tvQuantity);

        progressTextView = root.findViewById(R.id.scannedText);

        actvShtrihCode = root.findViewById(R.id.actvShtrihCode);

        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

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

                                                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

                                                    imm.hideSoftInputFromWindow(actvShtrihCode.getWindowToken(), 0);


                                                    if (contractorInputQuantity){
                                                        inputQuantity(strCatName);
                                                    }
                                                    else {
                                                        scanShtrihCode(strCatName, 1.0);
                                                    }
                                                    return true;
                                                }
                                                return false;
                                            }
                                        }
        );


        root.findViewById(R.id.ibKeyboard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

                actvShtrihCode.requestFocus();

                if (shtrihCodeKeyboard){

                    imm.hideSoftInputFromWindow(actvShtrihCode.getWindowToken(), 0);

                } else {

                    imm.showSoftInput(actvShtrihCode, 0, null);
                }

                shtrihCodeKeyboard = !shtrihCodeKeyboard;


            }
        });


        RecyclerView recyclerViewScanned = root.findViewById(R.id.scanned);

        adapterScanned = new ScannedShtrihCodeAdapter(getContext(), scanned);
        recyclerViewScanned.setAdapter(adapterScanned);

        adapterScanned.setOnStringClickListener(new ScannedShtrihCodeAdapter.OnStringClickListener(){
            @Override
            public void onStringClick(ScannedShtrihCode str, View itemView) {

//                askForAddKoeff(str.shtrihCode);
                askForSetName(str.shtrihCode);


            }
        });



        h = new Handler();

        Thread t = new Thread(new Runnable() {
            public void run() {
                h.post(setFocus);
            }
        });
        t.start();

        if (ref.equals(getString(R.string.emptyRef))){

            addInvent();

        }
        else {
            getInventShtrihs(ref);
        }

        return root;
    }

    private void addInvent() {

        final HttpClient httpClient = new HttpClient(getContext());
        httpClient.addParam("contractorRef", contractorRef);

        httpClient.postForResult("addInvent", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {
                progressBar.setVisibility(visibility);
            }

            @Override
            public void processResponse(JSONObject response) {

                ref = httpClient.getStringFromJSON(response, "Ref");

                getInventShtrihs(ref);
            }

        });
    }

    protected void getInventShtrihs(final String ref){

        final HttpClient httpClient = new HttpClient(getContext());
        httpClient.addParam("ref", ref);

        httpClient.postProc("getInventShtrihs", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {
                progressBar.setVisibility(visibility);
            }

            @Override
            public void processResponse(JSONObject response) {

                scanned.clear();

                JSONArray accept = httpClient.getJsonArrayFromJsonObject(response, "InventShtrihs");

                for (int i = 0; i < accept.length(); i++){

                    JSONObject accept_item = httpClient.getItemJSONArray(accept, i);

                    String date = httpClient.getStringFromJSON(accept_item, "Date");
                    String shtrih_code = httpClient.getStringFromJSON(accept_item, "ShtrihCode");
                    Double quantity = contractorInputQuantity ? httpClient.getDoubleFromJSON(accept_item, "Quantity") : null;

                    scanned.add(new ScannedShtrihCode(shtrih_code, date, false, quantity));

                }

                adapterScanned.notifyDataSetChanged();
//                adapterToScan.notifyDataSetChanged();
//
//                updatePercent();
//
//                if (mode != null && mode.equals("route")){
//
//                    Integer index = receipts.indexOf(refAccept);
//                    if (index + 1 < receipts.size()) {
//
//                        getReceiptShtrihs(receipts.get(index + 1));
//
//                    }
//
//                }
//
            }

        });

    }



    final Runnable setFocus = new Runnable() {
        public void run() {

            if (!actvShtrihCode.isFocused()){

                actvShtrihCode.requestFocus();
            }

            if (actvShtrihCode.isFocused() && !shtrihCodeKeyboard) {

                try {

                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(actvShtrihCode.getWindowToken(), 0);

                } catch (Exception e){

                }

            }

            h.postDelayed(setFocus, 500);


        }
    };

    private void askForSetName(final String strShtrihCode) {

        bundle.putString("refAccept", ref);
        bundle.putString("refContractor", contractorRef);
        bundle.putString("shtrihCode", strShtrihCode);

        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.setNameFragment, bundle);



    }

    private void askForAddKoeff(final String strShtrihCode) {

        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle("Выбрать коэффициент");

        alertDialogBuilder.setIcon(R.drawable.sklad96);

        String[] items = {"Коэффициент 2.000", "Коэффициент 3.000", "Коэффициент 4.000", "Коэффициент 5.000", "Коэффициент 6.000", "Коэффициент 7.000", "Коэффициент 8.000", "Коэффициент 9.000"};
        alertDialogBuilder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                sendAddService(strShtrihCode, "addKoeff", which + 2);

            }
        });

        final android.app.AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();

    }

    private void sendAddService(String strShtrihCode, String service, Integer koeff){

//        HttpClient httpClient = new HttpClient(getContext());
//        httpClient.addParam("shtrihCode", strShtrihCode);
//        httpClient.addParam("cellRef", cellRef);
//        httpClient.addParam("service", service);
//        httpClient.addParam("koeff", koeff);
//
//        httpClient.postForResult("setAddServiceToCellByShtrihCode", new HttpRequestInterface() {
//            @Override
//            public void setProgressVisibility(int visibility) {
//
//            }
//
//            @Override
//            public void processResponse(JSONObject response) {
//
//            }
//        });

    }


    private void inputQuantity(final String strCatName) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
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

//        Thread t = new Thread(new Runnable() {
//            public void run() {
//                h2.post(setFocus2);
//            }
//        });
//        t.start();




    }

    private void CloseScan(View view, String strCatName) {
        quantityDialog.cancel();

        quantity = Double.valueOf(((TextView) view.findViewById(R.id.etQuantity)).getText().toString());

        scanShtrihCode(strCatName, quantity);
    }

    final Runnable setFocus2 = new Runnable() {
        public void run() {

            InputMethodManager imm = (InputMethodManager) quantityDialog.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

            etQuantity.requestFocus();

            imm.showSoftInput(etQuantity, 0, null);


        }
    };


    private void scanShtrihCode(String strCatName, Double quantity) {

        if (!sendingInProgress){

            scanned.add(0, new ScannedShtrihCode(strCatName, StrDateTime.dateToStr(new Date()), false, quantity));
            adapterScanned.notifyDataSetChanged();

            tvQuantity.setText("Количество: " + String.valueOf(scanned.size()));

            setShtrihs(strCatName, quantity);

        }
    }


    protected void setShtrihs(String shtrihcode, Double quantity) {

        setShtrihsToInvent(shtrihcode, quantity);

    }

    private void setShtrihsToInvent(String shtrihcode, Double quantity) {

        final HttpClient httpClient = new HttpClient(getContext());
        httpClient.addParam("ref", ref);
        httpClient.addParam("shtrihCode", shtrihcode);
        httpClient.addParam("quantity", quantity);

        httpClient.postForResult("setAddToInvent", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {
                progressBar.setVisibility(visibility);
            }

            @Override
            public void processResponse(JSONObject response) {

                if (httpClient.getBooleanFromJSON(response, "Success")){

//                    getShtrihs();

                };

            }
        });

    }

    protected void setShtrihsToCell(String shtrihcode, Double quantity){

//        final HttpClient httpClient = new HttpClient(getContext());
//        httpClient.addParam("contractorRef", contractorRef);
//        httpClient.addParam("cell", cell);
//        httpClient.addParam("cellRef", cellRef);
//        httpClient.addParam("shtrihCode", shtrihcode);
//        httpClient.addParam("quantity", quantity);
//
//        httpClient.postForResult("setAddToCell", new HttpRequestInterface() {
//            @Override
//            public void setProgressVisibility(int visibility) {
//                progressBar.setVisibility(visibility);
//            }
//
//            @Override
//            public void processResponse(JSONObject response) {
//
//                if (httpClient.getBooleanFromJSON(response, "Success")){
//
////                    getShtrihs();
//
//                };
//
//            }
//        });

    }





}