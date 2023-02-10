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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import org.json.JSONObject;

public class NewContainerFragment extends Fragment {

    private NewContainerViewModel mViewModel;

    public static NewContainerFragment newInstance() {
        return new NewContainerFragment();
    }

    private boolean shtrihCodeKeyboard = false;

    private InputMethodManager imm;

    private EditText etShtrihCode;

    Handler h;

    String ref;

    Bundle bundle;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.new_container_fragment, container, false);

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

        root.findViewById(R.id.btnGenerate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final HttpClient httpClient = new HttpClient(getContext());

                Bundle arguments = new Bundle();

                httpClient.showQuestionYesNoCancel(getActivity(), new BundleMethodInterface() {
                    @Override
                    public void callMethod(Bundle arguments) {

                        scanShtrihCode("");

                    }
                }, arguments, "Генерировать контейнер?", "Контейнер");



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

        final HttpClient httpClient = new HttpClient(getContext());
        httpClient.addParam("ref", ref);
        httpClient.addParam("shtrihCode", strCatName);

        httpClient.postProc("newContainer", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {

            }

            @Override
            public void processResponse(JSONObject response) {

                String containerRef = httpClient.getStringFromJSON(response, "ContainerRef");
                String containerName = httpClient.getStringFromJSON(response, "ContainerName");

                if (!containerRef.isEmpty()) {

                    bundle.putString("ref", ref);
                    bundle.putString("containerRef", containerRef);
                    bundle.putString("containerName", containerName);

                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment).popBackStack();
                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(bundle.getInt("next"), bundle);
                }

            }
        });

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(NewContainerViewModel.class);
        // TODO: Use the ViewModel
    }

}