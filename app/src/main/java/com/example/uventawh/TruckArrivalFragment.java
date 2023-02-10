package com.example.uventawh;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class TruckArrivalFragment extends Fragment {

    public TruckArrivalFragment() {
        // Required empty public constructor
    }

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_truck_arrival, container, false);

//        Intent intent = getIntent();
//        filterRef = intent.getStringExtra("ref");
//        filterDescription = intent.getStringExtra("description");

        Bundle bundle = getArguments();

        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();

        NavigationView navigationView = getActivity().findViewById(R.id.nav_view);

        View headerView = navigationView.getHeaderView(0);

        MenuItem checkedItem = navigationView.getCheckedItem();
        //actionBar.setTitle(checkedItem.getTitle() + " - " + actionBar.getTitle());


        linearLayout = root.findViewById(R.id.linearLayout);

        progressBar = root.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        adapter = new DataTransportAdapter(getContext(), items);
        adapter.setOnItemClickListener(new DataTransportAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(TransportItem item) {

                chooseRoute(item.refTransport, item.descTransport, item.refDriver, item.descDriver);

            }
        });

        root.findViewById(R.id.fabAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();

                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.addTruckFragment, bundle);

            }
        });

        root.findViewById(R.id.fabAddShtrih).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();

                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.nav_scanRouteListFragment, bundle);

            }
        });

        recyclerView = root.findViewById(R.id.rvList);
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



        return root;
    }

    private void chooseRoute(String refTransport, String descTransport, String refDriver, String descDriver) {

        Bundle bundle = new Bundle();
        bundle.putString("refTransport", refTransport);
        bundle.putString("descTransport", descTransport);
        bundle.putString("refDriver", refDriver);
        bundle.putString("descDriver", descDriver);

        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.routesListFragment, bundle);

    }

    private void addRoute(final String refTransport, final String refDriver) {

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

//                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment).popBackStack(R.id.nav_home, false);

//                    Bundle bundle = new Bundle();
////                    bundle.putString("ref", ref);
////                    bundle.putString("refTransport", refTransport);
////                    bundle.putString("refDriver", refDriver);
////                    bundle.putString("descTransport", descTransport);
////                    bundle.putString("descDriver", descDriver);
//
//                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.addTruckFragment, bundle);
//
//                    editRoute(httpClient.getStringFromJSON(response, "RefRoute"));

                    Bundle bundle = new Bundle();
                    bundle.putString("ref", httpClient.getStringFromJSON(response, "RefRoute"));
                    bundle.putString("refTransport", refTransport);
                    bundle.putString("refDriver", refDriver);
//                    bundle.putString("descTransport", descTransport);
//                    bundle.putString("descDriver", descDriver);

                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.editRouteFragment, bundle);


                }
            }
        });

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

        final HttpClient httpClient = new HttpClient(getContext());

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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        if (resultCode == RESULT_OK){
//
//            if (requestCode == REQUEST_ADD_TRANSPORT){
//
//                Intent intent = new Intent();
//
//                intent.putExtra("refTransport", data.getStringExtra("refTransport"));
//                intent.putExtra("descTransport", data.getStringExtra("descTransport"));
//                intent.putExtra("refDriver", data.getStringExtra("refDriver"));
//                intent.putExtra("descDriver", data.getStringExtra("descDriver"));
//                intent.putExtra("refFilter", filterRef);
//                intent.putExtra("descFilter", filterDescription);
//
//                setResult(RESULT_OK, intent);
//
//                finish();
//
//            }
//
//        }

    }



}
