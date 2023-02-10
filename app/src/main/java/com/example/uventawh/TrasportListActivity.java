package com.example.uventawh;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TrasportListActivity extends AppCompatActivity {

    private static final int
            REQUEST_ADD_TRANSPORT = 1;

    List<TransportItem> items = new ArrayList<>();

    DataTransportAdapter adapter;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    LinearLayout linearLayout;

    String filterRef, filterDescription;

    SwipeController swipeController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trasport_list);

        Intent intent = getIntent();
        filterRef = intent.getStringExtra("ref");
        filterDescription = intent.getStringExtra("description");


        linearLayout = findViewById(R.id.linearLayout);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        adapter = new DataTransportAdapter(this, items);
        adapter.setOnItemClickListener(new DataTransportAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(TransportItem item) {

                Intent intent = new Intent();

                intent.putExtra("refTransport", item.refTransport);
                intent.putExtra("descTransport", item.descTransport);
                intent.putExtra("refDriver",item.refDriver);
                intent.putExtra("descDriver", item.descDriver);
                intent.putExtra("refFilter", filterRef);
                intent.putExtra("descFilter", filterDescription);

                setResult(RESULT_OK, intent);

                finish();

            }
        });

        findViewById(R.id.fabAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(TrasportListActivity.this, AddTransportActivity.class);

                startActivityForResult(intent, REQUEST_ADD_TRANSPORT);
            }
        });

        recyclerView = findViewById(R.id.rvList);
        recyclerView.setAdapter(adapter);

        setupRecyclerView();

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

        update();

    }

    private void setupRecyclerView() {

        swipeController = new SwipeController(new SwipeControllerActions() {
            @Override
            public void onRightClicked(int position) {
//                mAdapter.players.remove(position);
//                mAdapter.notifyItemRemoved(position);
//                mAdapter.notifyItemRangeChanged(position, mAdapter.getItemCount());
                super.onRightClicked(position);
            }

            @Override
            public void onLeftClicked(int position) {
                super.onLeftClicked(position);
            }
        });

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
        itemTouchhelper.attachToRecyclerView(recyclerView);

        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                swipeController.onDraw(c);
            }
        });
    }

    protected void update(){

        final HttpClient httpClient = new HttpClient(this);

        httpClient.postProc("getTransport", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {
                progressBar.setVisibility(visibility);
            }

            @Override
            public void processResponse(JSONObject response) {

                items.clear();

                JSONArray tasksJSON = httpClient.getJsonArrayFromJsonObject(response, "Transport");

                for (int j = 0; j < tasksJSON.length(); j++) {

                    JSONObject task_item = httpClient.getItemJSONArray(tasksJSON, j);

                    String refTransport = httpClient.getStringFromJSON(task_item, "RefTransport");
                    String descTransport = httpClient.getStringFromJSON(task_item, "DescTransport");
                    String refDriver = httpClient.getStringFromJSON(task_item, "RefDriver");
                    String descDriver = httpClient.getStringFromJSON(task_item, "DescDriver");
                    String descDocument = httpClient.getStringFromJSON(task_item, "DescDocument");
                    String date = httpClient.getStringFromJSON(task_item, "Date");

                    items.add(new TransportItem(refTransport, descTransport, refDriver, descDriver, descDocument, date));

                }

                adapter.notifyDataSetChanged();

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){

            if (requestCode == REQUEST_ADD_TRANSPORT){

                Intent intent = new Intent();

                intent.putExtra("refTransport", data.getStringExtra("refTransport"));
                intent.putExtra("descTransport", data.getStringExtra("descTransport"));
                intent.putExtra("refDriver", data.getStringExtra("refDriver"));
                intent.putExtra("descDriver", data.getStringExtra("descDriver"));
                intent.putExtra("refFilter", filterRef);
                intent.putExtra("descFilter", filterDescription);

                setResult(RESULT_OK, intent);

                finish();

            }

        }

    }
}
