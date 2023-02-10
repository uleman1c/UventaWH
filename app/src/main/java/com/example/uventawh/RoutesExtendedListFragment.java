package com.example.uventawh;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RoutesExtendedListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RoutesExtendedListFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RoutesExtendedListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RoutesExtendedListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RoutesExtendedListFragment newInstance(String param1, String param2) {
        RoutesExtendedListFragment fragment = new RoutesExtendedListFragment();
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

    private DataRoutesAdapter adapter;
    private LinearLayout linearLayout;
    private ProgressBar progressBar;

    private List<Route> routes = new ArrayList<>(), routesForShow = new ArrayList<>();

    private String refTransport, descTransport, refDriver, descDriver, refContractor, descContractor;

    private Boolean isReceipt = true, addRoute = false;
    private EditText etFilter;

    static int
            REQUEST_CODE_FILTER = 0,
            REQUEST_ADD_TRANSPORT = 1,
            REQUEST_EDIT_ROUTE = 2;

    private int next;

    private InputMethodManager imm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View root = inflater.inflate(R.layout.fragment_routes_extended_list, container, false);
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();

        NavigationView navigationView = getActivity().findViewById(R.id.nav_view);

        imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        View headerView = navigationView.getHeaderView(0);

        MenuItem checkedItem = navigationView.getCheckedItem();
        actionBar.setTitle(checkedItem.getTitle() + " - " + actionBar.getTitle());

        if (checkedItem.getItemId() == R.id.nav_placement)
        {
            next = R.id.nav_cell_placement;
        }
        else if (checkedItem.getItemId() == R.id.nav_marking)
        {
            next = R.id.createCodeFragment;
        }


        etFilter = root.findViewById(R.id.etFilter);
        etFilter.setText("");

        etFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                setFilter(etFilter.getText().toString());

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etFilter.setOnKeyListener(new View.OnKeyListener()
                                      {
                                          public boolean onKey(View v, int keyCode, KeyEvent event)
                                          {
                                              if(event.getAction() == KeyEvent.ACTION_DOWN &&
                                                      (keyCode == KeyEvent.KEYCODE_ENTER))
                                              {
                                                  imm.hideSoftInputFromWindow(etFilter.getWindowToken(), 0);

                                                  setFilter(etFilter.getText().toString());

                                                  return true;
                                              }
                                              return false;
                                          }
                                      }
        );

        root.findViewById(R.id.btnClearFilter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etFilter.setText("");
                setFilter("");
            }
        });

        linearLayout = root.findViewById(R.id.linearLayout);

        progressBar = root.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        Bundle bundle = getArguments();
        if (bundle != null){

            refContractor = bundle.getString("ref");
            descContractor = bundle.getString("description");
            refTransport = bundle.getString("refTransport");
            descTransport = bundle.getString("descTransport");
            refDriver = bundle.getString("refDriver");
            descDriver = bundle.getString("descDriver");
            isReceipt = bundle.getBoolean("isReceipt");
            addRoute = bundle.getBoolean("contractorAddRoute");
        }

        root.findViewById(R.id.fabAdd).setVisibility(addRoute ? View.VISIBLE : View.GONE);

        if (refTransport != null){
//            ((TextView)root.findViewById(R.id.tvHeader)).setText("Выбор рейса, водитель: " + descDriver + ", транспорт: " + descTransport);
        }

        adapter = new DataRoutesAdapter(getContext(), routesForShow);

        adapter.setOnStringClickListener(new DataRoutesAdapter.OnStringClickListener(){
            @Override
            public void onStringClick(Route str, View itemView) {

                TakeScreenShot.Do(getContext(), root, itemView);

                editRoute(str.ref);

            }
        });

        root.findViewById(R.id.fabFilter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent("com.example.uventawh.action.filteractivity");

                JSONArray params = new JSONArray();

                JSONObject request = new JSONObject();
                try {
                    request.put("name", "driver");
                    request.put("description", "Водитель");
                    request.put("value", "");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                params.put(request);

                intent.putExtra("filter", params.toString());

                startActivityForResult(intent, REQUEST_CODE_FILTER);

            }
        });

        root.findViewById(R.id.fabAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (refDriver != null){
                    addRoute(refTransport, refDriver);

                } else {

                    Bundle bundle = new Bundle();
//                    bundle.putString("ref", ref);
//                    bundle.putString("refTransport", refTransport);
//                    bundle.putString("refDriver", refDriver);
//                    bundle.putString("descTransport", descTransport);
//                    bundle.putString("descDriver", descDriver);

                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.nav_truck_arrival, bundle);

//
//
//                    Intent intent = new Intent(getContext(), TrasportListActivity.class);
//                    intent.putExtra("ref", "");
//                    intent.putExtra("description", "");
//
//                    startActivityForResult(intent, REQUEST_ADD_TRANSPORT);

                }

            }
        });

        root.findViewById(R.id.fabAddShtrih).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TakeScreenShot.Do(getContext(), root, v);

                Bundle bundle = new Bundle();
                bundle.putBoolean("isReceipt", isReceipt);

                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.nav_scanRouteListFragment, bundle);

            }
        });


        RecyclerView recyclerViewToScan = (RecyclerView) root.findViewById(R.id.rcvList);
        recyclerViewToScan.setAdapter(adapter);

        update(refContractor);






        return root;
    }

    private void editRoute(String ref) {

        Bundle bundle = new Bundle();
        bundle.putString("ref", ref);
        bundle.putString("refTransport", refTransport);
        bundle.putString("refDriver", refDriver);
        bundle.putString("descTransport", descTransport);
        bundle.putString("descDriver", descDriver);
        bundle.putBoolean("isReceipt", isReceipt);

        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.editRouteFragment, bundle);

    }

    private void addRoute(String refTransport, String refDriver) {

        final HttpClient httpClient = new HttpClient(getContext());
        httpClient.addParam("refTransport", refTransport);
        httpClient.addParam("refDriver", refDriver);

        httpClient.postProc("addRoute", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {
                progressBar.setVisibility(visibility);
            }

            @Override
            public void processResponse(JSONObject response) {

                if(httpClient.getBooleanFromJSON(response, "Success")){

                    editRoute(httpClient.getStringFromJSON(response, "RefRoute"));

                }
            }
        });

    }

    private void setFilter(String value) {

        routesForShow.clear();
        for (Route route:routes){

            Boolean foundContractor = false;
            for (ContractorRoute contractorRoute:route.contractorRoutes){
                if (value.isEmpty() || contractorRoute.contractor.description.toLowerCase().contains(value.toLowerCase())){
                    foundContractor = true;
                }
            }

            if (value.isEmpty() || route.driver.toLowerCase().contains(value.toLowerCase())
                || route.transport.toLowerCase().contains(value.toLowerCase())
                || foundContractor){
                routesForShow.add(route);
            }

        }

        adapter.setFilter(value);
        adapter.notifyDataSetChanged();


    }

    private void chooseContractor(final Route str) {

        if (str.contractorRoutes.size() == 1){

            finishChooseContractor(str, 0);

        }
        else {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
            alertDialogBuilder.setTitle("Выбрать контрагента");

            alertDialogBuilder.setIcon(R.drawable.sklad);

            ArrayList<String> contractor_items = new ArrayList<>();

            for (ContractorRoute contractorRoute : str.contractorRoutes) {
                contractor_items.add(contractorRoute.contractor.description);
            }

            alertDialogBuilder.setItems(contractor_items.toArray(new String[contractor_items.size()]), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    finishChooseContractor(str, which);

                }
            });

            final AlertDialog alertDialog = alertDialogBuilder.create();

            alertDialog.show();

        }

    }

    private void finishChooseContractor(Route str, int which) {

        ContractorRoute choosedContractor = str.contractorRoutes.get(which);

        Intent intent = new Intent();

        intent.putExtra("ref", str.ref);
        intent.putExtra("dateShipment", str.dateShipment);
        intent.putExtra("driver", str.driver);
        intent.putExtra("transport", str.transport);
//        intent.putExtra("contractorRef", choosedContractor.ref);
//        intent.putExtra("contractorDescription", choosedContractor.description);

//        setResult(RESULT_OK, intent);
//
//        finish();

    }

    protected void update(final String refContractor){

        final HttpClient httpClient = new HttpClient(getContext());
        httpClient.addParam("refContractor", refContractor);
        httpClient.addParam("isReceipt", isReceipt);

        httpClient.postForResult("getRoutesByContractor", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {
                progressBar.setVisibility(visibility);
            }

            @Override
            public void processResponse(JSONObject response) {

                JSONArray routesJson = httpClient.getJsonArrayFromJsonObject(response, "RoutesByContractor");

                routes.clear();

                for (int j = 0; j < routesJson.length(); j++) {

                    JSONObject routeJson = httpClient.getItemJSONArray(routesJson, j);

                    String ref = httpClient.getStringFromJSON(routeJson, "Ref");
                    String dateShipment = httpClient.getStringFromJSON(routeJson, "DateShipment");
                    String driver = httpClient.getStringFromJSON(routeJson, "Driver");
                    String transport = httpClient.getStringFromJSON(routeJson, "Transport");

                    ArrayList<Contractor> contractors = new ArrayList<>();
                    ArrayList<ContractorRoute> contractorRoutes = new ArrayList<>();

                    JSONArray routeContractorsList = httpClient.getJsonArrayFromJsonObject(routeJson, "Contractors");;

                    for (int k = 0; k < routeContractorsList.length(); k++) {

                        JSONObject contractorsListJSON_item = httpClient.getItemJSONArray(routeContractorsList, k);

                        String contractorRef = httpClient.getStringFromJSON(contractorsListJSON_item, "Ref");
                        String contractorDescription = httpClient.getStringFromJSON(contractorsListJSON_item, "Description");

                        contractorRoutes.add(new ContractorRoute(new Contractor(contractorRef, contractorDescription),
                                httpClient.getIntegerFromJSON(contractorsListJSON_item, "ToShipment"),
                                httpClient.getIntegerFromJSON(contractorsListJSON_item, "ToReceipt")));



                    }

                    routes.add(new Route(ref, dateShipment, driver, transport, contractorRoutes));
                }

                etFilter.setText("");

                setFilter("");

            }
        });

    }




}