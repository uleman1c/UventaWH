package com.example.uventawh;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RoutesListActivity extends AppCompatActivity {


    private DataRoutesAdapter adapter;
    private LinearLayout linearLayout;
    private ProgressBar progressBar;

    private List<Route> routes = new ArrayList<>(), routesForShow = new ArrayList<>();

    private String refTransport, descTransport, refDriver, descDriver;

    static int
            REQUEST_CODE_FILTER = 0,
            REQUEST_ADD_TRANSPORT = 1,
            REQUEST_EDIT_ROUTE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes_list);

        linearLayout = findViewById(R.id.linearLayout);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        Intent intent = getIntent();
        refTransport = intent.getStringExtra("refTransport");
        descTransport = intent.getStringExtra("descTransport");
        refDriver = intent.getStringExtra("refDriver");
        descDriver = intent.getStringExtra("descDriver");

        if (refTransport != null){
            ((TextView)findViewById(R.id.tvHeader)).setText("Выбор рейса, водитель: " + descDriver + ", транспорт: " + descTransport);
        }

        adapter = new DataRoutesAdapter(this, routesForShow);

        adapter.setOnStringClickListener(new DataRoutesAdapter.OnStringClickListener(){
            @Override
            public void onStringClick(Route str, View itemView) {

                editRoute(str.ref);

            }
        });

        findViewById(R.id.fabAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (refDriver != null){
                    addRoute(refTransport, refDriver);

                } else {

                    Intent intent = new Intent(RoutesListActivity.this, TrasportListActivity.class);
                    intent.putExtra("ref", "");
                    intent.putExtra("description", "");

                    startActivityForResult(intent, REQUEST_ADD_TRANSPORT);

                }

            }
        });

        RecyclerView recyclerViewToScan = (RecyclerView) findViewById(R.id.rcvList);
        recyclerViewToScan.setAdapter(adapter);

        update(refDriver, refTransport);


    }

    private void editRoute(String ref) {

        Intent intent = new Intent(RoutesListActivity.this, DeliveryOrdersActivity.class);

        intent.putExtra("ref", ref);
        intent.putExtra("refTransport", refTransport);
        intent.putExtra("refDriver", refDriver);
        intent.putExtra("descTransport", descTransport);
        intent.putExtra("descDriver", descDriver);

        startActivityForResult(intent, REQUEST_EDIT_ROUTE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_EDIT_ROUTE){
            update(refDriver, refTransport);
        }

        if (resultCode == RESULT_OK) {

            if (requestCode == REQUEST_CODE_FILTER) {

                JSONArray filter = null;
                try {
                    filter = new JSONArray(data.getStringExtra("filter"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                for (int i = 0; i < filter.length(); i++) {

                    JSONObject filterItem = null;
                    try {
                        filterItem = (JSONObject) filter.get(i);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        setFilter(filterItem.getString("value"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
            else if (requestCode == REQUEST_ADD_TRANSPORT){

                addRoute(data.getStringExtra("refTransport"),
                        data.getStringExtra("refDriver"));

            }
        }
    }

    private void addRoute(String refTransport, String refDriver) {

        final HttpClient httpClient = new HttpClient(this);
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

            if (value.isEmpty() || value.equals(route.driver)){
                routesForShow.add(route);
            }

        }

        adapter.notifyDataSetChanged();


    }

    private void chooseContractor(final Route str) {

//        if (str.contractors.size() == 1){
//
//            finishChooseContractor(str, 0);
//
//        }
//        else {
//
//            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RoutesListActivity.this);
//            alertDialogBuilder.setTitle("Выбрать контрагента");
//
//            alertDialogBuilder.setIcon(R.drawable.sklad);
//
//            ArrayList<String> contractor_items = new ArrayList<>();
//
//            for (Contractor contractor : str.contractors) {
//                contractor_items.add(contractor.description);
//            }
//
//            alertDialogBuilder.setItems(contractor_items.toArray(new String[contractor_items.size()]), new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//
//                    finishChooseContractor(str, which);
//
//                }
//            });
//
//            final AlertDialog alertDialog = alertDialogBuilder.create();
//
//            alertDialog.show();
//
//        }

    }

    private void finishChooseContractor(Route str, int which) {

//        Contractor choosedContractor = str.contractors.get(which);
//
//        Intent intent = new Intent();
//
//        intent.putExtra("ref", str.ref);
//        intent.putExtra("dateShipment", str.dateShipment);
//        intent.putExtra("driver", str.driver);
//        intent.putExtra("transport", str.transport);
//        intent.putExtra("contractorRef", choosedContractor.ref);
//        intent.putExtra("contractorDescription", choosedContractor.description);
//
//        setResult(RESULT_OK, intent);
//
//        finish();

    }

    protected void update(final String refDriver, String refTransport){

        final HttpClient httpClient = new HttpClient(this);
        if (refDriver != null){
            httpClient.addParam("refDriver", refDriver);
            httpClient.addParam("refTransport", refTransport);
        }

        httpClient.postProc("getRoutes" + (refDriver != null ? "DriverTransport" : ""), new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {
                progressBar.setVisibility(visibility);
            }

            @Override
            public void processResponse(JSONObject response) {

                JSONArray routesJson = httpClient.getJsonArrayFromJsonObject(response, "Routes" + (refDriver != null ? "DriverTransport" : ""));

                routes.clear();

                for (int j = 0; j < routesJson.length(); j++) {

                    JSONObject routeJson = httpClient.getItemJSONArray(routesJson, j);

                    String ref = httpClient.getStringFromJSON(routeJson, "Ref");
                    String dateShipment = httpClient.getStringFromJSON(routeJson, "DateShipment");
                    String driver = httpClient.getStringFromJSON(routeJson, "Driver");
                    String transport = httpClient.getStringFromJSON(routeJson, "Transport");

                    ArrayList<Contractor> contractors = new ArrayList<>();

                    JSONArray routeContractorsList = httpClient.getJsonArrayFromJsonObject(routeJson, "Contractors");;

                    for (int k = 0; k < routeContractorsList.length(); k++) {

                        JSONObject contractorsListJSON_item = httpClient.getItemJSONArray(routeContractorsList, k);

                        String contractorRef = httpClient.getStringFromJSON(contractorsListJSON_item, "Ref");
                        String contractorDescription = httpClient.getStringFromJSON(contractorsListJSON_item, "Description");

                        contractors.add(new Contractor(contractorRef, contractorDescription));

                    }

//                    routes.add(new Route(ref, dateShipment, driver, transport, contractors));
                }

                setFilter("");

            }
        });

    }

}
