package com.example.uventawh;

import org.json.JSONObject;

public interface HttpRequestInterface {

    void setProgressVisibility(int visibility);
    void processResponse(JSONObject response);

}
