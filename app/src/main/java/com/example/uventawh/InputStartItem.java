package com.example.uventawh;

import org.json.JSONObject;

public class InputStartItem {

    String ref;
    String date;
    String container;
    String product;
    int quantity;

    public InputStartItem(String ref, String date, String container, String product, Integer quantity){

        this.ref = ref;
        this.date = date;
        this.container = container;
        this.product = product;
        this.quantity = quantity;
    }

    public static InputStartItem extractFromJSON(JSONObject deliveryOrderJSON, HttpClient client1) {

        String ref = client1.getStringFromJSON(deliveryOrderJSON, "Ref");
        String date = client1.getStringFromJSON(deliveryOrderJSON, "Date");
        String container = client1.getStringFromJSON(deliveryOrderJSON, "Container");
        String product = client1.getStringFromJSON(deliveryOrderJSON, "Product");
        Integer quantity = client1.getIntegerFromJSON(deliveryOrderJSON, "Quantity");

        return new InputStartItem(ref, client1.dateStrToDate(date), container, product, quantity);
    }



}
