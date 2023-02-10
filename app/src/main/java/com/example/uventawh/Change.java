package com.example.uventawh;

import org.json.JSONException;
import org.json.JSONObject;

public class Change {

    public String _Тип;
    public String _Вид;
    public String _Ссылка;

    public JSONObject getJSONObject() throws JSONException {

        JSONObject jResult = new JSONObject();
        jResult.put("_Тип", _Тип);
        jResult.put("_Вид", _Вид);
        jResult.put("_Ссылка", _Ссылка);

        return jResult;

    }
}
