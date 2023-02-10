package com.example.uventawh;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ContainerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContainerFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ContainerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ContainerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ContainerFragment newInstance(String param1, String param2) {
        ContainerFragment fragment = new ContainerFragment();
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

    private TextView tvHeader;
    private String shipmentOrder, cell_shtrih_code, container_shtrih_code;
    private ProgressBar progressBar;
    private RecyclerView rvProducts;

    private List<DeliveryOrderProduct> deliveryOrderProducts = new ArrayList<>();
    private DataDeliveryOrdersProductsAdapter dataDeliveryOrdersProductsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_container, container, false);

        Bundle bundle = getArguments();
        shipmentOrder = bundle.getString("shipmentOrder");
        container_shtrih_code = bundle.getString("container_shtrih_code");
        cell_shtrih_code = bundle.getString("cell_shtrih_code");
        tvHeader = root.findViewById(R.id.tvHeader);
//        tvHeader.setText(cell);

        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.setTitle("Контейнер: " + container_shtrih_code);

        progressBar = root.findViewById(R.id.progressBar);

        progressBar = root.findViewById(R.id.progressBar);

        dataDeliveryOrdersProductsAdapter = new DataDeliveryOrdersProductsAdapter(getContext(), deliveryOrderProducts);
        dataDeliveryOrdersProductsAdapter.setOnTaskItemClickListener(new DataDeliveryOrdersProductsAdapter.OnTaskItemClickListener() {
            @Override
            public void onTaskItemClick(DeliveryOrderProduct taskItem, View itemView) {

                taskItem.scanned = taskItem.scanned + 1;

                dataDeliveryOrdersProductsAdapter.notifyDataSetChanged();

            }
        });
        rvProducts = root.findViewById(R.id.rvProducts);
        rvProducts.setAdapter(dataDeliveryOrdersProductsAdapter);

        update();

        return root;
    }

    private void update() {

        deliveryOrderProducts.clear();

        final HttpClient httpClient = new HttpClient(getContext());
        httpClient.addParam("refDeliveryOrder", shipmentOrder);
        httpClient.addParam("cell_shtrih_code", cell_shtrih_code);
        httpClient.addParam("container_shtrih_code", container_shtrih_code);

        httpClient.postProc("getDeliveryOrderContainer", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {
                progressBar.setVisibility(visibility);
            }

            @Override
            public void processResponse(JSONObject response) {

                JSONArray routeShtrihs = httpClient.getJsonArrayFromJsonObject(response, "DeliveryOrderContainer");

                for (int j = 0; j < routeShtrihs.length(); j++) {

                    JSONObject accept_item = httpClient.getItemJSONArray(routeShtrihs, j);

                    deliveryOrderProducts.add(DeliveryOrderProduct.DeliveryOrderProductFromJSON(accept_item, httpClient));

                }

                dataDeliveryOrdersProductsAdapter.notifyDataSetChanged();

            }
        });





    }
}