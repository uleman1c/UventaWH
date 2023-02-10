package com.example.uventawh;

import android.location.Location;

public interface LocationRequestInterface {

    void onSuccess(Location location);
    void onFailure(String provider);

}
