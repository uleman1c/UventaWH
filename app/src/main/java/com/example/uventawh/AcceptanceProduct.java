package com.example.uventawh;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class AcceptanceProduct {

    public String product, ref, container, product_status, product_part;
    public Integer quantity;
    public ArrayList<String> shtrih_codes;
    public ArrayList<String> serialNumbers;
    public Boolean serial_number_exist, isContainer, party_number_exist, party_date_exist, party_date_expired_exist, party_date_produced_exist;
    public String party_number, party_date, party_date_expired, party_date_produced;

    public AcceptanceProduct(String product, String ref, String container, String product_status, String product_part, Integer quantity,
                             Boolean serial_number_exist, ArrayList<String> shtrih_codes,
                             Boolean party_number_exist, Boolean party_date_exist, Boolean party_date_expired_exist, Boolean party_date_produced_exist) {
        this.product = product;
        this.ref = ref;
        this.container = container;
        this.product_status = product_status;
        this.product_part = product_part;
        this.quantity = quantity;
        this.serial_number_exist = serial_number_exist;
        this.shtrih_codes = shtrih_codes;
        this.isContainer = false;
        this.serialNumbers = new ArrayList<>();
        this.party_number_exist = party_number_exist;
        this.party_date_exist = party_date_exist;
        this.party_date_expired_exist = party_date_expired_exist;
        this.party_date_produced_exist = party_date_produced_exist;
    }

    public static AcceptanceProduct AcceptanceProductFromJSON(JSONObject js, HttpClient ht){

        String product = ht.getStringFromJSON(js, "Product");
        String ref = ht.getStringFromJSON(js, "ProductRef");
        String container = ht.getStringFromJSON(js, "Container");
        String product_status = ht.getStringFromJSON(js, "ProductStatus");
        String product_part = ht.getStringFromJSON(js, "ProductPart");
        Integer quantity = ht.getIntegerFromJSON(js, "Quantity");
        Boolean serial_number_exist = ht.getBooleanFromJSON(js, "SerialNumberExist");
        JSONArray ar_shtrih_codes = ht.getJsonArrayFromJsonObject(js, "ShtrihCodes");
        Boolean party_number_exist = ht.getBooleanFromJSON(js, "PartyNumberExist");
        Boolean party_date_exist = ht.getBooleanFromJSON(js, "PartyDateExist");
        Boolean party_date_expired_exist = ht.getBooleanFromJSON(js, "PartyDateExpiredExist");
        Boolean party_date_produced_exist = ht.getBooleanFromJSON(js, "PartyDateProducedExist");

        ArrayList<String> shtrih_codes = new ArrayList<>();

        for (int i = 0; i < ar_shtrih_codes.length(); i++) {

            shtrih_codes.add(ht.getStringFromJSON(ht.getItemJSONArray(ar_shtrih_codes, i), "ShtrihCode"));

        }

        return new AcceptanceProduct(product, ref, container, product_status, product_part, quantity, serial_number_exist, shtrih_codes,
                party_number_exist, party_date_exist, party_date_expired_exist, party_date_produced_exist);
    }


}

