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
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ScanCellFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScanCellFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ScanCellFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ScanCellFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ScanCellFragment newInstance(String param1, String param2) {
        ScanCellFragment fragment = new ScanCellFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private boolean shtrihCodeKeyboard = false;

    private InputMethodManager imm;

    private EditText etShtrihCode;

    Handler h;

    String ref, scontainer, shtrihcode;

    Bundle bundle;

    Integer quantity;

    SoundPlayer soundPlayer;

    TextView tvProduct;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_scan_cell, container, false);

        bundle = getArguments();
        ref = bundle.getString("ref");

        h = new Handler();

        etShtrihCode = root.findViewById(R.id.etShtrihCode);

        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        etShtrihCode.requestFocus();
        imm.hideSoftInputFromWindow(etShtrihCode.getWindowToken(), 0);

        etShtrihCode.setOnKeyListener(new View.OnKeyListener() {
                                          public boolean onKey(View v, int keyCode, KeyEvent event) {
                                              if (event.getAction() == KeyEvent.ACTION_DOWN &&
                                                      (keyCode == KeyEvent.KEYCODE_ENTER)) {

                                                  String strCatName = etShtrihCode.getText().toString();

                                                  etShtrihCode.setText("");

                                                  shtrihCodeKeyboard = false;

                                                  imm.hideSoftInputFromWindow(etShtrihCode.getWindowToken(), 0);

                                                  if (!strCatName.isEmpty()) {
                                                      scanShtrihCode(strCatName);
                                                  }
                                                  return true;
                                              }
                                              return false;
                                          }
                                      }
        );

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

        Thread t = new Thread(new Runnable() {
            public void run() {
                h.post(setFocus);
            }
        });
        t.start();




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

    private void scanShtrihCode(String strCatName) {

        final HttpClient httpClient = new HttpClient(getContext());
        httpClient.addParam("shtrihCode", strCatName);

        httpClient.postProc("scanCell", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {

            }

            @Override
            public void processResponse(JSONObject response) {

                String containerRef = httpClient.getStringFromJSON(response, "CellRef");
                String containerName = httpClient.getStringFromJSON(response, "CellName");

                if (!containerRef.isEmpty()) {

                    bundle.putString("ref", ref);
                    bundle.putString("cellRef", containerRef);
                    bundle.putString("cellName", containerName);

                    Integer next = bundle.getInt("next");

                    bundle.putInt("next", bundle.getInt("next2"));

                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment).popBackStack();
                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(next, bundle);
                }

            }
        });

    }
}