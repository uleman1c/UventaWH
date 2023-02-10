package com.example.uventawh;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ContractorsListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContractorsListFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ContractorsListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ContractorsListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ContractorsListFragment newInstance(String param1, String param2) {
        ContractorsListFragment fragment = new ContractorsListFragment();
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

    private DataContractorAdapter adapter;
    private LinearLayout linearLayout;
    private ProgressBar progressBar;

    private int next, back = 0;
    private List<Contractor> contractors = new ArrayList<>(), contractorsButtons = new ArrayList<>();

    Bundle bundle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View root = inflater.inflate(R.layout.fragment_contractors_list, container, false);

        bundle = getArguments();

        if (bundle == null){
            bundle = new Bundle();
        }
        else {

            String header = bundle.getString("header");

            if (header != null){

                ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
                actionBar.setTitle(header + " - " + actionBar.getTitle());

            }

            next = bundle.getInt("next");
            back = bundle.getInt("back", 0);

        }

        NavigationView navigationView = getActivity().findViewById(R.id.nav_view);

        if (navigationView.getCheckedItem().getItemId() == R.id.nav_placement_menu)
        {
            next = R.id.nav_cell_placement;
        }
        else if (navigationView.getCheckedItem().getItemId() == R.id.nav_marking)
        {
            next = R.id.createCodeFragment;
        }

        linearLayout = root.findViewById(R.id.linearLayout);

        progressBar = root.findViewById(R.id.progressBar);

        adapter = new DataContractorAdapter(getContext(), contractors);


        adapter.setOnStringClickListener(new DataContractorAdapter.OnStringClickListener(){
            @Override
            public void onStringClick(Contractor str, View itemView) {

                TakeScreenShot.Do(getContext(), root, itemView);

                doSelect(str);

            }
        });

        RecyclerView recyclerViewToScan = root.findViewById(R.id.rcvList);
        recyclerViewToScan.setAdapter(adapter);

        root.findViewById(R.id.ibKari).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TakeScreenShot.Do(getContext(), root, v);

                doSelect(contractorsButtons.get(0));

            }
        });

        root.findViewById(R.id.ibMfg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TakeScreenShot.Do(getContext(), root, v);

                doSelect(contractorsButtons.get(1));

            }
        });

        root.findViewById(R.id.ibAtp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TakeScreenShot.Do(getContext(), root, v);

                doSelect(contractorsButtons.get(2));

            }
        });

        updateButtons();

        update();

        return root;
    }

    private void doSelect(Contractor str) {
        bundle.putString("ref", str.ref);
        bundle.putString("description", str.description);
        bundle.putBoolean("inputQuantity", str.inputQuantity);
        bundle.putString("contractorRef", str.ref);
        bundle.putString("contractorDescription", str.description);
        bundle.putBoolean("contractorInputQuantity", str.inputQuantity);
        bundle.putBoolean("contractorAddRoute", str.addRoute);

        if (back != 0){

            Navigation.findNavController(getActivity(), R.id.nav_host_fragment).popBackStack(back, false);
        }

        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(next, bundle);
    }

    protected void updateButtons(){

        final HttpClient httpClient = new HttpClient(getContext());
        httpClient.postForResult("getContractorsButtons", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {
                progressBar.setVisibility(visibility);
            }

            @Override
            public void processResponse(JSONObject response) {

                JSONArray contractorsJSON = httpClient.getJsonArrayFromJsonObject(response, "ContractorsButtons");

                contractorsButtons.clear();

                for (int i = 0; i < contractorsJSON.length(); i++) {

                    JSONObject contractorsJSON_item = httpClient.getItemJSONArray(contractorsJSON, i);

                    String ref = httpClient.getStringFromJSON(contractorsJSON_item, "Ref");
                    String description = httpClient.getStringFromJSON(contractorsJSON_item, "Description");
                    Boolean inputQuantity = httpClient.getBooleanFromJSON(contractorsJSON_item, "InputQuantity");

                    contractorsButtons.add(new Contractor(ref, description, inputQuantity));
                }

            }
        });


    }

    protected void update(){

        final HttpClient httpClient = new HttpClient(getContext());
        httpClient.postProc("getContractors", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {
                progressBar.setVisibility(visibility);
            }

            @Override
            public void processResponse(JSONObject response) {

                if (httpClient.getStringFromJSON(response, "Name").equals("getContractors")){

                    JSONArray contractorsJSON = httpClient.getJsonArrayFromJsonObject(response, "Contractors");

                    contractors.clear();

                    for (int i = 0; i < contractorsJSON.length(); i++) {

                        JSONObject contractorsJSON_item = httpClient.getItemJSONArray(contractorsJSON, i);

                        String ref = httpClient.getStringFromJSON(contractorsJSON_item, "Ref");
                        String description = httpClient.getStringFromJSON(contractorsJSON_item, "Description");
                        Boolean inputQuantity = httpClient.getBooleanFromJSON(contractorsJSON_item, "InputQuantity");
                        Boolean addRoute = httpClient.getBooleanFromJSON(contractorsJSON_item, "AddRoute");

                        contractors.add(new Contractor(ref, description, inputQuantity, addRoute));
                    }

                    adapter.notifyDataSetChanged();
                }

            }
        });


    }



}