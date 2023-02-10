package com.example.uventawh;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class TakeScreenShot{

    public static void Do(Context context, View view, float x, float y) {

        if (DB.getConstant(context, "screenShots").equals("true")) {

            View v1 = view.getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            Paint paint = new Paint();
            paint.setStyle(Paint.Style.FILL);
            // Use Color.parseColor to define HTML colors
            paint.setColor(Color.YELLOW);

            Canvas canvas = new Canvas(bitmap);

//        canvas.drawPaint(paint);
            canvas.drawCircle(x, y, 30, paint);
            paint.setColor(Color.WHITE);
            canvas.drawCircle(x, y, 20, paint);
            paint.setColor(Color.YELLOW);
            canvas.drawCircle(x, y, 10, paint);


            GetFoto getFoto = new GetFoto(context);

            FileOutputStream outputStream = null;
            try {
                outputStream = new FileOutputStream(getFoto.file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            int quality = 100;

            if (outputStream != null) {

                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
                try {
                    outputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                sendFotoInDocRef(context, "log/" + StrDateTime.dateToStrMs(new Date()) + "/" + DB.getProgId(context), "setFoto", getFoto.file);
            }
        }

    }

    public static void Do(Context context, View view, View button){

        int[] location = new int[2];
        button.getLocationInWindow(location);
        int x = location[0] + button.getWidth() / 2;
        int y = location[1] + button.getHeight() / 2;

        Do(context, view, x, y);

    }

    public static void DoBack(Context context, View view){

        Do(context, view, 60, view.getHeight() - 60);

    }

    private static void sendFotoInDocRef(Context context, final String url, final String methodName, File file){

        final HttpClient httpClient = new HttpClient(context);
        httpClient.addFile(file);

        httpClient.postBinaryForResultDelayed(url, methodName, new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {
//                progressBar.setVisibility(visibility);
            }

            @Override
            public void processResponse(JSONObject response) {

                if (httpClient.getBooleanFromJSON(response, "Success")) {

//                    fotos.remove(fotos.size() - 1);
//
//                    if (fotos.size() == 0){
//
//                        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).popBackStack();
//                    }
//                    else {
//
//                        sendFotoInDocRef(refInDoc, pathInDoc);
//
//                    }


                }
            }
        });

    }


    public static void DoUp(Context context, View view) {

        Do(context, view, 60, 60);

    }
}


