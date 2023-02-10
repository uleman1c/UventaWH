package com.example.uventawh;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateCodeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateCodeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CreateCodeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreateCodeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateCodeFragment newInstance(String param1, String param2) {
        CreateCodeFragment fragment = new CreateCodeFragment();
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

    private String contractorRef, contractorDescription;
    private Boolean contractorInputQuantity;
    DelayAutoCompleteTextView etShtrihCode;
    AutoCompleteAdapter aucaAddress;

    List<ScannedShtrihCode> scanned = new ArrayList<>();

    ScannedShtrihCodeAdapter adapterScanned;

    ProgressBar progressBar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_create_code, container, false);

        contractorRef = getArguments().getString("ref");
        contractorDescription = getArguments().getString("description");
        contractorInputQuantity = getArguments().getBoolean("inputQuantity");

        progressBar = root.findViewById(R.id.progressBar);

        ((TextView)root.findViewById(R.id.tvContractor)).setText(contractorDescription);

        etShtrihCode = root.findViewById(R.id.etShtrihCode);
        etShtrihCode.setThreshold(3);

        aucaAddress = new AutoCompleteAdapter(getContext(), "getOrdersAP");
        aucaAddress.addParam("contractorRef", contractorRef);

        etShtrihCode.setAdapter(aucaAddress);
        etShtrihCode.setLoadingIndicator((ProgressBar) root.findViewById(R.id.progress_bar));

        etShtrihCode.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                RefDesc refDesc = (RefDesc) adapterView.getItemAtPosition(position);

                etShtrihCode.setText(refDesc.ref);

                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

                imm.hideSoftInputFromWindow(etShtrihCode.getWindowToken(), 0);

                setShtrihs(refDesc.ref);

            }
        });

        RecyclerView recyclerViewScanned = root.findViewById(R.id.scanned);

        adapterScanned = new ScannedShtrihCodeAdapter(getContext(), scanned);
        recyclerViewScanned.setAdapter(adapterScanned);



        etShtrihCode.requestFocus();

        Handler h = new Handler();
        h.postDelayed(showInput, 500);

        return root;
    }

    final Runnable showInput = new Runnable() {
        public void run() {

            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

            imm.showSoftInput(etShtrihCode, 0, null);


        }
    };

    protected void setShtrihs(final String shtrihcode){

        final HttpClient httpClient = new HttpClient(getContext());
        httpClient.addParam("contractorRef", contractorRef);
        httpClient.addParam("shtrihCode", shtrihcode);

        httpClient.postForResult("setAddToMark", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {
                progressBar.setVisibility(visibility);
            }

            @Override
            public void processResponse(JSONObject response) {

                if (httpClient.getBooleanFromJSON(response, "Success")){

                    scanned.add(0, new ScannedShtrihCode(shtrihcode, "", true));
                    adapterScanned.notifyDataSetChanged();

                    etShtrihCode.setText("");

                    Handler h = new Handler();
                    h.postDelayed(showInput, 500);

                };

            }
        });

    }



}