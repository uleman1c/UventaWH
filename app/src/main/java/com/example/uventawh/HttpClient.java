package com.example.uventawh;


import static android.content.Context.LOCATION_SERVICE;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.entity.mime.HttpMultipartMode;
import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder;
import cz.msebera.android.httpclient.entity.mime.content.FileBody;

public class HttpClient {

    private Context mCtx;
    private AsyncHttpClient client;

    private String serverUrl = "";

    private JSONObject requestParams;
    private MultipartEntityBuilder builder;
    private String pathToFile;

    public void setServerUrl(String base, String hs) {

        DB db = new DB(mCtx);
        db.open();

        String server = db.getConstant("server_address");

        db.close();

        if (server == null){
            server = mCtx.getString(R.string.server);
        }

        server = server + (server.substring(server.length() - 1).equals("/") ? "" : "/");

        this.serverUrl = server + base + (new DB(mCtx).isTest() ? "_test" : "") + hs;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public HttpClient(Context Ctx) {

        Init(Ctx, false);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void Init(Context Ctx, Boolean sync){

        client = sync ? new SyncHttpClient() : new AsyncHttpClient();

        mCtx = Ctx;

        requestParams = new JSONObject();

        DB db = new DB(mCtx);

        db.open();

        String prog_id = db.getConstant("prog_id");
        if (prog_id == null) {

            prog_id = UUID.randomUUID().toString();
            db.updateConstant("prog_id", prog_id);

        }

        String base_name = db.getConstant("base_name");
        String user = db.getConstant("user");
        String pwd = db.getConstant("pwd");

        db.close();

        addParam("prog_id", prog_id);
        addParam("version", Ctx.getResources().getString(R.string.version));

        setServerUrl(base_name, mCtx.getString(R.string.hs_wms));


        client.addHeader("Authorization", "Basic " + Base64.getEncoder().encodeToString((user + ":" + pwd).getBytes(StandardCharsets.UTF_8)));  //ZXhjaDoxMjM0NTY=

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public HttpClient(Context Ctx, Boolean sync) {

        Init(Ctx, sync);

    }

    public void addCoordinates(final LocationRequestInterface locationRequestInterface){

        final Boolean[] coordsGot = {false};

        LocationManager locationManager = (LocationManager) mCtx.getSystemService(LOCATION_SERVICE);

        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                if (!coordsGot[0]){

                    addParam("lat", location.getLatitude());
                    addParam("lon", location.getLongitude());

                    locationRequestInterface.onSuccess(location);

                    coordsGot[0] = true;
                }


            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                locationRequestInterface.onFailure(provider);
            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mCtx.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && mCtx.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            }
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 10, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 10, locationListener);

    }





    public void addParam(String name, Boolean value){

        try {
            requestParams.put(name, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void addParam(String name, JSONArray value){

        try {
            requestParams.put(name, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void addParam(String name, EditText value){

        try {
            requestParams.put(name, value.getText());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void addParam(String name, Switch value){

        try {
            requestParams.put(name, value.isChecked());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void addParam(String name, CharSequence value){

        try {
            requestParams.put(name, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void addParam(String name, String value){

        try {
            requestParams.put(name, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void addParam(String name, int value){

        try {
            requestParams.put(name, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void addParam(String name, double value){

        try {
            requestParams.put(name, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public RequestHandle get(Context context, String url, ResponseHandlerInterface responseHandler) {

        RequestParams params = new RequestParams();

        return client.get(context, serverUrl + url, params, responseHandler);

    }

    public RequestHandle getProc(Context context, String url, final HttpRequestInterface httpRequestInterface) {

        RequestParams params = new RequestParams();

        return client.get(context, serverUrl + url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                JSONArray response = getResponseJSONArray(responseBody);

                httpRequestInterface.processResponse(getItemJSONArray(response, 0));

//                for (int i = 0; i < response.length(); i++) {
//
//                    JSONObject response_item = getItemJSONArray(response, i);
//
//                    if (getStringFromJSON(response_item, "Name").equals(methodName)) {
//
//                    }
//                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                showMessageOnFailure(statusCode, headers, responseBody, error);

            }
        });

    }

    public RequestHandle post(Context context, String url, JSONArray params, ResponseHandlerInterface responseHandler) {

        StringEntity entity = null;
        entity = new StringEntity(params.toString(), "UTF-8");

        return client.post(context, serverUrl + url, entity, "application/json", responseHandler);

    }

    public RequestHandle postProc(final String methodName, final HttpRequestInterface httpRequestInterface) {

        JSONArray params = new JSONArray();

        JSONObject request = new JSONObject();
        try {
            request.put("request", methodName);
            request.put("parameters", requestParams);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        params.put(request);

        return client.post(mCtx, serverUrl + new DB(mCtx).getRequestUserProg(), new StringEntity(params.toString(), "UTF-8"), "application/json", new AsyncHttpResponseHandler(){

            @Override
            public void onStart() {
                super.onStart();

                httpRequestInterface.setProgressVisibility(View.VISIBLE);

            }

            @Override
            public void onFinish() {
                super.onFinish();

                httpRequestInterface.setProgressVisibility(View.GONE);

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                JSONArray response = getResponseJSONArray(responseBody);

                for (int i = 0; i < response.length(); i++) {

                    JSONObject response_item = getItemJSONArray(response, i);

                    if (getStringFromJSON(response_item, "Name").equals(methodName)) {

                        httpRequestInterface.processResponse(response_item);

                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                showMessageOnFailure(statusCode, headers, responseBody, error);
            }
        });

    }

    public void setParamsFromString(String s){

        try {
            requestParams = new JSONObject(s);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void toastShow(String message){

        Toast.makeText(mCtx, message, Toast.LENGTH_SHORT).show();

    }

    public RequestHandle postForResult(final String methodName, final HttpRequestInterface httpRequestInterface) {

        JSONArray params = new JSONArray();

        JSONObject request = new JSONObject();
        try {
            request.put("request", methodName);
            request.put("parameters", requestParams);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        params.put(request);

        return client.post(mCtx, serverUrl + new DB(mCtx).getRequestUserProg(), new StringEntity(params.toString(), "UTF-8"), "application/json", new AsyncHttpResponseHandler(){

            @Override
            public void onStart() {
                super.onStart();

                httpRequestInterface.setProgressVisibility(View.VISIBLE);

            }

            @Override
            public void onFinish() {
                super.onFinish();

                httpRequestInterface.setProgressVisibility(View.GONE);

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                JSONArray response = getResponseJSONArray(responseBody);

                for (int i = 0; i < response.length(); i++) {

                    JSONObject response_item = getItemJSONArray(response, i);

                    if (getStringFromJSON(response_item, "Name").equals(methodName)) {

                        httpRequestInterface.processResponse(getJsonObjectFromJsonObject(response_item, "Result"));

                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                showMessageOnFailure(statusCode, headers, responseBody, error);
            }
        });

    }

    private void addRequest(String methodName, String response){

        DB db = new DB(mCtx);

        db.open();

        ContentValues cv = new ContentValues();
        cv.put("method", methodName);
        cv.put("params", requestParams.toString());
        cv.put("response", response);

        db.insert("requests", null, cv);

        db.close();

    }

    private String addRef(String ref){

        DB db = new DB(mCtx);

        db.open();

        String external = db.getExternalRef(ref);

        if (external == null) {

            external = ref;

            ContentValues cv = new ContentValues();
            cv.put("internal", ref);
            cv.put("external", "");

            db.insert("refs", null, cv);
        }

        if (external.isEmpty()){

            external = ref;

        }

        db.close();

        return external;

    }

    public RequestHandle postForResultDelayed(final String methodName, final HttpRequestInterface httpRequestInterface) {

        JSONObject request = new JSONObject();

        Boolean success = true;

        if (methodName.equals("addExam")) {

            String ref = UUID.randomUUID().toString();

            putInJsonObject(request, "Ref", ref);
            addRequest(methodName, ref);
            addRef(ref);

        }
        else if (methodName.equals("setExamStep1")) {

            addParam("ref", addRef(getStringFromJSON(requestParams, "ref")));
            addRequest(methodName, "");

        }
        else if (methodName.equals("setExamStep2")) {

            addParam("ref", addRef(getStringFromJSON(requestParams, "ref")));
            addRequest(methodName, "");

        }
        else if (methodName.equals("setExamStep3")) {

            addParam("ref", addRef(getStringFromJSON(requestParams, "ref")));
            addRequest(methodName, "");

        }
        else if (methodName.equals("setExamStep4")) {

            addParam("ref", addRef(getStringFromJSON(requestParams, "ref")));
            addRequest(methodName, "");

        }
        else if (methodName.equals("setExamStep5")) {

            addParam("ref", addRef(getStringFromJSON(requestParams, "ref")));
            addRequest(methodName, "");

        }
        else if (methodName.equals("setExamStep6")) {

            addParam("ref", addRef(getStringFromJSON(requestParams, "ref")));
            addRequest(methodName, "");

        }
        else if (methodName.equals("setDamage")) {

            String ref = UUID.randomUUID().toString();

            putInJsonObject(request, "Ref", ref);
            addRequest(methodName, ref);
            addRef(ref);

        }
        else if (methodName.equals("setMoneyReport")) {

            String ref = UUID.randomUUID().toString();

            putInJsonObject(request, "Ref", ref);
            addRequest(methodName, ref);
            addRef(ref);

        }
        else if (methodName.equals("setFuelAdd")) {

            String ref = UUID.randomUUID().toString();

            putInJsonObject(request, "Ref", ref);
            addRequest(methodName, ref);
            addRef(ref);

        }
        else if (methodName.equals("setRouteDocAdd")) {

            String ref = UUID.randomUUID().toString();

            putInJsonObject(request, "Ref", ref);
            addRequest(methodName, ref);
            addRef(ref);

        }
        else if (methodName.equals("setExecuteAddress")) {

            addRequest(methodName, "");

        }
        else if (methodName.equals("getIncomingDocByParameters")) {

            DB db = new DB(mCtx);
            db.open();

            String internalRef = getStringFromJSON(requestParams, "ref");
            String externalRef = db.getExternalRef(internalRef);

            if (externalRef == null) {

                externalRef = internalRef;
            }


            db.close();

            addParam("ref", externalRef.isEmpty() ? internalRef : externalRef);

            String ref = UUID.randomUUID().toString();

            putInJsonObject(request, "Ref", ref);
            addRequest(methodName, ref);
            addRef(ref);

        }
        else if (methodName.equals("getIncomingDocRoutePointFoto")) {

            DB db = new DB(mCtx);
            db.open();

            String internalRef = getStringFromJSON(requestParams, "ref");
            String externalRef = db.getExternalRef(internalRef);

            if (externalRef == null) {

                externalRef = internalRef;
            }


            db.close();

            addParam("ref", externalRef.isEmpty() ? internalRef : externalRef);

            String ref = UUID.randomUUID().toString();

            putInJsonObject(request, "Ref", ref);
            addRequest(methodName, ref);
            addRef(ref);

        }
        else if (methodName.equals("setRoutePointDateStart")) {

            addRequest(methodName, "");

        }
        else if (methodName.equals("setRoutePointDriverData")) {

            addRequest(methodName, "");

        }
        else {
            success = false;
        }

        putInJsonObject(request, "Success", success);
        if (!success){
            putInJsonObject(request, "Message", "Invalid method " + methodName);
        }


        httpRequestInterface.setProgressVisibility(View.VISIBLE);
        httpRequestInterface.setProgressVisibility(View.GONE);
        httpRequestInterface.processResponse(request);

        return null;
    }

    private JSONObject getResponseJsonObject() {

        JSONObject request = new JSONObject();


        return request;

    }

    private void putInJsonObject(JSONObject request, String name, String value) {

        try {
            request.put(name, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void putInJsonObject(JSONObject request, String name, Boolean value) {

        try {
            request.put(name, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void putInJsonObject(JSONObject request, String name, Integer value) {

        try {
            request.put(name, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void putInJsonObject(JSONObject request, String name, Double value) {

        try {
            request.put(name, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void putInJsonObject(JSONObject request, String name, Long value) {

        try {
            request.put(name, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void putInJsonObject(JSONObject request, String name, Float value) {

        try {
            request.put(name, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public RequestHandle postBinary(Context context, String url, HttpEntity entity, ResponseHandlerInterface responseHandler) {

        return client.post(context, serverUrl + url, entity, entity.getContentType().toString(), responseHandler);

    }

    public RequestHandle postBinaryForResult(String url, final String methodName, final HttpRequestInterface httpRequestInterface) {

        HttpEntity entity = builder.build();

        return client.post(mCtx, serverUrl + url, entity, entity.getContentType().toString(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                JSONArray response = getResponseJSONArray(responseBody);

                for (int i = 0; i < response.length(); i++) {

                    JSONObject response_item = getItemJSONArray(response, i);

                    if (getStringFromJSON(response_item, "Name").equals(methodName)) {

                        httpRequestInterface.processResponse(getJsonObjectFromJsonObject(response_item, "Result"));

                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                showMessageOnFailure(statusCode, headers, responseBody, error);
            }
        });

    }

    private void addFileToSend(String methodName, String url){

        DB db = new DB(mCtx);

        db.open();

        ContentValues cv = new ContentValues();
        cv.put("method", methodName);
        cv.put("url", url);
        cv.put("path", pathToFile);

        db.insert("filesToSend", null, cv);

        db.close();

    }

    public RequestHandle postBinaryForResultDelayed(String url, final String methodName, final HttpRequestInterface httpRequestInterface) {

        JSONObject request = new JSONObject();

        Boolean success = true;

        if (methodName.equals("setExamFoto")) {

            String[] arStr = url.split("/");

            String oldRef = arStr[arStr.length - 1];

            addFileToSend(methodName, url.replace(oldRef, addRef(oldRef)));

        }
        else if (methodName.equals("setFoto")) {

            addFileToSend(methodName, url);

        }
        else {
            success = false;
        }

        putInJsonObject(request, "Success", success);
        if (!success){
            putInJsonObject(request, "Message", "Invalid method " + methodName);
        }

        httpRequestInterface.setProgressVisibility(View.VISIBLE);
        httpRequestInterface.setProgressVisibility(View.GONE);
        httpRequestInterface.processResponse(request);

        return null;
    }

    protected void verifyDeviceApp(final HttpRequestAuthInterface httpRequestAuthInterface){

        DB db = new DB(mCtx);

        db.open();

        String prog_id = db.getConstant("prog_id");
        if (prog_id == null) {

            prog_id = UUID.randomUUID().toString();
            db.updateConstant("prog_id", prog_id);

        }

        db.close();

        addParam("app", mCtx.getString(R.string.app_name));

        postProc("authorizationDeviceApp", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {
                httpRequestAuthInterface.setProgressVisibility(visibility);
            }

            @Override
            public void processResponse(JSONObject response) {

                JSONObject authorizationJSON = getJsonObjectFromJsonObject(response, "authorization");

                if (getBooleanFromJSON(authorizationJSON, "Success")) {

                    DB db = new DB(mCtx);

                    db.open();

                    String user_id = db.getConstant("user_id");
                    String userRef = getStringFromJSON(authorizationJSON, "Ref");
                    if (user_id == null || !userRef.equals(user_id) ){
                        db.updateConstant("user_id", userRef);
                    }

                    db.close();

                    httpRequestAuthInterface.authorizationSuccess(response);

                }
                else {

                    httpRequestAuthInterface.authorizationFault(response);

                }

            }
        });

    }

    public void addFile(File file){

        pathToFile = file.getPath();

        builder = MultipartEntityBuilder.create();

        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

//        String[]arFilePath = pathFotoDistance.toString().split("/");
//
//        URI uri = URI.create(pathFotoDistance);

        FileBody fileBody = new FileBody(file); //image should be a String
//        FileBody fileBody = new FileBody(new File(Environment.getExternalStorageDirectory(), arFilePath[arFilePath.length - 1])); //image should be a String
        builder.addPart("icon", fileBody);

    }




    public String getResponseString(byte[] responseBody){

        String[] str = new String[1];

        str[0] = null;
        try {
            str[0] = responseBody == null ? "" : new String(responseBody, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return str[0];
    }

    public String dateStrToDate(String date){

        String result = "";
        try {
            result = date.substring(6, 8) + "." + date.substring(4, 6) + "." + date.substring(0, 4);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public JSONArray getResponseJSONArray(byte[] responseBody){

        JSONObject readerArray = new JSONObject();
        try {
            readerArray = new JSONObject(getResponseString(responseBody));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONArray response = getJsonArrayFromJsonObject(readerArray, "Response");

        return response;
    }

    public JSONArray getJsonArrayFromJsonObject(JSONObject readerArray, String name) {
        JSONArray response = new JSONArray();
        try {
            response = (JSONArray) readerArray.get(name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return response;
    }

    public JSONObject getJsonObjectFromJsonObject(JSONObject readerArray, String name) {
        JSONObject response = new JSONObject();
        try {
            response = (JSONObject) readerArray.get(name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return response;
    }

    public JSONObject getItemJSONArray(JSONArray response, Integer i){

        JSONObject response_item = new JSONObject();
        try {
            response_item = (JSONObject) response.get(i);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return response_item;

    }

    public String getStringFromJSON(JSONObject accept_item, String field_name) {
        String date = "";
        try {
            date = accept_item.getString(field_name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return date;
    }

    public Integer getIntegerFromJSON(JSONObject accept_item, String field_name) {

        Integer date = 0;
        try {
            date = accept_item.getInt(field_name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return date;
    }

    public Double getDoubleFromJSON(JSONObject accept_item, String field_name) {

        Double date = 0.;
        try {
            date = accept_item.getDouble(field_name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return date;
    }

    public Long getLongFromJSON(JSONObject accept_item, String field_name) {

        Long date = Long.valueOf(0);
        try {
            date = accept_item.getLong(field_name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return date;
    }

    public Boolean getBooleanFromJSON(JSONObject accept_item, String field_name) {

        Boolean date = false;
        try {
            date = accept_item.getBoolean(field_name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return date;
    }


    public void showMessageOnFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

        String message = error.getLocalizedMessage() + ", status code " + String.valueOf(statusCode) + ": " + getResponseString(responseBody);

//                if (debug){

        try {

            Toast toast = Toast.makeText(mCtx, message, Toast.LENGTH_LONG);
            toast.show();
        } catch (Exception e){};


//            }
//                Log.v("HTTPLOG", message);

    }

    public void showQuestionYesNoCancel(Activity activity, final BundleMethodInterface bundleMethodInterface, final Bundle arguments, String question, String title) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mCtx);
        alertDialogBuilder.setTitle(title);

        alertDialogBuilder.setIcon(R.drawable.sklad96);

        LayoutInflater inflater = activity.getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_yes_no_cancel, null);

        ((TextView) view.findViewById(R.id.tvQuestion)).setText(question);

        alertDialogBuilder.setView(view);
        alertDialogBuilder.setIcon(R.drawable.sklad96);

        final AlertDialog alertDialog = alertDialogBuilder.create();

        view.findViewById(R.id.btnYes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bundleMethodInterface.callMethod(arguments);

                alertDialog.cancel();


            }
        });

        view.findViewById(R.id.btnNo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.cancel();

            }
        });

        view.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.cancel();

            }
        });

        alertDialog.show();



    }

    public void showInputQuantity(Integer quantity, Activity activity, final BundleMethodInterface bundleMethodInterface, final Bundle arguments, String question, String title) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mCtx);
        alertDialogBuilder.setTitle("Ввод количества");

        LayoutInflater inflater = activity.getLayoutInflater();

        final View view = inflater.inflate(R.layout.dialog_input_quantity, null);

        alertDialogBuilder.setTitle(title);

        ((TextView) view.findViewById(R.id.tvCode)).setText(question);

        Button btnQuantity = view.findViewById(R.id.btnQuantity);

        EditText etQuantity = view.findViewById(R.id.etQuantity);

        if(quantity == null){

            btnQuantity.setVisibility(View.GONE);
        }
        else {

            btnQuantity.setVisibility(View.VISIBLE);
            btnQuantity.setText("<<  " + quantity.toString());
        }

        alertDialogBuilder.setView(view);
        alertDialogBuilder.setIcon(R.drawable.sklad96);

        final AlertDialog quantityDialog = alertDialogBuilder.create();

        btnQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                etQuantity.setText(quantity.toString());

                quantityDialog.cancel();

                Integer quantity2 = Integer.valueOf(((TextView) view.findViewById(R.id.etQuantity)).getText().toString());

                arguments.putInt("quantity", quantity2);

                bundleMethodInterface.callMethod(arguments);

            }
        });

        view.findViewById(R.id.btnOK).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String strQuantity = ((TextView) view.findViewById(R.id.etQuantity)).getText().toString();

                if (!strQuantity.isEmpty()) {

                    if (Integer.valueOf(strQuantity) > quantity){

                        etQuantity.setText("");

                    }
                    else {
                        quantityDialog.cancel();

                        Integer quantity2 = Integer.valueOf(strQuantity);

                        arguments.putInt("quantity", quantity2);

                        bundleMethodInterface.callMethod(arguments);
                    }
                }
            }
        });

        quantityDialog.show();

        etQuantity.requestFocus();

        final Handler h2 = new Handler();

        final Runnable setFocus2 = new Runnable() {
            public void run() {

                InputMethodManager imm = (InputMethodManager) quantityDialog.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

                etQuantity.requestFocus();

                imm.showSoftInput(etQuantity, 0, null);

//                h2.postDelayed(setFocus2, 500);

            }
        };

        h2.postDelayed(setFocus2, 500);

//        Thread t = new Thread(new Runnable() {
//            public void run() {
//                h2.post(setFocus2);
//            }
//        });
//        t.start();




    }




}
