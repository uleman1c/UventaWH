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
 * Use the {@link ScanQuantityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScanQuantityFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ScanQuantityFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ScanQuantityFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ScanQuantityFragment newInstance(String param1, String param2) {
        ScanQuantityFragment fragment = new ScanQuantityFragment();
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

    String cellRef, containerRef, shtrihcode;

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
        View root = inflater.inflate(R.layout.fragment_scan_quantity, container, false);

        soundPlayer = new SoundPlayer(getContext(), R.raw.hrn05);

        tvProduct = root.findViewById(R.id.tvProduct);

        bundle = getArguments();
        cellRef = bundle.getString("cellRef");
        containerRef = bundle.getString("containerRef");

        shtrihcode = "";

        quantity = 0;

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

        root.findViewById(R.id.btnSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final HttpClient httpClient = new HttpClient(getContext());

                Bundle arguments = new Bundle();

                httpClient.showQuestionYesNoCancel(getActivity(), new BundleMethodInterface() {
                    @Override
                    public void callMethod(Bundle arguments) {

                        save();

                    }
                }, arguments, "Сохранить?", "Контейнер");



            }
        });

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

        if (shtrihcode.isEmpty()) {

            shtrihcode = strCatName;

        }

        if (shtrihcode.equals(strCatName)) {

            quantity += 1;

            tvProduct.setText(shtrihcode + ", " + quantity + " шт");

        } else {

            soundPlayer.play();

        }
        return;

    }

    private void save() {

        final HttpClient httpClient = new HttpClient(getContext());
        httpClient.addParam("cellRef", cellRef);
        httpClient.addParam("containerRef", containerRef);
        httpClient.addParam("shtrihCode", shtrihcode);
        httpClient.addParam("quantity", quantity);

        httpClient.postProc("newInputStart", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {

            }

            @Override
            public void processResponse(JSONObject response) {

                String containerRef = httpClient.getStringFromJSON(response, "ContainerRef");
                String containerName = httpClient.getStringFromJSON(response, "ContainerName");

                if (!containerRef.isEmpty()) {

                    bundle.putString("ref", cellRef);
                    bundle.putString("containerRef", containerRef);
                    bundle.putString("containerName", containerName);

                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment).popBackStack();
                }

            }
        });

    }

}