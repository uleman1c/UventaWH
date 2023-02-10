package com.example.uventawh;

public class ScanCode extends Adapterable {

    public String scanCode;

    public ScanCode(String scanCode) {
        this.scanCode = scanCode;
    }

    @Override
    public String getField(String key) {

        String result = "";

        if (key.equals("scanCode")){
            result = scanCode;
        }

        return result;
    }


}
