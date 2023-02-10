package com.example.uventawh;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class GetFoto {

    public Uri uri;
    public Intent intent;
    public File file;

    private File createImageFile(Context context, String ext) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                UUID.randomUUID().toString(),  /* prefix */
                ext,         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
//        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void GetFotoWithExt(Context context, String ext){

        if (Build.VERSION.SDK_INT >= 24) {

            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                // Create the File where the photo should go
                file = null;
                try {
                    file = createImageFile(context, ext);
                } catch (IOException ex) {
                    // Error occurred while creating the File
                    //...
                }
                // Continue only if the File was successfully created
                if (file != null) {
                    uri = FileProvider.getUriForFile(context,context.getApplicationContext().getPackageName() + ".provider", file);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                    //                    startActivityForResult(takePictureIntent, CAMERA_REQUEST);

                } else {
                    intent = null;
                }
            } else {
                intent = null;
            }

        } else {

            file = new File(Environment.getExternalStorageDirectory(), UUID.randomUUID().toString() + ext);

            uri = Uri.fromFile(file);

            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

            if (intent.resolveActivity(context.getPackageManager()) == null) {

                intent = null;

            }

        }

    }

    public GetFoto(Context context) {

        GetFotoWithExt(context, ".jpg");


    }

    public GetFoto(Context context, String ext) {

        GetFotoWithExt(context, ext);

    }


}
