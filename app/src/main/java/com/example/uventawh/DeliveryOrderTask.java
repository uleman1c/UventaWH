package com.example.uventawh;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class DeliveryOrderTask {

//         Ref" -> "34f03fe6-59c0-11ec-819c-74e6e2fa6c43"
//                "Status" -> "К выполнению"
//                "Product" -> "Мешкозашивочная машина Aurora GK9-200"
//                "ShtrihCode" -> "2150000874144"
//                "Quantity" -> "2"
//                "ProductStatus" -> ""
//                "ProductPart" -> ""
//                "Container" -> "211122.000471"
//                "ContainerShtrihCode" -> "211122.000471"
//                "Cell" -> "06.09.10.1"
//                "CellShtrihCode" -> "0609101"

    public String ref, status, product, product_status, product_part, container, container_shtrih_code, cell, cell_shtrih_code;
    public Integer quantity, level, scanned;
    public Boolean childExist, serialNumberExist;
    public ArrayList<String> shtrih_codes;
    public ArrayList<ShtrihPack> shtrihPacks;


    public DeliveryOrderTask(String ref, String status, String product, ArrayList<String> shtrih_codes, String product_status,
                             String product_part, String container, String container_shtrih_code, String cell, String cell_shtrih_code,
                             Integer quantity, Boolean serialNumberExist, ArrayList<ShtrihPack> shtrihPacks) {
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
        this.shtrihPacks = shtrihPacks;
    }

    public static DeliveryOrderTask DeliveryOrderTaskFromJSON(JSONObject js, HttpClient ht){

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

        ArrayList<ShtrihPack> shtrihPacks = new ArrayList<>();
        JSONArray sc2 = ht.getJsonArrayFromJsonObject(js, "ShtrihCodesAll");
        for (int i = 0; i < sc2.length(); i++) {

            shtrihPacks.add(ShtrihPack.ShtrihPackFromJson(ht.getItemJSONArray(sc2, i), ht));

        }

        return new DeliveryOrderTask(ref, status, product, shtrih_codes, product_status, product_part, container, container_shtrih_code,
                cell, cell_shtrih_code, quantity, serialNumberExist, shtrihPacks);
    }


}

