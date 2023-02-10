package com.example.uventawh;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import org.json.JSONObject;

import java.io.File;

public class ExchangeService extends Service {

    private Integer delay = 10000;
    Handler hPostChanges;

    Handler.Callback hcPostChanges = new Handler.Callback() {

        public boolean handleMessage(Message msg) {

            postChanges();

            return false;
        }

    };

    public ExchangeService() {
    }

    @Override
    public void onCreate() {

        super.onCreate();

        hPostChanges = new Handler(hcPostChanges);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        postChanges();

        return super.onStartCommand(intent, flags, startId);
    }

    public void postChanges() {

        DB db = new DB(getContext());

        db.open();

        if (db.getConstant("exchanging").equals("false")) {

            db.updateConstant("exchanging", "true");

            postNexChanges();

        }
        db.close();

    }

    public void postNexChanges() {

        DB db = new DB(getContext());

        db.open();

        Cursor cursor = db.getAllData("requests", "_id");
        if (cursor.moveToFirst()) {

            int _id = cursor.getInt(cursor.getColumnIndex("_id"));
            String method = cursor.getString(cursor.getColumnIndex("method"));
            String params = cursor.getString(cursor.getColumnIndex("params"));
            String response = cursor.getString(cursor.getColumnIndex("response"));

            try {
                sendRequest(_id, method, params, response);
            } catch (Exception e) {
                sendError(e.getLocalizedMessage());
            }

        } else {

            cursor = db.getAllData("filesToSend", "_id");

            if (cursor.moveToFirst()) {

                int _id = cursor.getInt(cursor.getColumnIndex("_id"));
                String method = cursor.getString(cursor.getColumnIndex("method"));
                String url = cursor.getString(cursor.getColumnIndex("url"));
                String path = cursor.getString(cursor.getColumnIndex("path"));

                try {

                    sendFileRequest(_id, method, url, path);
                } catch (Exception e) {
                    sendError(e.getLocalizedMessage());

                }

            } else {

                db.updateConstant("exchanging", "false");

                hPostChanges.sendEmptyMessageDelayed(1, delay);
            }
        }

        cursor.close();

        db.close();

    }

    private void sendRequest(final int _id, final String method, final String params, final String oldResponse) {

        final HttpClient httpClient = new HttpClient(getContext());
        httpClient.setParamsFromString(params);

        httpClient.postForResult(method, new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {

            }

            @Override
            public void processResponse(JSONObject response) {

                if (method.equals("addExam")){

                    if(httpClient.getBooleanFromJSON(response, "Success")) {

                        String ref = httpClient.getStringFromJSON(response, "Ref");

                        processAddExam(ref, oldResponse, _id);

                    }
                }
                else if (method.equals("setExamStep1")){

                    if(httpClient.getBooleanFromJSON(response, "Success")) {

                        processSetExamStep1(_id);

                    }
                }
                else if (method.equals("getIncomingDocByParameters")){

                    if(httpClient.getBooleanFromJSON(response, "Success")) {

                        String ref = httpClient.getStringFromJSON(response, "Ref");

                        processGetIncomingDocByParameters(ref, oldResponse, _id);

                    }
                }
                else if (method.equals("getIncomingDocRoutePointFoto")){

                    if(httpClient.getBooleanFromJSON(response, "Success")) {

                        String ref = httpClient.getStringFromJSON(response, "Ref");

                        processGetIncomingDocByParameters(ref, oldResponse, _id);

                    }
                }
                else if (method.equals("setExamStep2")){

                    if(httpClient.getBooleanFromJSON(response, "Success")) {

                        processSetExamStep1(_id);

                    }
                }
                else if (method.equals("setExamStep3")){

                    if(httpClient.getBooleanFromJSON(response, "Success")) {

                        processSetExamStep1(_id);

                    }
                }
                else if (method.equals("setExamStep4")){

                    if(httpClient.getBooleanFromJSON(response, "Success")) {

                        processSetExamStep1(_id);

                    }
                }
                else if (method.equals("setExamStep5")){

                    if(httpClient.getBooleanFromJSON(response, "Success")) {

                        processSetExamStep1(_id);

                    }
                }
                else if (method.equals("setExamStep6")){

                    if(httpClient.getBooleanFromJSON(response, "Success")) {

                        processSetExamStep1(_id);

                    }
                }
                else if (method.equals("setDamage")){

                    if(httpClient.getBooleanFromJSON(response, "Success")) {

                        String ref = httpClient.getStringFromJSON(response, "Ref");

                        processSetDamage(ref, oldResponse, _id);

                    }
                }
                else if (method.equals("setMoneyReport")){

                    if(httpClient.getBooleanFromJSON(response, "Success")) {

                        String ref = httpClient.getStringFromJSON(response, "Ref");

                        processsetMoneyReport(ref, oldResponse, _id);

                    }
                }
                else if (method.equals("setFuelAdd")){

                    if(httpClient.getBooleanFromJSON(response, "Success")) {

                        String ref = httpClient.getStringFromJSON(response, "Ref");

                        processsetsetFuelAdd(ref, oldResponse, _id);

                    }
                }
                else if (method.equals("setRouteDocAdd")){

                    if(httpClient.getBooleanFromJSON(response, "Success")) {

                        String ref = httpClient.getStringFromJSON(response, "Ref");

                        processsetsetFuelAdd(ref, oldResponse, _id);

                    }
                }
                else if (method.equals("setExecuteAddress")){

                    if(httpClient.getBooleanFromJSON(response, "Success")) {

                        processsetsetExecuteAddress(_id);

                    }
                }
                else if (method.equals("setRoutePointDateStart")){

                    if(httpClient.getBooleanFromJSON(response, "Success")) {

                        processsetRoutePointDateStart(_id);

                    }
                }
                else if (method.equals("setRoutePointDriverData")){

                    if(httpClient.getBooleanFromJSON(response, "Success")) {

                        processsetRoutePointDriverData(_id);

                    }
                }

                postNexChanges();

            }
        });

    }

    private void processsetRoutePointDriverData(int id) {

        removeRequest(id);

    }

    private void processsetRoutePointDateStart(int id) {

        removeRequest(id);

    }

    private void processsetsetExecuteAddress(int id) {

        removeRequest(id);

    }

    private void processsetsetFuelAdd(String ref, String oldResponse, int id) {

        setExternalRef(ref, oldResponse);

        removeRequest(id);

    }

    private void processsetMoneyReport(String ref, String oldResponse, int id) {

        setExternalRef(ref, oldResponse);

        removeRequest(id);

    }

    private Context getContext() {
        return this;
    }

    private void setExternalRef(String ref, String oldResponse) {

        DB db = new DB(getContext());

        db.open();

        ContentValues contentValues = new ContentValues();
        contentValues.put("internal", oldResponse);
        contentValues.put("external", ref);
        db.update("refs", contentValues, "internal = ?", new String[] { oldResponse });

        Cursor cursor = db.getAllDataByFilter("requests", "params LIKE ?", new String[] { "%" + oldResponse + "%" }, null);

        if (cursor.moveToFirst()){

            do {

                contentValues = new ContentValues();
                contentValues.put("params", cursor.getString(cursor.getColumnIndex("params")).replace(oldResponse, ref));
                db.update("requests", contentValues, "_id = ?", new String[] { cursor.getString(cursor.getColumnIndex("_id")) });


            } while (cursor.moveToNext());

        }

        cursor.close();

        cursor = db.getAllDataByFilter("filesToSend", "url LIKE ?", new String[] { "%" + oldResponse + "%" }, null);

        if (cursor.moveToFirst()){

            do {

                contentValues = new ContentValues();
                contentValues.put("url", cursor.getString(cursor.getColumnIndex("url")).replace(oldResponse, ref));
                db.update("filesToSend", contentValues, "_id = ?", new String[] { cursor.getString(cursor.getColumnIndex("_id")) });

            } while (cursor.moveToNext());

        }

        cursor.close();

    }

    private void removeFromTable(String table, int _id) {

        DB db = new DB(getContext());

        db.open();

        db.delRec(table, _id);

        db.close();
    }

    private void removeRequest(int _id) {

        removeFromTable("requests", _id);

    }

    private void processAddExam(String ref, String oldResponse, int _id) {

        setExternalRef(ref, oldResponse);

        removeRequest(_id);

    }

    private void processSetExamStep1(int _id) {

        removeRequest(_id);

    }

    private void processSetDamage(String ref, String oldResponse, int _id) {


        setExternalRef(ref, oldResponse);

        removeRequest(_id);

    }

    private void processGetIncomingDocByParameters(String ref, String oldResponse, int _id) {


        setExternalRef(ref, oldResponse);

        removeRequest(_id);

    }

    private void sendFileRequest(final int _id, final String method, final String url, final String path) {

        final HttpClient httpClient = new HttpClient(getContext());
        httpClient.addFile(new File(path));

        httpClient.postBinaryForResult(url, method, new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {

            }

            @Override
            public void processResponse(JSONObject response) {

                if (httpClient.getBooleanFromJSON(response, "Success")) {

                    new File(path).delete();

                    if (method.equals("setExamFoto")) {

                        String ref = httpClient.getStringFromJSON(response, "Ref");

                        processSetExamFoto(_id);

                    }
                    else if (method.equals("setFoto")) {

                        processSetFoto(_id);

                    }

                }

                postNexChanges();

            }
        });

    }

    private void processSetExamFoto(int _id) {

        removeFromTable("filesToSend", _id);
    }

    private void processSetFoto(int _id) {

        removeFromTable("filesToSend", _id);
    }

    private void sendError(String error) {

        Intent intent = new Intent(MainWareHouseActivity.BROADCAST_ACTION);
        intent.putExtra("error", error);
        sendBroadcast(intent);

    }

    @Override
    public void onDestroy() {

        super.onDestroy();

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
