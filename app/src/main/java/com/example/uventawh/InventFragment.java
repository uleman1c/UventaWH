package com.example.uventawh;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

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
 * Use the {@link InventFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InventFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public InventFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InventFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InventFragment newInstance(String param1, String param2) {
        InventFragment fragment = new InventFragment();
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

    private DataArrayAdapter adapter;
    private LinearLayout linearLayout;
    private ProgressBar progressBar;

    private int next, back = 0;
    private List<Invent> invents = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View root = inflater.inflate(R.layout.fragment_invent, container, false);


        progressBar = root.findViewById(R.id.progressBar);

//        adapter = new DataContractorAdapter(getContext(), invents);

        String[] from = new String[] {"date", "contractor"};
        int[] to = new int[] { R.id.tvDate, R.id.tvContractor };

        adapter = new DataArrayAdapter(getContext(), (ArrayList) invents, from, to, R.layout.item_invent, R.id.llMain);


        adapter.setOnStringClickListener(new DataArrayAdapter.OnStringClickListener(){
            @Override
            public void onStringClick(Adapterable taskItem) {

                doSelect((Invent) taskItem);

            }

//            @Override
//            public void onStringClick(Contractor str, View itemView) {
//
//                TakeScreenShot.Do(getContext(), root, itemView);
//
//                doSelect(str);
//
//            }
        });

        RecyclerView recyclerViewToScan = root.findViewById(R.id.list);
        recyclerViewToScan.setAdapter(adapter);

        root.findViewById(R.id.fabAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TakeScreenShot.Do(getContext(), root, v);

                    chooseContractor();

            }
        });


        update();

        return root;

    }

    private void chooseContractor() {

        Bundle bundle = new Bundle();
        bundle.putString("inventRef", getString(R.string.emptyRef));
        bundle.putString("inventDate", StrDateTime.dateToStr(new Date()));

        //        bundle.putString("refTransport", refTransport);
//        bundle.putString("descTransport", descTransport);
//        bundle.putString("refDriver", refDriver);
//        bundle.putString("descDriver", descDriver);
//        bundle.putString("refRoute", refRoute);
        bundle.putString("refFilter", getString(R.string.emptyRef));
        bundle.putString("descFilter", "");

            bundle.putString("header", "Инвентаризация");
            bundle.putInt("next", R.id.scanInventFragment);
            bundle.putInt("back", R.id.nav_invent);

            Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.contractorsListFragment, bundle);


    }


    protected void update(){

        final HttpClient httpClient = new HttpClient(getContext());
        httpClient.postProc("getInvents", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {
                progressBar.setVisibility(visibility);
            }

            @Override
            public void processResponse(JSONObject response) {

                if (httpClient.getStringFromJSON(response, "Name").equals("getInvents")){

                    JSONArray contractorsJSON = httpClient.getJsonArrayFromJsonObject(response, "Invents");

                    invents.clear();

                    for (int i = 0; i < contractorsJSON.length(); i++) {

                        JSONObject contractorsJSON_item = httpClient.getItemJSONArray(contractorsJSON, i);

                        String ref = httpClient.getStringFromJSON(contractorsJSON_item, "Ref");
                        String date = httpClient.getStringFromJSON(contractorsJSON_item, "Date");
                        String contractor = httpClient.getStringFromJSON(contractorsJSON_item, "Contractor");
                        String contractorRef = httpClient.getStringFromJSON(contractorsJSON_item, "ContractorRef");

                        invents.add(new Invent(ref, date, contractor, contractorRef));
                    }

                    adapter.notifyDataSetChanged();
                }

            }
        });


    }


    private void doSelect(final Invent taskItem) {

        final HttpClient httpClient = new HttpClient(getContext());
        httpClient.addParam("ref", taskItem.contractorRef);

        httpClient.postForResult("getContractorSettings", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {
                progressBar.setVisibility(visibility);
            }

            @Override
            public void processResponse(JSONObject response) {

                if (httpClient.getBooleanFromJSON(response, "Success")) {

                    Boolean inputSenderAddress = httpClient.getBooleanFromJSON(response, "InputSenderAddress");
                    Boolean inputDelivererAddress = httpClient.getBooleanFromJSON(response, "InputDelivererAddress");
                    Boolean inputQuantity = httpClient.getBooleanFromJSON(response, "InputQuantity");

                    Bundle bundle = new Bundle();

                    bundle.putString("inventRef", taskItem.ref);
                    bundle.putString("inventDate", taskItem.date);
                    bundle.putString("contractorRef", taskItem.contractorRef);
                    bundle.putString("contractorDescription", taskItem.contractor);
                    bundle.putBoolean("contractorInputQuantity", inputQuantity);

                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.scanInventFragment, bundle);

                }
            }

        });


    }
}