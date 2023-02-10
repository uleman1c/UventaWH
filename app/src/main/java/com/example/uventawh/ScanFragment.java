package com.example.uventawh;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ScanFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScanFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private class ReceiptRefCode{

        public String receiptRef, code;

        public ReceiptRefCode(String receiptRef, String code) {
            this.receiptRef = receiptRef;
            this.code = code;
        }
    }

    public ScanFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ScanFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ScanFragment newInstance(String param1, String param2) {
        ScanFragment fragment = new ScanFragment();
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

    String refRoute, ref, name, number, date, refContractor, company, refSender, sender, refReceiver, receiver, curShtrihCode, currentPhotoPath, mode;
    Integer image, codeSymbolsQuantity;

    EditText editText;

    List<ReceiptRefCode> receiptRefCodes = new ArrayList<>();

    ArrayList<String> receipts = new ArrayList<>();
    List<String> toScanStart = new ArrayList<>();
    List<String> toScan = new ArrayList<>();
    List<ScannedShtrihCode> scanned = new ArrayList<>();

    ScannedShtrihCodeAdapter adapterScanned;
    DataStringAdapter adapterToScan;

    ProgressBar progressBar;
    LinearLayout llMain;

    ProgressTextView progressTextView;

    private Button btnClose;

    private Timer timer=new Timer();
    private final long DELAY = 1000; // milliseconds

    int cnt;
    final int max = 100;
    Handler h;

    Boolean manualInput = false, sendingInProgress = false;

    Uri outputFileUri;

    private static final int CAMERA_REQUEST = 20;

    private SoundPlayer soundPlayer;
    private String emptyRef = "00000000-0000-0000-0000-000000000000";


    EditText actvShtrihCode;
//    DelayAutoCompleteTextView actvShtrihCode;

    private boolean shtrihCodeKeyboard = false, inputSenderAddress, inputDelivererAddress, inputQuantity, update_scan, inputDelivererAddressOnEmptyReceipt;

    EditText etQuantity;

    androidx.appcompat.app.AlertDialog quantityDialog = null;

    Double quantity;

    Bundle bundle;

    TextView tvQuantity;

    GetFoto getFoto;
    String pathFoto;

    View root;

    private ArrayList<RefDesc> receivers = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_scan, container, false);

        DB db = new DB(getContext());
        db.open();
        String update_scanStr = db.getConstant("update_scan");
        update_scan = (update_scanStr != null && update_scanStr.equals("true")) ? true : false;
        db.close();

        ((TextView) root.findViewById(R.id.tvVersion)).setText(getResources().getString(R.string.version));

        progressTextView = root.findViewById(R.id.scannedText);

        actvShtrihCode = root.findViewById(R.id.actvShtrihCode);
//        actvShtrihCode.setThreshold(3);
//
//        actvShtrihCode.setAdapter(new StringAutoCompleteAdapter(getContext(), toScan));
//        actvShtrihCode.setLoadingIndicator((ProgressBar) root.findViewById(R.id.progress_bar0));
//
//        actvShtrihCode.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
//
//                String strCatName = (String) adapterView.getItemAtPosition(position);
//
//                actvShtrihCode.setText("");
//
//                shtrihCodeKeyboard = false;
//
//                hideKeyboard();
//
//                scanShtrihCode(strCatName);
//
//            }
//        });

//        actvShtrihCode.requestFocus();

        hideKeyboard();

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

                                                    hideKeyboard();

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

//                actvShtrihCode.requestFocus();
//
//                if (shtrihCodeKeyboard){
//
//                    hideKeyboard();
//
//                } else {
//
//                    actvShtrihCode.requestFocus();
//
//                    ((MainWareHouseActivity) getActivity()).imm.showSoftInput(actvShtrihCode, 0, null);
//                }
//
//                shtrihCodeKeyboard = !shtrihCodeKeyboard;


            }
        });



        soundPlayer = new SoundPlayer(getContext(), R.raw.hrn05);
        getActivity().setVolumeControlStream(soundPlayer.streamType);

        h = new Handler();

        bundle = getArguments();
        refRoute = bundle.getString("refRoute");
        ref = bundle.getString("ref");
        name = bundle.getString("name");
        number = bundle.getString("number");
        date = bundle.getString("date");
        refContractor = bundle.getString("refContractor");
        company = bundle.getString("company");
        refSender = bundle.getString("refSender");
        sender = bundle.getString("sender");

        refReceiver = bundle.getString("refReceiver");

        refReceiver = refReceiver == null ? emptyRef : refReceiver;

        receiver = bundle.getString("receiver");

        image = (int) bundle.getLong("image", 0);
        mode = bundle.getString("mode");
        receipts = bundle.getStringArrayList("receipts");

        progressBar = root.findViewById(R.id.progressBar);
        llMain = root.findViewById(R.id.llMain);

        ((TextView) root.findViewById(R.id.header)).setText(name + " №" + number + " от " + date + " " + company
                + (sender == null ? "" : ", " + sender));

        editText = (EditText) root.findViewById(R.id.editText);
        editText.setOnKeyListener(new View.OnKeyListener()
                                  {
                                      public boolean onKey(View v, int keyCode, KeyEvent event)
                                      {
                                          if(event.getAction() == KeyEvent.ACTION_DOWN &&
                                                  (keyCode == KeyEvent.KEYCODE_ENTER))
                                          {
                                              manualInput = false;

                                              String strCatName = editText.getText().toString();

                                              editText.setText("");

                                              scanShtrihCode(strCatName);

                                              adapterToScan.notifyDataSetChanged();

                                              return true;
                                          }
                                          return false;
                                      }
                                  }
        );

        Thread t = new Thread(new Runnable() {
            public void run() {
                h.post(setFocus);
            }
        });
        t.start();

        root.findViewById(R.id.btnOcr).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent("com.example.uventawh.action.startocrcapture");

                startActivityForResult(intent, 2);


            }
        });

        root.findViewById(R.id.btnAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent("com.example.uventawh.action.add_to_accept_activity");

                intent.putExtra("ref", ref);
                intent.putExtra("name", name);
                intent.putExtra("number", number);
                intent.putExtra("date", date);
                intent.putExtra("refContractor", refContractor);
                intent.putExtra("company", company);
                intent.putExtra("refSender", refSender);
                intent.putExtra("sender", sender);

                startActivityForResult(intent, 3);


            }
        });

        adapterToScan = new DataStringAdapter(getContext(), toScan);

        adapterToScan.setOnStringClickListener(new DataStringAdapter.OnStringClickListener(){
            @Override
            public void onStringClick(String str, View itemView) {

                TakeScreenShot.Do(getContext(), root, itemView);

                if (manualInput) {

                    scanShtrihCode(str);

                    adapterToScan.notifyDataSetChanged();

                    manualInput = false;
                }
            }
        });



        RecyclerView recyclerViewToScan = (RecyclerView) root.findViewById(R.id.toScan);
        recyclerViewToScan.setAdapter(adapterToScan);

        RecyclerView recyclerViewScanned = (RecyclerView) root.findViewById(R.id.scanned);

        adapterScanned = new ScannedShtrihCodeAdapter(getContext(), scanned);
        recyclerViewScanned.setAdapter(adapterScanned);

        adapterScanned.setOnStringClickListener(new ScannedShtrihCodeAdapter.OnStringClickListener(){
            @Override
            public void onStringClick(final ScannedShtrihCode str, View itemView) {

                TakeScreenShot.Do(getContext(), root, itemView);

                final HttpClient httpClient = new HttpClient(getContext());

                httpClient.addParam("shtrihCode", str.shtrihCode);
                httpClient.addParam("receipt", ref);

                httpClient.postForResult("getReceiverCode", new HttpRequestInterface() {
                    @Override
                    public void setProgressVisibility(int visibility) {

                    }

                    @Override
                    public void processResponse(JSONObject response) {

                        JSONObject receiverJson = httpClient.getJsonObjectFromJsonObject(response, "Receiver");

                        askForAddService(str.shtrihCode, httpClient.getStringFromJSON(receiverJson, "Description"));
                    }
                });


            }
        });

        recyclerViewScanned.setAdapter(adapterScanned);

        btnClose = root.findViewById(R.id.btnClose);
        btnClose.setVisibility(View.GONE);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TakeScreenShot.Do(getContext(), root, v);

                close();

            }
        });

        contractorSettings();

        return root;
    }

    private void hideKeyboard() {

//        View v = ((Activity) getContext()).getCurrentFocus();
//        if (v != null) {
////            ((MainWareHouseActivity) getActivity()).imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
//        }
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

                    codeSymbolsQuantity = httpClient.getIntegerFromJSON(response, "CodeSymbolsQuantity");
                    inputSenderAddress = httpClient.getBooleanFromJSON(response, "InputSenderAddress");
                    inputDelivererAddress = httpClient.getBooleanFromJSON(response, "InputDelivererAddress");
                    inputQuantity = httpClient.getBooleanFromJSON(response, "InputQuantity");
                    inputDelivererAddressOnEmptyReceipt = httpClient.getBooleanFromJSON(response, "InputDelivererAddressOnEmptyReceipt");

                    if (!inputDelivererAddressOnEmptyReceipt) {
                        getReceiptReceivers();
                    }
                    else {
                        getShtrihs();
                    }
                }
            }

        });

    }

    private void getReceiptReceivers() {

        final HttpClient httpClient = new HttpClient(getContext());
        httpClient.addParam("receipt", ref);

        httpClient.postForResult("getReceiptReceivers", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {

            }

            @Override
            public void processResponse(JSONObject response) {

                receivers.clear();

                if (httpClient.getBooleanFromJSON(response, "Success")){

                    JSONArray jsReceipts = httpClient.getJsonArrayFromJsonObject(response, "ReceiptReceivers");

                    for (int i = 0; i < jsReceipts.length(); i++) {

                        JSONObject jsReceipt = httpClient.getItemJSONArray(jsReceipts, i);

                        receivers.add(new RefDesc(httpClient.getStringFromJSON(jsReceipt, "Ref"),
                                httpClient.getStringFromJSON(jsReceipt, "Description")));

                    }

                    getShtrihs();

                }

            }
        });
    }


    private void close() {


        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle("Закрытие приемки");

        alertDialogBuilder.setIcon(R.drawable.sklad96);

        LayoutInflater inflater = this.getLayoutInflater();

        final View view = inflater.inflate(R.layout.dialog_yes_no_cancel, null);

        ((TextView) view.findViewById(R.id.tvQuestion)).setText("Закрыть приемку \"" + name + " №" + number + " от " + date + " " + company + ", " + sender +"\" ?");

        alertDialogBuilder.setView(view);
        alertDialogBuilder.setIcon(R.drawable.sklad96);

        final AlertDialog alertDialog = alertDialogBuilder.create();

        view.findViewById(R.id.btnYes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TakeScreenShot.Do(getContext(), view, v);

                closeReceipt();

                alertDialog.cancel();


            }
        });

        view.findViewById(R.id.btnNo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TakeScreenShot.Do(getContext(), view, v);

                alertDialog.cancel();

            }
        });

        view.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TakeScreenShot.Do(getContext(), view, v);

                alertDialog.cancel();

            }
        });

        alertDialog.show();



    }

    private void closeReceipt() {

        final HttpClient httpClient = new HttpClient(getContext());
        httpClient.addParam("ref", ref);

        httpClient.postForResult("closeReceipt", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {

            }

            @Override
            public void processResponse(JSONObject response) {

                if(httpClient.getBooleanFromJSON(response, "Success")){

//                    Intent intent = new Intent();
//
//                    setResult(RESULT_OK, intent);
//
//                    finish();

                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment).popBackStack(R.id.editRouteFragment, false);

                };

            }
        });

    }


    private void askForRepeatCode(final String strShtrihCode, String message) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle(strShtrihCode + message);

        alertDialogBuilder.setIcon(R.drawable.sklad96);

        LayoutInflater inflater = this.getLayoutInflater();

        final View view = inflater.inflate(R.layout.dialog_ask_repeat_shtrihcode, null);

        alertDialogBuilder.setView(view);
        alertDialogBuilder.setIcon(R.drawable.sklad96);

        final AlertDialog alertDialog = alertDialogBuilder.create();

        view.findViewById(R.id.btnGenerate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                setShtrihs("");

                alertDialog.cancel();


            }
        });

        view.findViewById(R.id.btnIgnore).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TakeScreenShot.Do(getContext(), view, v);

                alertDialog.cancel();

            }
        });

        alertDialog.show();


    }

    private void askForAddService(final String strShtrihCode, String description) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle(strShtrihCode + " доп.услуги:");

        alertDialogBuilder.setIcon(R.drawable.sklad96);

        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View view = inflater.inflate(R.layout.dialog_ask_add_service, null);

        alertDialogBuilder.setView(view);
        alertDialogBuilder.setIcon(R.drawable.sklad96);

        BoldStringBuilder builder = new BoldStringBuilder();

        builder.erase();
        builder.addBoldString("Отправитель: ");
        builder.addString(sender + "\n");
        builder.addBoldString("Получатель: ");
        builder.addString(description);

        ((TextView)view.findViewById(R.id.tvAddresses)).setText(builder.spannableStringBuilder, TextView.BufferType.SPANNABLE);

        final AlertDialog alertDialog = alertDialogBuilder.create();

        view.findViewById(R.id.btnAddKoeff).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TakeScreenShot.Do(getContext(), view, v);

                alertDialog.cancel();

                askForAddKoeff(strShtrihCode);

            }
        });

        view.findViewById(R.id.btnRepack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TakeScreenShot.Do(getContext(), view, v);

                alertDialog.cancel();

                sendAddService(strShtrihCode, "repack", 1);

            }
        });

        view.findViewById(R.id.btnFilm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TakeScreenShot.Do(getContext(), view, v);

                alertDialog.cancel();

                sendAddService(strShtrihCode, "film", 1);

            }
        });

        view.findViewById(R.id.btnFoto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TakeScreenShot.Do(getContext(), view, v);

                alertDialog.cancel();

                getFoto(strShtrihCode);

            }
        });

        view.findViewById(R.id.btnContains).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TakeScreenShot.Do(getContext(), view, v);

                alertDialog.cancel();

                getContains(strShtrihCode);

            }
        });

        view.findViewById(R.id.btnName).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TakeScreenShot.Do(getContext(), view, v);

                alertDialog.cancel();

                askForSetName(strShtrihCode);

            }
        });

        view.findViewById(R.id.btnComment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TakeScreenShot.Do(getContext(), view, v);

                alertDialog.cancel();

                askForAddKoeff(strShtrihCode);

            }
        });

        alertDialog.show();


    }

    private void askForSetName(final String strShtrihCode) {

        bundle.putString("refAccept", ref);
        bundle.putString("refContractor", refContractor);
        bundle.putString("shtrihCode", strShtrihCode);

        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.setNameFragment, bundle);



    }



    private void getContains(String strShtrihCode) {

//        Intent intent = new Intent(getContext(), ContainsActivity.class);
//
//        intent.putExtra("refAccept", ref);
//        intent.putExtra("refContractor", refContractor);
//        intent.putExtra("shtrihCode", strShtrihCode);
//
//        startActivityForResult(intent, 3);

        Bundle bundle = new Bundle();
        bundle.putString("refAccept", ref);
        bundle.putString("refContractor", refContractor);
        bundle.putString("shtrihCode", strShtrihCode);

        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.containsFragment, bundle);


    }

    private void getFoto(String strShtrihCode) {

        curShtrihCode = strShtrihCode;

        getFoto = new GetFoto(getContext());

        if (getFoto.intent != null){

            pathFoto = getFoto.uri.toString();

            startActivityForResult(getFoto.intent, CAMERA_REQUEST);

        }

    }


    private void askForAddKoeff(final String strShtrihCode) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle("Выбрать коэффициент");

        alertDialogBuilder.setIcon(R.drawable.sklad96);

        String[] items = {"Коэффициент 2.000", "Коэффициент 3.000", "Коэффициент 4.000", "Коэффициент 5.000", "Коэффициент 6.000", "Коэффициент 7.000", "Коэффициент 8.000", "Коэффициент 9.000"};
        alertDialogBuilder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                sendAddService(strShtrihCode, "addKoeff", which + 2);

            }
        });

        final AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();


    }

    private void sendAddService(String strShtrihCode, String service, Integer koeff){

        final String[] str = new String[1];
        final HttpClient client1 = new HttpClient(getContext());

        DB db = new DB(getContext());
        db.open();

        String url = "request/" + db.getConstant("user_id") + "/" + db.getConstant("prog_id");

        db.close();

        JSONArray params = new JSONArray();

        JSONObject requestParams = new JSONObject();

        try {
            requestParams.put("ref", ref);
            requestParams.put("shtrihCode", strShtrihCode);
            requestParams.put("service", service);
            requestParams.put("koeff", koeff);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject request = new JSONObject();
        try {
            request.put("request", "setAddService");
            request.put("parameters", requestParams);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        params.put(request);

        client1.post(getContext(), url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

                llMain.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinish() {

                llMain.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);

                super.onFinish();
            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {

                str[0] = null;
                try {
                    str[0] = new String(responseBody, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                JSONObject readerArray = null;
                try {
                    readerArray = new JSONObject(str[0]);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JSONArray accept = new JSONArray();
                try {
                    accept = (JSONArray) readerArray.get("Response");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                for (int i = 0; i < accept.length(); i++){

                    JSONObject accept_item = null;

                    try {
                        accept_item = (JSONObject) accept.get(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (client1.getStringFromJSON(accept_item, "Name").equals("setAddService")){

                        JSONArray contractorsJSON = new JSONArray();
                        try {
                            contractorsJSON = (JSONArray) accept_item.get("Addresses");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }

                }



            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
//                if (debug){Toast toast = Toast.makeText(ChooserActivity.this, "FAIL. FBToken: " + FireBaseToken + "StatusCode" + String.valueOf(statusCode), Toast.LENGTH_SHORT);
//                    toast.show();}
                Log.v("HTTPLOG", String.valueOf(statusCode));
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });






    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == 3) {

                getShtrihs();

            }

            else if (requestCode == CAMERA_REQUEST) {

                sendFotoInDoc(ref, curShtrihCode, getFoto.file);

            }

        }

    }

    private void sendFotoInDoc(String ref, String strShtrihCode, final File file){

        final HttpClient httpClient = new HttpClient(getContext());

        httpClient.addParam("type", "doc");
        httpClient.addParam("name", "усПриемка");
        httpClient.addParam("ref", ref);
        httpClient.addParam("typeInDoc", strShtrihCode);

        httpClient.postForResult("getIncomingDocByParameters", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {

            }

            @Override
            public void processResponse(JSONObject response) {

                sendFotoInDocRef(file, httpClient.getStringFromJSON(response, "Ref"), "files/doc/ДокументВходящийДО/");

            }
        });


    }

    private void sendFotoInDocRef(File file, String refInDoc, String pathInDoc){

        final HttpClient httpClient = new HttpClient(getContext());
        httpClient.addFile(file);

        httpClient.postBinaryForResult(pathInDoc + refInDoc, "setExamFoto", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {
//                progressBar.setVisibility(visibility);
            }

            @Override
            public void processResponse(JSONObject response) {

                if (httpClient.getBooleanFromJSON(response, "Success")) {


                }
            }
        });

    }


    private void scanShtrihCode(String strCatName) {

        if (!sendingInProgress){

            List<String> refReceipts = new ArrayList<>();
            Integer index = -1;
            if (codeSymbolsQuantity == 0){

                index = toScan.indexOf(strCatName);
            }
            else {

                String searchStr = strCatName;

                while (searchStr.length() < codeSymbolsQuantity)
                    searchStr = searchStr + " ";

                for (int i = 0; i < receiptRefCodes.size(); i++) {

                    ReceiptRefCode receiptRefCode = receiptRefCodes.get(i);
                    if (receiptRefCode.code.equals(searchStr.substring(0, codeSymbolsQuantity))){

                        index = i;

                        refReceipts.add(receiptRefCode.receiptRef);

                    }

                }

            }

            if (index < 0 || refReceipts.size() > 1){

                soundPlayer.play();

                if (index < 0){

                    Boolean present = false;
                    for (ScannedShtrihCode scannedShtrihCode:scanned){
                        present = scannedShtrihCode.shtrihCode.equals(strCatName);
                        if (present) break;
                    }

                    if (present){

                        askForRepeatCode(strCatName, " уже есть. Выбрать");

                    } else {

                        askForNotFoundCode(strCatName, refReceipts.size() == 0 ? ref : refReceipts.get(0));

                    }
                }
                else {

                    askForRepeatCode(strCatName, " есть в нескольких приемках. Выбрать");

                }


            }
            else {

                Boolean found = false;
                if (codeSymbolsQuantity == 0){

                    toScan.remove(strCatName);
                    adapterToScan.notifyDataSetChanged();

                }
                else {

                    for (int i = 0; i < scanned.size() && !found; i++) {

                        ScannedShtrihCode scannedShtrihCode = scanned.get(i);
                        found = scannedShtrihCode.shtrihCode.equals(strCatName);

                    }

                }

                if (found){

                    askForRepeatCode(strCatName, " уже есть. Выбрать");

                }
                else {
                    String refAccept = refReceipts.size() == 0 ? ref : refReceipts.get(0);

                    if (inputQuantity) {
                        inputQuantity(strCatName, false, false, 0., refAccept);
                    } else {
                        setShtrihs(strCatName, 1.0, false, false, 0., refAccept);
                    }
                }
            }
        }
    }

    private void askForNotFoundCode(final String strShtrihCode, final String refAccept) {

        final HttpClient httpClient = new HttpClient(getContext());
        httpClient.addParam("shtrihCode", strShtrihCode);
        httpClient.addParam("refContractor", refContractor);

        httpClient.postForResult("getCheckDoubleAcceptedGood", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {

            }

            @Override
            public void processResponse(JSONObject response) {

                if (httpClient.getBooleanFromJSON(response, "Success")){

                    JSONArray receipts = httpClient.getJsonArrayFromJsonObject(response, "CheckDoubleAcceptedGood");

                    if (receipts.length() == 0){

                        askForNotFoundCodeDialog(strShtrihCode, refAccept);
                    }
                    else {

                        askForRepeatCode(strShtrihCode, " уже принято в "
                                + httpClient.getStringFromJSON(httpClient.getItemJSONArray(receipts, 0), "Number")
                                + ". Выбрать");

                    }


                }


            }
        });



    }

    private void askForNotFoundCodeDialog(final String strShtrihCode, final String refAccept) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle(strShtrihCode + " не найден. Выбрать");

        alertDialogBuilder.setIcon(R.drawable.sklad96);

        LayoutInflater inflater = this.getLayoutInflater();

        final View view = inflater.inflate(R.layout.dialog_ask_not_found_shtrihcode, null);

        alertDialogBuilder.setView(view);
        alertDialogBuilder.setIcon(R.drawable.sklad96);

        final AlertDialog alertDialog = alertDialogBuilder.create();

        view.findViewById(R.id.btnAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TakeScreenShot.Do(getContext(), view, v);

                if (inputQuantity){
                    inputQuantity(strShtrihCode, true, false, 0., refAccept);
                }
                else {
                    setShtrihs(strShtrihCode, 1.0, true, false, 0., refAccept);
                }

                alertDialog.cancel();


            }
        });

        view.findViewById(R.id.btnAddP).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TakeScreenShot.Do(getContext(), view, v);

                alertDialog.cancel();

                inputQuantityOnPalet(strShtrihCode, true, refAccept);


            }
        });

        view.findViewById(R.id.btnIgnore).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TakeScreenShot.Do(getContext(), view, v);

                alertDialog.cancel();

            }
        });

        alertDialog.show();

    }
        @Override
    public void onDestroy() {
        super.onDestroy();

        shtrihCodeKeyboard = false;

    }

    final Runnable setFocus = new Runnable() {
        public void run() {

            if (!actvShtrihCode.isFocused()){
                actvShtrihCode.requestFocus();
            }

//            if (actvShtrihCode.isFocused() && !shtrihCodeKeyboard) {
//
//                hideKeyboard();
//
//            }

            h.postDelayed(setFocus, 500);


        }
    };

    protected void getReceiptShtrihs(final String refAccept){

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

                for (int i = 0; i < accept.length(); i++){

                    JSONObject accept_item = httpClient.getItemJSONArray(accept, i);

                    Boolean tested = httpClient.getBooleanFromJSON(accept_item, "Tested");
                    Boolean added = httpClient.getBooleanFromJSON(accept_item, "Added");
                    String date = httpClient.getStringFromJSON(accept_item, "Date");
                    String shtrih_code = httpClient.getStringFromJSON(accept_item, "ShtrihCode");
                    Double quantity = inputQuantity ? httpClient.getDoubleFromJSON(accept_item, "Quantity") : null;

                    if (tested){

                        scanned.add(new ScannedShtrihCode(shtrih_code, date, added, quantity));
                    } else {

                        toScan.add(shtrih_code);

                        receiptRefCodes.add(new ReceiptRefCode(refAccept, shtrih_code));

                    }


                }

                adapterScanned.notifyDataSetChanged();
                adapterToScan.notifyDataSetChanged();

                updatePercent();

                if (mode != null && mode.equals("route")){

                    Integer index = receipts.indexOf(refAccept);
                    if (index + 1 < receipts.size()) {

                        getReceiptShtrihs(receipts.get(index + 1));

                    }

                }

            }

        });

    }

    protected void getShtrihs(){

        receiptRefCodes.clear();

        toScan.clear();
        scanned.clear();

        if (mode != null && mode.equals("route")){

            getReceiptShtrihs(receipts.get(0));

        }
        else {
            
            getReceiptShtrihs(ref);
            
        }
    }

    private void updatePercent() {
        Integer scannedQ = scanned.size();
        Integer toAcceptQ = toScan.size() + scannedQ;

        Integer procent = toAcceptQ == 0 ? 100 : scannedQ * 100 / toAcceptQ;

        if (toScan.size() == 0){
            btnClose.setVisibility(View.VISIBLE);
        }

        progressTextView.setText(scannedQ.toString() + " из " + toAcceptQ.toString() + ", " + procent.toString() + "%");
        progressTextView.setMax(toAcceptQ);
        progressTextView.setProgress(scannedQ);
    }

    private void inputQuantityOnPalet(final String strCatName, final boolean added, final String refAccept) {

        androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle("Ввод количества коробок на палете");

        LayoutInflater inflater = getLayoutInflater();

        final View view = inflater.inflate(R.layout.dialog_input_quantity, null);

        ((TextView) view.findViewById(R.id.tvCode)).setText(strCatName);

        Button btnQuantity = view.findViewById(R.id.btnQuantity);

        etQuantity = view.findViewById(R.id.etQuantity);

//        if(quantity == null){

            btnQuantity.setVisibility(View.GONE);
//        }
//        else {
//
//            btnQuantity.setVisibility(View.VISIBLE);
//            btnQuantity.setText("<<  " + quantity.toString());
//        }
//
//        btnQuantity.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                etQuantity.setText(quantity.toString());
//
//                CloseScan(view, strCatName, added, isPalet);
//
//            }
//        });

        alertDialogBuilder.setView(view);
        alertDialogBuilder.setIcon(R.drawable.sklad96);

        quantityDialog = alertDialogBuilder.create();

        view.findViewById(R.id.btnOK).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TakeScreenShot.Do(getContext(), view, v);

                Double boxQuantity = Double.valueOf(((TextView) view.findViewById(R.id.etQuantity)).getText().toString());

                quantityDialog.cancel();

                if (inputQuantity){
                    inputQuantity(strCatName, true, true, boxQuantity, refAccept);
                }
                else {
                    setShtrihs(strCatName, 1.0, true, true, boxQuantity, refAccept);
                }


            }
        });

        quantityDialog.show();

        final Handler h3 = new Handler();
        h3.postDelayed(setFocus3, 500);

    }

    final Runnable setFocus3 = new Runnable() {
        public void run() {

//            etQuantity.requestFocus();
//
//            ((MainWareHouseActivity) getActivity()).imm.showSoftInput(etQuantity, 0, null);


        }
    };

    private void inputQuantity(final String strCatName, final boolean added, final boolean isPalet, final Double boxQuantity, final String refAccept) {

        androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle("Ввод количества");

        LayoutInflater inflater = getLayoutInflater();

        final View view = inflater.inflate(R.layout.dialog_input_quantity, null);

        ((TextView) view.findViewById(R.id.tvCode)).setText((isPalet ? "Палет: " : "") + strCatName + (isPalet ? " (" + boxQuantity.toString() + " шт)" : ""));

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

                TakeScreenShot.Do(getContext(), view, v);

                etQuantity.setText(quantity.toString());

                CloseScan(view, strCatName, added, isPalet, boxQuantity, refAccept);

            }
        });

        alertDialogBuilder.setView(view);
        alertDialogBuilder.setIcon(R.drawable.sklad96);

        quantityDialog = alertDialogBuilder.create();

        view.findViewById(R.id.btnOK).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TakeScreenShot.Do(getContext(), view, v);

                CloseScan(view, strCatName, added, isPalet, boxQuantity, refAccept);

            }
        });

        quantityDialog.show();

        final Handler h2 = new Handler();
        h2.postDelayed(setFocus2, 500);

    }

    private void CloseScan(View view, String strCatName, boolean added, boolean isPalet, Double boxQuantity, String refAccept) {
        quantityDialog.cancel();

        quantity = Double.valueOf(((TextView) view.findViewById(R.id.etQuantity)).getText().toString());

        setShtrihs(strCatName, quantity, added, isPalet, boxQuantity, refAccept);
    }

    final Runnable setFocus2 = new Runnable() {
        public void run() {

//            etQuantity.requestFocus();
//
//            ((MainWareHouseActivity) getActivity()).imm.showSoftInput(etQuantity, 0, null);


        }
    };

    protected void setShtrihs(final String shtrihCode, final Double quantity, final boolean added, final boolean isPalet, Double boxQuantity, String refAccept){

        if (added && inputDelivererAddress){

            if(!inputDelivererAddressOnEmptyReceipt){

                if (receivers.size() < 2) {

                    addER(shtrihCode, quantity, added, isPalet, boxQuantity, refAccept, receivers.size() == 0 ? refReceiver : receivers.get(0).ref);
                }

                else {

                    selectReceiver(shtrihCode, quantity, added, isPalet, boxQuantity, refAccept);

                }

            }
            else {
                Bundle bundle = new Bundle();
                bundle.putString("refContractor", refContractor);
                bundle.putString("refAccept", refAccept);
                bundle.putString("shtrihCode", shtrihCode);
                bundle.putString("refReceiver", refReceiver);
                bundle.putString("receiver", receiver);
                bundle.putBoolean("isPalet", isPalet);
                bundle.putBoolean("added", added);
                bundle.putDouble("boxQuantity", boxQuantity);
                bundle.putInt("back", R.id.scanFragment);

                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.addressFragment, bundle);
            }
        }
        else {

            final HttpClient httpClient = new HttpClient(getContext());
            httpClient.addParam("refAccept", refAccept);
            httpClient.addParam("shtrihCode", shtrihCode);
            httpClient.addParam("isPalet", isPalet);
            httpClient.addParam("boxQuantity", boxQuantity);

            httpClient.postProc("setAcceptShtrih", new HttpRequestInterface() {
                @Override
                public void setProgressVisibility(int visibility) {
                    progressBar.setVisibility(visibility);
                }

                @Override
                public void processResponse(JSONObject response) {

                    if (httpClient.getBooleanFromJSON(response, "Success")) {

                        scanned.add(0, new ScannedShtrihCode(shtrihCode, StrDateTime.dateToStr(new Date()), added, quantity, isPalet));
                        adapterScanned.notifyDataSetChanged();

                        updatePercent();

                        //&& httpClient.getBooleanFromJSON(response, "IsNew")
                        if (inputQuantity) {

                            setQuantityToShtrih(shtrihCode, quantity);

                        } else {

                            if (update_scan) {

                                getShtrihs();
                            }

                        }

                    }
                    ;

                }
            });
        }
    }

    private void addER(String shtrihCode, Double quantity, boolean added, boolean isPalet, Double boxQuantity, String refAccept, String refReceiver) {

        addEmptyReceipt(refAccept, shtrihCode, isPalet, boxQuantity, refReceiver);

        scanned.add(0, new ScannedShtrihCode(shtrihCode, StrDateTime.dateToStr(new Date()), added, quantity, isPalet));
        adapterScanned.notifyDataSetChanged();

        updatePercent();

        if (update_scan) {

            getShtrihs();
        }
    }

    private void selectReceiver(final String shtrihCode, final Double quantity, final boolean added, final boolean isPalet, final Double boxQuantity, final String refAccept) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle("Выбрать получателя");

        alertDialogBuilder.setIcon(R.drawable.sklad96);

        ArrayList<String> receiversDescriptions = new ArrayList<>();

        for (int i = 0; i < receivers.size(); i++) {

            receiversDescriptions.add(receivers.get(i).desc);

        }

        String[] items = receiversDescriptions.toArray(new String[0]);
        alertDialogBuilder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                addER(shtrihCode, quantity, added, isPalet, boxQuantity, refAccept, receivers.get(which).ref);

            }
        });

        final AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();


    }



    private void addEmptyReceipt(String refAccept, String shtrihCode, Boolean isPalet, Double boxQuantity, String ref_receiver) {

        final HttpClient httpClient = new HttpClient(getContext());
        httpClient.addParam("refAccept", refAccept);
        httpClient.addParam("shtrihCode", shtrihCode);
        httpClient.addParam("isPalet", isPalet);
        httpClient.addParam("boxQuantity", boxQuantity);
        httpClient.addParam("refReceiver", ref_receiver);

        httpClient.postProc("setReceiptShtrihAddress", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {
                progressBar.setVisibility(visibility);
            }

            @Override
            public void processResponse(JSONObject response) {

                if (httpClient.getBooleanFromJSON(response, "Success")) {

//                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment).popBackStack(back, false);

//                    scanned.add(0, new ScannedShtrihCode(shtrihCode, StrDateTime.dateToStr(new Date()), added, quantity, isPalet));
//                    adapterScanned.notifyDataSetChanged();
//
//                    updatePercent();
//
//                    if (inputQuantity && httpClient.getBooleanFromJSON(response, "IsNew")) {
//
//                        setQuantityToShtrih(shtrihCode, quantity);
//
//                    } else {
//
//                        if (update_scan) {
//
//                            getShtrihs();
//                        }
//
//                    }

                }

            }
        });




    }

    private void scanShtrihCode(String strCatName, Double quantity) {

        if (!sendingInProgress){

            scanned.add(0, new ScannedShtrihCode(strCatName, "", false, quantity));
            adapterScanned.notifyDataSetChanged();

            setQuantityToShtrih(strCatName, quantity);

        }
    }

    private void setQuantityToShtrih(final String shtrihcode, final Double quantity) {

        final HttpClient httpClient = new HttpClient(getContext());
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

                    if (update_scan){

                        getShtrihs();
                    }
                    else {

                        for (ScannedShtrihCode scannedShtrihCode: scanned) {

                            if (scannedShtrihCode.shtrihCode.equals(shtrihcode)){
                                scannedShtrihCode.quantity = quantity;
                            }

                        }

                        adapterScanned.notifyDataSetChanged();

                        updatePercent();


                    }


                };

            }
        });



    }


}