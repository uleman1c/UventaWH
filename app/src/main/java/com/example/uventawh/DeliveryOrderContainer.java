package com.example.uventawh;

import org.json.JSONObject;

public class DeliveryOrderContainer {

    public String ref, status, product, shtrih_code, product_status, product_part, container, container_shtrih_code;
    public Integer quantity;

    public DeliveryOrderContainer(String ref, String status, String product, String shtrih_code, String product_status, String product_part, String container, String container_shtrih_code, Integer quantity) {
        this.ref = ref;
        this.status = status;
        this.product = product;
        this.shtrih_code = shtrih_code;
        this.product_status = product_status;
        this.product_part = product_part;
        this.container = container;
        this.container_shtrih_code = container_shtrih_code;
        this.quantity = quantity;
    }

    public static DeliveryOrderContainer DeliveryOrderContainerFromJSON(JSONObject js, HttpClient ht){

        String ref = ht.getStringFromJSON(js, "Ref");
        String status = ht.getStringFromJSON(js, "Status");
        String product = ht.getStringFromJSON(js, "Product");
        String shtrih_code = ht.getStringFromJSON(js, "ShtrihCode");
        String product_status = ht.getStringFromJSON(js, "ProductStatus");
        String product_part = ht.getStringFromJSON(js, "ProductPart");
        String container = ht.getStringFromJSON(js, "Container");
        String container_shtrih_code = ht.getStringFromJSON(js, "ContainerShtrihCode");
        Integer quantity = ht.getIntegerFromJSON(js, "Quantity");

        return new DeliveryOrderContainer(ref, status, product, shtrih_code, product_status, product_part, container, container_shtrih_code, quantity);
    }


}

