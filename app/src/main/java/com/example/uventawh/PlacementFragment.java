package com.example.uventawh;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlacementFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlacementFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PlacementFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PlacementFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PlacementFragment newInstance(String param1, String param2) {
        PlacementFragment fragment = new PlacementFragment();
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

    private InputMethodManager imm;

    private Handler h;

    private boolean shtrihCodeKeyboard = false;

    private ProgressBar progressBar;
    private ImageButton ibSettings;

    private EditText etShtrihCode;

    private String contractorRef, contractorDescription;
    private Boolean contractorInputQuantity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_placement, container, false);

        contractorRef = getArguments().getString("ref");
        contractorDescription = getArguments().getString("description");
        contractorInputQuantity = getArguments().getBoolean("inputQuantity");

        ((TextView)root.findViewById(R.id.tvContractor)).setText(contractorDescription);

        imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        etShtrihCode = root.findViewById(R.id.etShtrihCode);

        progressBar = root.findViewById(R.id.progressBar);

        root.findViewById(R.id.ibKeyboard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                etShtrihCode.requestFocus();

                if (shtrihCodeKeyboard){

                    imm.hideSoftInputFromWindow(etShtrihCode.getWindowToken(), 0);

                } else {

                    imm.showSoftInput(etShtrihCode, 0, null);
                }

                shtrihCodeKeyboard = !shtrihCodeKeyboard;


            }
        });

        etShtrihCode.setOnKeyListener(new View.OnKeyListener()
                                      {
                                          public boolean onKey(View v, int keyCode, KeyEvent event)
                                          {
                                              if(event.getAction() == KeyEvent.ACTION_DOWN &&
                                                      (keyCode == KeyEvent.KEYCODE_ENTER))
                                              {

                                                  String strCatName = etShtrihCode.getText().toString();

                                                  etShtrihCode.setText("");

                                                  shtrihCodeKeyboard = false;

                                                  imm.hideSoftInputFromWindow(etShtrihCode.getWindowToken(), 0);

                                                      findCell(strCatName);

                                                  return true;
                                              }
                                              return false;
                                          }
                                      }
        );

        root.findViewById(R.id.ibChooseContractor).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
//                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.contractorsListFragment, bundle);


            }
        });


        h = new Handler();
        Thread t = new Thread(new Runnable() {
            public void run() {
                h.post(setFocus);
            }
        });
        t.start();

        etShtrihCode.requestFocus();



        return root;
    }

    final Runnable setFocus = new Runnable() {
        public void run() {

            if (etShtrihCode.isFocused() && !shtrihCodeKeyboard) {

                imm.hideSoftInputFromWindow(etShtrihCode.getWindowToken(), 0);

            }

            h.postDelayed(setFocus, 500);


        }
    };

    protected void findCell(final String cell) {

        final HttpClient httpClient = new HttpClient(getContext());
        httpClient.addParam("cell", cell);

        httpClient.postForResult("getCell", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {

                progressBar.setVisibility(visibility);

                etShtrihCode.setEnabled(visibility == View.GONE);
//                ibSettings.setEnabled(visibility == View.GONE);

            }

            @Override
            public void processResponse(JSONObject response) {

                if (httpClient.getBooleanFromJSON(response, "Success")) {

                    Bundle bundle = new Bundle();
                    bundle.putString("mode", "placement");
                    bundle.putString("header", "Размещение");
                    bundle.putString("cellRef", httpClient.getStringFromJSON(response, "Ref"));
                    bundle.putString("cell", cell);
                    bundle.putString("cellName", "ячейка");
                    bundle.putString("contractorRef", contractorRef);
                    bundle.putString("contractorDescription", contractorDescription);
                    bundle.putBoolean("contractorInputQuantity", contractorInputQuantity);
                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.scanGoodsFragment, bundle);

                }
                else {

                    Toast toast = Toast.makeText(getContext(), "Ячейка не найдена", Toast.LENGTH_SHORT);
                    toast.show();
                }

            }
        });

    }



}