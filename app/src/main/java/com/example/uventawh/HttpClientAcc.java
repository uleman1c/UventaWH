package com.example.uventawh;


import android.content.Context;

public class HttpClientAcc extends HttpClient {

    public HttpClientAcc(Context Ctx) {
        super(Ctx);

        setServerUrl(Ctx.getString(R.string.base_acc), Ctx.getString(R.string.hs_acc));

    }

}
