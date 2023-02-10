package com.example.uventawh;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AcceptActivity extends AppCompatActivity {

    Boolean debug = false;
    Boolean authorized = false;
    String user_id, filterRef, filterDescription = "";

    List<TaskItem> task_items = new ArrayList<>();

    DataAdapter adapter;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    LinearLayout linearLayout;
    TextView tvFilter;

    private static final int
            REQUEST_ADD_TRANSPORT = 4,
            REQUEST_SELECT_CONTRACTOR = 5,
            REQUEST_ADD_FROM_ROUTE = 6,
            REQUEST_ADD_ACCEPT_FROM_TRANSPORT = 10;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_accept);


        filterRef  = this.getString(R.string.emptyRef);

        linearLayout = findViewById(R.id.linearLayout);
        tvFilter = findViewById(R.id.tvFilter);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        findViewById(R.id.btnClearFilter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                filterRef = getString(R.string.emptyRef);
                filterDescription = "выберите контрагента";

                tvFilter.setText(filterDescription);

                update();

            }
        });

        findViewById(R.id.btnChoose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent("com.example.uventawh.action.contractorsactivity");

                startActivityForResult(intent, REQUEST_SELECT_CONTRACTOR);

            }
        });

        findViewById(R.id.fabAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addAccept();

            }
        });



        adapter = new DataAdapter(this, task_items);
        adapter.setOnTaskItemClickListener(new DataAdapter.OnTaskItemClickListener() {
            @Override
            public void onTaskItemClick(TaskItem taskItem) {

                if (taskItem.name.equals("Приемка")) {

                    Intent intent = new Intent(getBaseContext(), ScanActivity.class);

                    intent.putExtra("ref", taskItem.ref);
                    intent.putExtra("name", taskItem.name);
                    intent.putExtra("number", taskItem.number);
                    intent.putExtra("date", taskItem.date);
                    intent.putExtra("refContractor", taskItem.refContractor);
                    intent.putExtra("company", taskItem.company);
                    intent.putExtra("image", taskItem.image);
                    intent.putExtra("refSender", taskItem.refSender);
                    intent.putExtra("sender", taskItem.sender);

                    startActivityForResult(intent, 2);
                }
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.list);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState==RecyclerView.SCROLL_STATE_IDLE){

                    if (!recyclerView.canScrollVertically(1)) {

                        update();

                    }
                    else if (!recyclerView.canScrollVertically(-1)) {

                        update();

                    }
                }

            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });


        DB db = new DB(this);
        db.open();

        user_id = db.getConstant("user_id");

        authorized = user_id != null;

        db.close();

        if (authorized) {

            update();

        }


    }

    private void addAccept() {

        add();

//        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AcceptActivity.this);
//        alertDialogBuilder.setTitle("Добавить приемку");
//
//        LayoutInflater inflater = AcceptActivity.this.getLayoutInflater();
//
//        View view = inflater.inflate(R.layout.dialog_choose_accept, null);
//
//        alertDialogBuilder.setView(view);
//        alertDialogBuilder.setIcon(R.drawable.sklad96);
//
//        final AlertDialog alertDialog = alertDialogBuilder.create();
//
//        view.findViewById(R.id.btnAddFromRoute).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                alertDialog.cancel();
//
//                addFromRoute();
//
//            }
//        });
//
//        view.findViewById(R.id.btnAddFree).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                alertDialog.cancel();
//
//                add();
//
//            }
//        });
//
//
//        alertDialog.show();
//
//




    }

    private void addFromRoute() {

        Intent intent = new Intent("com.example.uventawh.action.routesactivity");

        startActivityForResult(intent, REQUEST_ADD_FROM_ROUTE);
    }

    protected void update(){

        final HttpClient httpClient = new HttpClient(this);
        httpClient.addParam("contractor", filterRef);

        httpClient.postProc("getTasks", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {
                progressBar.setVisibility(visibility);
            }

            @Override
            public void processResponse(JSONObject response) {

                task_items.clear();

                JSONArray tasksJSON = httpClient.getJsonArrayFromJsonObject(response, "Tasks");

                for (int j = 0; j < tasksJSON.length(); j++) {

                    JSONObject task_item = httpClient.getItemJSONArray(tasksJSON, j);

                    String ref = httpClient.getStringFromJSON(task_item, "Ref");
                    String type = httpClient.getStringFromJSON(task_item, "Type");
                    String number = httpClient.getStringFromJSON(task_item, "Number");
                    String date = httpClient.getStringFromJSON(task_item, "Date");
                    String refContractor = httpClient.getStringFromJSON(task_item, "ContractorRef");
                    String company = httpClient.getStringFromJSON(task_item, "Contractor");
                    String refSender = httpClient.getStringFromJSON(task_item, "SenderRef");
                    String sender = httpClient.getStringFromJSON(task_item, "Sender");
                    Integer quantity = httpClient.getIntegerFromJSON(task_item, "Quantity");
                    Integer accepted = httpClient.getIntegerFromJSON(task_item, "Accepted");
                    String driverFio = httpClient.getStringFromJSON(task_item, "DriverFIO");
                    String driverDocument = httpClient.getStringFromJSON(task_item, "DriverDocument");
                    String transport = httpClient.getStringFromJSON(task_item, "Transport");
                    String documentNumber = httpClient.getStringFromJSON(task_item, "DocumentNumber");

                    Integer intPicture = 0;
                    if (type.equals("Приемка")) {
                        intPicture = R.drawable.green_arrow;
                    } else if (type.equals("ГрупповаяПриемка")) {
                        intPicture = R.drawable.green_2_arrows;
                    }
                    task_items.add(new TaskItem(ref, type, number, date, refContractor, company, refSender, sender, intPicture, quantity, accepted, driverFio, driverDocument, transport, documentNumber));

                }

                adapter.notifyDataSetChanged();

            }
        });

    }

    protected void add(){

        Intent intent = new Intent(this, TrasportListActivity.class);
        intent.putExtra("ref", filterRef);
        intent.putExtra("description", filterDescription);

        startActivityForResult(intent, REQUEST_ADD_TRANSPORT);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 2){

            update();

        }
        else if(requestCode == 3){

            if (resultCode == RESULT_OK) {

                String ref = data.getStringExtra("ref");
                String description = data.getStringExtra("description");

                addAcceptForContractor(ref, description);

            }
        }
        else if(requestCode == REQUEST_ADD_TRANSPORT){

//        if (filterRef.equals(this.getString(R.string.emptyRef))) {
//
//            Intent intent = new Intent("com.example.uventawh.action.contractorsactivity");
//
//            startActivityForResult(intent, 3);
//        }
//        else {
//
//            addAcceptForContractor(filterRef, filterDescription);
//
//        }
            if (resultCode == RESULT_OK) {

                Intent intent = new Intent(this, AcceptPageOneActivity.class);

                intent.putExtra("refTransport", data.getStringExtra("refTransport"));
                intent.putExtra("descTransport", data.getStringExtra("descTransport"));
                intent.putExtra("refDriver", data.getStringExtra("refDriver"));
                intent.putExtra("descDriver", data.getStringExtra("descDriver"));
                intent.putExtra("refFilter", data.getStringExtra("refFilter"));
                intent.putExtra("descFilter", data.getStringExtra("descFilter"));

                startActivityForResult(intent, REQUEST_ADD_ACCEPT_FROM_TRANSPORT);


            }
        }

        else if(requestCode == REQUEST_SELECT_CONTRACTOR){

            if (resultCode == RESULT_OK) {

                filterRef = data.getStringExtra("ref");
                filterDescription = data.getStringExtra("description");

                tvFilter.setText(filterDescription);

                update();

            }
        }

        else if(requestCode == REQUEST_ADD_FROM_ROUTE){

            if (resultCode == RESULT_OK) {

                String routeRef = data.getStringExtra("ref");
                String dateShipment = data.getStringExtra("dateShipment");
                String driver = data.getStringExtra("driver");
                String transport = data.getStringExtra("transport");
                String contractorRef = data.getStringExtra("contractorRef");
                String contractorDescription = data.getStringExtra("contractorDescription");

                Intent intent = new Intent("com.example.uventawh.action.acceptfromrouteactivity");
                intent.putExtra("ref", routeRef);
                intent.putExtra("dateShipment", dateShipment);
                intent.putExtra("driver", driver);
                intent.putExtra("transport", transport);
                intent.putExtra("contractorRef", contractorRef);
                intent.putExtra("contractorDescription", contractorDescription);

                startActivityForResult(intent, 7);

            }
        }



    }

    private void addAcceptForContractor(String ref, String description) {

        Intent intent = new Intent(this, AcceptPageOneActivity.class);
        intent.putExtra("ref", ref);
        intent.putExtra("description", description);

        startActivityForResult(intent, 4);

    }



}
