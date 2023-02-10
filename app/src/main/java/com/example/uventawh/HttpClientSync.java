package com.example.uventawh;


import android.content.Context;

public class HttpClientSync extends HttpClient {

    public HttpClientSync(Context Ctx) {
        super(Ctx, true);

        setServerUrl(Ctx.getString(R.string.base_wms), Ctx.getString(R.string.hs_wms));

    }


}
