package com.example.uventawh;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class RefillTask {

    public String type, ref, status, product, product_status, product_part, container, container_shtrih_code, cell, cell_shtrih_code;
    public Integer quantity, level, scanned;
    public Boolean childExist, serialNumberExist;
    public ArrayList<String> shtrih_codes;


    public RefillTask(String type, String ref, String status, String product, ArrayList<String> shtrih_codes, String product_status,
                      String product_part, String container, String container_shtrih_code, String cell, String cell_shtrih_code,
                      Integer quantity, Boolean serialNumberExist) {
        this.type = type;
        this.ref = ref;
        this.status = status;
        this.product = product;
        this.shtrih_codes = shtrih_codes;
        this.product_status = product_status;
        this.product_part = product_part;
        this.container = container;
        this.container_shtrih_code = container_shtrih_code;
        this.cell = cell;
        this.cell_shtrih_code = cell_shtrih_code;
        this.quantity = quantity;
        this.level = 0;
        this.childExist = false;
        this.scanned = 0;
        this.serialNumberExist = serialNumberExist;
    }

    public static RefillTask RefillTaskFromJSON(JSONObject js, HttpClient ht){

        String type = ht.getStringFromJSON(js, "Type");
        String ref = ht.getStringFromJSON(js, "Ref");
        String status = ht.getStringFromJSON(js, "Status");
        String product = ht.getStringFromJSON(js, "Product");
        String product_status = ht.getStringFromJSON(js, "ProductStatus");
        String product_part = ht.getStringFromJSON(js, "ProductPart");
        String container = ht.getStringFromJSON(js, "Container");
        String container_shtrih_code = ht.getStringFromJSON(js, "ContainerShtrihCode");
        String cell = ht.getStringFromJSON(js, "Cell");
        String cell_shtrih_code = ht.getStringFromJSON(js, "CellShtrihCode");
        Integer quantity = ht.getIntegerFromJSON(js, "Quantity");
        Boolean serialNumberExist = ht.getBooleanFromJSON(js, "SerialNumberExist");

        ArrayList<String> shtrih_codes = new ArrayList<>();
        JSONArray sc = ht.getJsonArrayFromJsonObject(js, "ShtrihCodes");
        for (int i = 0; i < sc.length(); i++) {

            shtrih_codes.add(ht.getStringFromJSON(ht.getItemJSONArray(sc, i), "ShtrihCode"));

        }

        return new RefillTask(type, ref, status, product, shtrih_codes, product_status, product_part, container, container_shtrih_code,
                cell, cell_shtrih_code, quantity, serialNumberExist);
    }


}

