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

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InventarizationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InventarizationFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public InventarizationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InventarizationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InventarizationFragment newInstance(String param1, String param2) {
        InventarizationFragment fragment = new InventarizationFragment();
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
    private String refRoute, ref, description, shtrihCode = "",
            ref_sender = emptyRef, description_sender = "",
            ref_receiver = emptyRef, description_receiver = "";

    List<InventarizationItem> items = new ArrayList<>();

    DelayAutoCompleteTextView actvShtrihCode, actvSender, actvReceiver, actvNumber;

    DataInventarizationAdapter adapter;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    LinearLayout linearLayout;
    ConstraintLayout ccFilter;
    Button btnFilter;
    Boolean isFilter;

    private boolean shtrihCodeKeyboard = false;

    private InputMethodManager imm;

    private static final int
            REQUEST_SELECT_SENDER_ADDRESS = 0,
            REQUEST_SELECT_RECEIVER_ADDRESS = 1;

    private Handler h;

    private EditText etFilter;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_inventarization, container, false);

        Bundle intent = getArguments();
        refRoute = ""; //intent.getString("refRoute");
        ref = emptyRef; // intent.getString("ref");
        description = ""; // intent.getString("description");

        ((TextView) root.findViewById(R.id.tvHeader)).setText(description + ": Заказы на отгрузку");

        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        progressBar = root.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

//        setStatusCheckBoxes(root);

        etFilter = root.findViewById(R.id.etFilter);
        etFilter.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {

                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {

                    String strCatName = etFilter.getText().toString();

//                    etFilter.setText("");

                    imm.hideSoftInputFromWindow(etFilter.getWindowToken(), 0);

                    update(strCatName);

                    return true;
                }

                return false;
            }
        });

        root.findViewById(R.id.btnClear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                etFilter.setText("");
                update(etFilter.getText().toString());

            }
        });


        ccFilter = root.findViewById(R.id.ccFilter);
        ccFilter.setVisibility(View.GONE);
        isFilter = false;

        btnFilter = root.findViewById(R.id.btnFilter);

        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                isFilter = !isFilter;

                btnFilter.setText(isFilter ? "Список" : "Фильтр");

                ccFilter.setVisibility(isFilter ? View.VISIBLE : View.GONE);

                if (!isFilter)
                    update("");
            }
        });

        btnFilter.setVisibility(View.GONE);

//        setTabHost(root);

        adapter = new DataInventarizationAdapter(getContext(), items);
        adapter.setOnTaskItemClickListener(new DataInventarizationAdapter.OnTaskItemClickListener() {
            @Override
            public void onTaskItemClick(InventarizationItem taskItem, View itemView) {

//                if (taskItem.name.equals("Отгрузка")) {
//
////                    setRouteToShipment(taskItem.ref);
//
//                    if(taskItem.status.equals("Новый")) {
//
////                        askForOrderToPlan(taskItem);
//
//                    }  else{
//
////                        deliveryOrderTest(taskItem);
//
//                    }
//                }
            }
        });

        recyclerView = root.findViewById(R.id.rvList);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState==RecyclerView.SCROLL_STATE_IDLE){

                    if (!recyclerView.canScrollVertically(1)) {

                        update(etFilter.getText().toString());

                    }
                    else if (!recyclerView.canScrollVertically(-1)) {

                        update(etFilter.getText().toString());

                    }
                }

            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        AddressAutoCompleteAdapter aucaAddress = new AddressAutoCompleteAdapter(getContext(),
                new DB(getContext()).getRequestUserProg(), new HttpClientSync(getContext()), ref, "getContractorsByFilter");

        actvSender = root.findViewById(R.id.actvSender);
        actvSender.setThreshold(3);

        actvSender.setAdapter(aucaAddress);
        actvSender.setLoadingIndicator((ProgressBar) root.findViewById(R.id.progress_bar));

        actvSender.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                RefDesc refDesc = (RefDesc) adapterView.getItemAtPosition(position);

                ref = refDesc.ref;
                description_sender = refDesc.desc;

                actvSender.setText(description_sender);

                imm.hideSoftInputFromWindow(actvSender.getWindowToken(), 0);

//                update2();

            }
        });

        root.findViewById(R.id.btnClearContractor).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ref = emptyRef;
                description_sender = "";

                actvSender.setText(description_sender);

            }
        });


        AddressAutoCompleteAdapter aucaNumber = new AddressAutoCompleteAdapter(getContext(),
                new DB(getContext()).getRequestUserProg(), new HttpClientSync(getContext()), ref, "getDeliveryNumbersByFilter");

        actvNumber = root.findViewById(R.id.actvNumber);
        actvNumber.setThreshold(3);

        actvNumber.setAdapter(aucaNumber);
        actvNumber.setLoadingIndicator((ProgressBar) root.findViewById(R.id.progress_bar));

        actvNumber.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                RefDesc refDesc = (RefDesc) adapterView.getItemAtPosition(position);

                shtrihCode = refDesc.desc;

                actvNumber.setText(shtrihCode);

                imm.hideSoftInputFromWindow(actvSender.getWindowToken(), 0);

//                update2();

            }
        });

        root.findViewById(R.id.btnClearNumber).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                shtrihCode = "";

                actvNumber.setText(shtrihCode);

            }
        });

        update("");



        return root;
    }

    protected void update(String sFilter){

        adapter.setFilter(sFilter);

        final HttpClient httpClient = new HttpClient(getContext());
        httpClient.addParam("filter", sFilter);

        httpClient.postProc("getInventarizationByStringFilter", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {

            }

            @Override
            public void processResponse(JSONObject response) {

                items.clear();

                JSONArray deliveryOrdersJSON = httpClient.getJsonArrayFromJsonObject(response, "InventarizationByStringFilter");

                for (int k = 0; k < deliveryOrdersJSON.length(); k++) {

                    JSONObject deliveryOrderJSON = httpClient.getItemJSONArray(deliveryOrdersJSON, k);

                    items.add(InventarizationItem.extractFromJSON(deliveryOrderJSON, httpClient));
                }

                adapter.notifyDataSetChanged();

            }
        });





    }


}