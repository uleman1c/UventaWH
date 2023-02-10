package com.example.uventawh;

import org.json.JSONObject;

public class ShtrihPack {

    public String shtrih, pack, status, part;
    public Integer range, quantity;


    public ShtrihPack(String shtrih, String pack, String status, String part, Integer range, Integer quantity) {
        this.shtrih = shtrih;
        this.pack = pack;
        this.status = status;
        this.part = part;
        this.range = range;
        this.quantity = quantity;
    }

    public static ShtrihPack ShtrihPackFromJson(JSONObject js, HttpClient ht){

        String shtrih = ht.getStringFromJSON(js, "ShtrihCode");
        String pack = ht.getStringFromJSON(js, "Pack");
        String status = ht.getStringFromJSON(js, "Status");
        String part = ht.getStringFromJSON(js, "Part");
        Integer range = ht.getIntegerFromJSON(js, "Range");
        Integer quantity = ht.getIntegerFromJSON(js, "Quantity");

        return new ShtrihPack(shtrih, pack, status, part, range, quantity);
    }
}
