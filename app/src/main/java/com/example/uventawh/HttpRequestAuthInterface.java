package com.example.uventawh;

import org.json.JSONObject;

public interface HttpRequestAuthInterface {

    void setProgressVisibility(int visibility);
    void authorizationSuccess(JSONObject response);
    void authorizationFault(JSONObject response);

}
