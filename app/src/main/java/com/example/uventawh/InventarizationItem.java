package com.example.uventawh;

import org.json.JSONObject;

public class InventarizationItem {

    String ref;
    String number;
    String date;
    String cell;
    String container;
    String product;
    String status;

    public InventarizationItem(String ref, String number, String date, String cell, String container, String product, String status){

        this.ref = ref;
        this.number = number;
        this.date = date;
        this.cell = cell;
        this.container = container;
        this.product = product;
        this.status = status;
    }

    public static InventarizationItem extractFromJSON(JSONObject deliveryOrderJSON, HttpClient client1) {

        String ref = client1.getStringFromJSON(deliveryOrderJSON, "Ref");
        String number = client1.getStringFromJSON(deliveryOrderJSON, "Number");
        String date = client1.getStringFromJSON(deliveryOrderJSON, "Date");
        String cell = client1.getStringFromJSON(deliveryOrderJSON, "Cell");
        String container = client1.getStringFromJSON(deliveryOrderJSON, "Container");
        String product = client1.getStringFromJSON(deliveryOrderJSON, "Product");
        String status = client1.getStringFromJSON(deliveryOrderJSON, "Status");

        return new InventarizationItem(ref, number, client1.dateStrToDate(date), cell, container, product, status);
    }



}
