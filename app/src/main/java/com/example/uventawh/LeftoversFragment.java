package com.example.uventawh;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uventawh.objects.Leftover;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class LeftoversFragment extends Fragment {

    private LeftoversViewModel mViewModel;

    public static LeftoversFragment newInstance() {
        return new LeftoversFragment();
    }

    private boolean shtrihCodeKeyboard = false;

    private InputMethodManager imm;

    DelayAutoCompleteTextView actvShtrihCode;

    LeftoversAdapter adapter;
    private ArrayList<Leftover> leftovers = new ArrayList<>();
    RecyclerView rvList;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_leftovers, container, false);

        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        actvShtrihCode = inflate.findViewById(R.id.actvShtrihCode);
        actvShtrihCode.requestFocus();
        imm.hideSoftInputFromWindow(actvShtrihCode.getWindowToken(), 0);

        actvShtrihCode.setOnKeyListener(new View.OnKeyListener() {
                                            public boolean onKey(View v, int keyCode, KeyEvent event) {
                                                if (event.getAction() == KeyEvent.ACTION_DOWN &&
                                                        (keyCode == KeyEvent.KEYCODE_ENTER)) {

                                                    String strCatName = actvShtrihCode.getText().toString();

                                                    actvShtrihCode.setText("");

                                                    shtrihCodeKeyboard = false;

                                                    imm.hideSoftInputFromWindow(actvShtrihCode.getWindowToken(), 0);

                                                    scanShtrihCode(strCatName);

                                                    return true;
                                                }
                                                return false;
                                            }
                                        }
        );

        adapter = new LeftoversAdapter(getContext(), leftovers);
        adapter.setOnClickListener(new LeftoversAdapter.OnClickListener() {
            @Override
            public void onClick(Leftover taskItem, Integer pos, View itemView) {

            }
        });

        adapter.setOnLongClickListener(new LeftoversAdapter.OnLongClickListener() {
            @Override
            public void onLongClick(Leftover taskItem, Integer pos, View itemView) {

            }
        });

        rvList = inflate.findViewById(R.id.rvList);
        rvList.setAdapter(adapter);



        return inflate;
    }

    private void scanShtrihCode(String shtrihCode) {

        final HttpClient httpClient = new HttpClient(getContext());
        httpClient.addParam("shtrihCode", shtrihCode);

        httpClient.postProc("getLeftovers", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {
                //progressBar.setVisibility(visibility);
            }

            @Override
            public void processResponse(JSONObject response) {

                JSONArray jsonArray = httpClient.getJsonArrayFromJsonObject(response, "Leftovers");

                for (int j = 0; j < jsonArray.length(); j++) {

                    leftovers.add(Leftover.LeftoverFromJSON(httpClient.getItemJSONArray(jsonArray, j), httpClient));

                }

                adapter.notifyDataSetChanged();

            }
        });



    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(LeftoversViewModel.class);
        // TODO: Use the ViewModel
    }

}