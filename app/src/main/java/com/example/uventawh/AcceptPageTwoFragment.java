package com.example.uventawh;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AcceptPageTwoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AcceptPageTwoFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AcceptPageTwoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AcceptPageTwoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AcceptPageTwoFragment newInstance(String param1, String param2) {
        AcceptPageTwoFragment fragment = new AcceptPageTwoFragment();
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

    private String emptyRef = "00000000-0000-0000-0000-000000000000";

    private String ref, description,
            ref_sender = emptyRef, description_sender = "<выберите>",
            ref_receiver = emptyRef, description_receiver = "<выберите>",
            refTransport, descTransport, refDriver, descDriver, numDocument, refRoute;

    private LinearLayout llMain;
    private ProgressBar progressBar;

    private Button btnSave;
    private Integer count = 1, scanned = 0, added = 0;
    private TextView tvShtrihCodeCount, tvSenderDescription, tvReceiverDescription, tvAdded, tvScanned;
    private EditText etShtrihCode;

    private Boolean dateIsSet = false;

    private List<String> shtrihCodes = new ArrayList<>();
    private Handler h;

    private String[] cargos = {"Товар", "Переброска", "Оборудование"};

    TextView tvSendDate, tvCargoDescription;
    Calendar dateAndTime = Calendar.getInstance();

    List<ScannedShtrihCode> scannedList = new ArrayList<>();
    ScannedShtrihCodeAdapter adapterScanned;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_accept_page_two, container, false);

        llMain = root.findViewById(R.id.llMain);

        tvSendDate = root.findViewById(R.id.tvSendDate);
        tvCargoDescription = root.findViewById(R.id.tvCargoDescription);

        progressBar = root.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        Bundle bundle = getArguments();

        ref = bundle.getString("ref");
        description = bundle.getString("description");
        ref_sender = bundle.getString("ref_sender");
        description_sender = bundle.getString("description_sender");
        ref_receiver = bundle.getString("ref_receiver");
        description_receiver = bundle.getString("description_receiver");

        refRoute = bundle.getString("refRoute");
        refTransport = bundle.getString("refTransport");
        descTransport = bundle.getString("descTransport");
        refDriver = bundle.getString("refDriver");
        descDriver = bundle.getString("descDriver");
        numDocument = bundle.getString("numDocument");

        ((TextView)root.findViewById(R.id.tvContractor)).setText(description);
        ((TextView)root.findViewById(R.id.tvTransport)).setText(descTransport);
        ((TextView)root.findViewById(R.id.tvDriver)).setText(descDriver);
        ((TextView)root.findViewById(R.id.tvNumberDocument)).setText(numDocument);

        dateAndTime.setTimeInMillis(bundle.getLong("sendDate", 0));
        tvCargoDescription.setText(bundle.getString("cargo"));

        dateIsSet = bundle.getBoolean("dateIsSet", false);

        if (dateIsSet){

            tvSendDate.setText(new SimpleDateFormat("dd MMMM yyyy").format(dateAndTime.getTime()));
        }

        tvShtrihCodeCount = root.findViewById(R.id.tvShtrihCodeCount);

        tvSenderDescription = root.findViewById(R.id.tvSenderDescription);
        tvSenderDescription.setText(description_sender);

        tvReceiverDescription = root.findViewById(R.id.tvReceiverDescription);
        tvReceiverDescription.setText(description_receiver);

        btnSave = root.findViewById(R.id.btnSave);

        etShtrihCode = root.findViewById(R.id.etShtrihCode);
        etShtrihCode.setOnKeyListener(new View.OnKeyListener()
                                      {
                                          public boolean onKey(View v, int keyCode, KeyEvent event)
                                          {
                                              if(event.getAction() == KeyEvent.ACTION_DOWN &&
                                                      (keyCode == KeyEvent.KEYCODE_ENTER))
                                              {
                                                  String strShtrihCode = etShtrihCode.getText().toString();

                                                  etShtrihCode.setText("");

                                                  if (shtrihCodes.indexOf(strShtrihCode) == -1) {

                                                      shtrihCodes.add(strShtrihCode);

                                                      tvScanned.setText("Сканировано: " + String.valueOf(shtrihCodes.size()));

                                                      btnSave.setText("Сохранить: " + String.valueOf(added + shtrihCodes.size()));

                                                      scannedList.add(0, new ScannedShtrihCode(strShtrihCode, StrDateTime.dateToStr(new Date()), true));
                                                      adapterScanned.notifyDataSetChanged();

                                                  }
                                                  else {

                                                      askForRepeatCode(strShtrihCode);

                                                  }
//                                              scanShtrihCode(strCatName);

                                                  return true;
                                              }
                                              return false;
                                          }
                                      }
        );

        tvAdded = root.findViewById(R.id.tvAdded);
        tvScanned = root.findViewById(R.id.tvScanned);

        ((TextView) root.findViewById(R.id.tvHeader)).setText("Приемка: " + description);

        root.findViewById(R.id.btnDec).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (count > 1) {

                    count = count - 1;

                    tvShtrihCodeCount.setText(count.toString());

                }

            }
        });

        root.findViewById(R.id.btnInc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                count = count + 1;

                tvShtrihCodeCount.setText(count.toString());

            }
        });

        root.findViewById(R.id.btnGenerate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                added = added + count;

                tvAdded.setText("Генерировано: " + String.valueOf(added));

                btnSave.setText("Сохранить: " + String.valueOf(added + shtrihCodes.size()));

                count = 1;

                tvShtrihCodeCount.setText(count.toString());

            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendGroupAccept(ref, ref_sender, ref_receiver, count);

            }

        });

        RecyclerView recyclerViewScanned = (RecyclerView) root.findViewById(R.id.scanned);

        adapterScanned = new ScannedShtrihCodeAdapter(getContext(), scannedList);
        recyclerViewScanned.setAdapter(adapterScanned);

        adapterScanned.setOnStringClickListener(new ScannedShtrihCodeAdapter.OnStringClickListener(){
            @Override
            public void onStringClick(ScannedShtrihCode str, View itemView) {

//                askForAddService(str.shtrihCode);

            }
        });

        recyclerViewScanned.setAdapter(adapterScanned);


        h = new Handler();

        Thread t = new Thread(new Runnable() {
            public void run() {
                h.post(setFocus);
            }
        });
        t.start();

        return root;

    }

    final Runnable setFocus = new Runnable() {
        public void run() {

            if (getActivity()  != null) {

                etShtrihCode.requestFocus();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(etShtrihCode.getWindowToken(), 0);

                h.postDelayed(setFocus, 500);
            }
        }
    };

    private void sendGroupAccept(String ref, String ref_sender, String ref_receiver, Integer count){

        HttpClient httpClient = new HttpClient(getContext());
        httpClient.addParam("organization", ref);
        httpClient.addParam("sender", ref_sender == null ? emptyRef : ref_sender);
        httpClient.addParam("receiver", ref_receiver == null ? emptyRef : ref_receiver);
        httpClient.addParam("count", added);
        httpClient.addParam("cargo", tvCargoDescription.getText().toString());
        httpClient.addParam("sendDate", new SimpleDateFormat("yyyyMMdd").format(dateAndTime.getTime()) + "000000");
        httpClient.addParam("dateIsSet", dateIsSet);
        httpClient.addParam("shtrihCodes", new JSONArray(shtrihCodes));
        httpClient.addParam("refTransport", refTransport);
        httpClient.addParam("refDriver", refDriver);
        httpClient.addParam("route", refRoute);
        httpClient.addParam("numDocument", numDocument);

        httpClient.postProc("setAcceptFromRoute", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {

                llMain.setVisibility(visibility == View.VISIBLE ? View.GONE : View.VISIBLE);
                progressBar.setVisibility(visibility);
            }

            @Override
            public void processResponse(JSONObject response) {

//                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).popBackStack(R.id.docInputFragment, false);
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).popBackStack(R.id.docNumberFragment, false);

            }
        });

    }

    private void askForRepeatCode(String strShtrihCode) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle(strShtrihCode + " уже есть. Выбрать");

        alertDialogBuilder.setIcon(R.drawable.sklad96);

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_ask_repeat_shtrihcode, null);

        alertDialogBuilder.setView(view);
        alertDialogBuilder.setIcon(R.drawable.sklad96);

        final AlertDialog alertDialog = alertDialogBuilder.create();

        view.findViewById(R.id.btnGenerate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.cancel();

                added = added + count;

                tvAdded.setText("Генерировано: " + String.valueOf(added));

                btnSave.setText("Сохранить: " + String.valueOf(added + shtrihCodes.size()));

                count = 1;

                tvShtrihCodeCount.setText(count.toString());

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


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {

            if (resultCode == RESULT_OK)  {

                ref_sender = data.getStringExtra("ref");
                description_sender = data.getStringExtra("description");

                tvSenderDescription.setText(description_sender);

            }
        }
        else if(requestCode == 2){

            if (resultCode == RESULT_OK) {

                ref_receiver = data.getStringExtra("ref");
                description_receiver = data.getStringExtra("description");

                tvReceiverDescription.setText(description_receiver);

            }
        }


    }
}
