package com.example.uventawh;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
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
 * Use the {@link WhatPlacedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WhatPlacedFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public WhatPlacedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WhatPlacedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WhatPlacedFragment newInstance(String param1, String param2) {
        WhatPlacedFragment fragment = new WhatPlacedFragment();
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

    private DelayAutoCompleteTextView actvShtrihCode;
    private InputMethodManager imm;
    private boolean shtrihCodeKeyboard = false;
    private RefDesc refDesc;
    private TextView tvHeader;
    private List<ScanCode> scanCodes = new ArrayList<>();
    private DataArrayAdapter dataArrayAdapter;
    private String mode;
    private Button btnPlaceToCell;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_what_placed, container, false);

        mode = "";
        Bundle bundle = getArguments();
        if (bundle!=null){

            mode = bundle.getString("mode");
        }

        tvHeader = root.findViewById(R.id.tvHeader);

        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        btnPlaceToCell = root.findViewById(R.id.btnPlaceToCell);
        btnPlaceToCell.setVisibility(View.GONE);
        btnPlaceToCell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        if (mode.equals("placementContainer")){

            btnPlaceToCell.setVisibility(View.VISIBLE);
        }

        setActvShtrihCode(root);


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

        String[] from = new String[] {"scanCode"};
        int[] to = new int[] { R.id.tvScanCode };

        dataArrayAdapter = new DataArrayAdapter(getContext(), (ArrayList) scanCodes, from, to, R.layout.item_scan_code, R.id.llMain);

        ((RecyclerView) root.findViewById(R.id.rvList)).setAdapter(dataArrayAdapter);

        actvShtrihCode.requestFocus();

        imm.hideSoftInputFromWindow(actvShtrihCode.getWindowToken(), 0);

        return root;
    }

    private void setActvShtrihCode(View root) {

        actvShtrihCode = root.findViewById(R.id.actvShtrihCode);
        actvShtrihCode.setThreshold(3);

        actvShtrihCode.setAdapter(new HttpAutoCompleteAdapter(getContext(), "getCellsByFilter", new HttpAutoCompleteAdapter.OnProcessResponseListener() {
            @Override
            public void OnProcessResponse(HttpClientSync httpClientSync, List<RefDesc> refDescs, JSONObject response) {

                JSONArray cells = httpClientSync.getJsonArrayFromJsonObject(response, "Cells");

                for (int i = 0; i < cells.length(); i++) {

                    JSONObject cell = httpClientSync.getItemJSONArray(cells, i);

                    String ref = httpClientSync.getStringFromJSON(cell, "Ref");
                    String description = httpClientSync.getStringFromJSON(cell, "Description");

                    refDescs.add(new RefDesc(ref, description));

                }

            }
        }));

        actvShtrihCode.setLoadingIndicator((ProgressBar) root.findViewById(R.id.progress_bar0));

        actvShtrihCode.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                refDesc = (RefDesc) adapterView.getItemAtPosition(position);

                actvShtrihCode.setText("");

                tvHeader.setText("Ячейка: " + refDesc.desc);

                btnPlaceToCell.setText("Поместить в ячейку " + refDesc.desc);

                shtrihCodeKeyboard = false;

                imm.hideSoftInputFromWindow(actvShtrihCode.getWindowToken(), 0);

                getPlacedScanCodes(refDesc.ref, refDesc.desc);

            }
        });

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

                                                    getCellByName(strCatName);

                                                    return true;
                                                }
                                                return false;
                                            }
                                        }
        );
    }

    private void getCellByName(final String description) {

        final HttpClient httpClient = new HttpClient(getContext());
        httpClient.addParam("description", description);

        httpClient.postForResult("getCellByName", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {

            }

            @Override
            public void processResponse(JSONObject response) {

                getPlacedScanCodes(httpClient.getStringFromJSON(response, "Ref"), description);

            }
        });

    }

    private void getPlacedScanCodes(String ref, final String desc) {

        final HttpClient httpClient = new HttpClient(getContext());
        httpClient.addParam("ref", ref);

        httpClient.postForResult("getPlacedScanCodes", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {

            }

            @Override
            public void processResponse(JSONObject response) {

                scanCodes.clear();

                JSONArray goods = httpClient.getJsonArrayFromJsonObject(response, "Goods");

                for (int i = 0; i < goods.length(); i++) {

                    JSONObject good = httpClient.getItemJSONArray(goods, i);
                    scanCodes.add(new ScanCode(httpClient.getStringFromJSON(good, "Description")));

                }

                dataArrayAdapter.notifyDataSetChanged();

                tvHeader.setText("Ячейка: " + desc + ", мест: " + scanCodes.size());

            }
        });

    }
}