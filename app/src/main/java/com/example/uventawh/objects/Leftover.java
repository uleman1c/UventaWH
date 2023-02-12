package com.example.uventawh.objects;

import com.example.uventawh.HttpClient;

import org.json.JSONObject;

public class Leftover {

    public String productRef, productDescription,
            containerRef, containerDescription,
            cellType, cellRef, cellDescription;
    public Double number, numberIncome, numberOutcome;


    public Leftover(String productRef, String productDescription, String containerRef, String containerDescription,
                    String cellType, String cellRef, String cellDescription, Double number, Double numberIncome, Double numberOutcome) {
        this.productRef = productRef;
        this.productDescription = productDescription;
        this.containerRef = containerRef;
        this.containerDescription = containerDescription;
        this.cellType = cellType;
        this.cellRef = cellRef;
        this.cellDescription = cellDescription;
        this.number = number;
        this.numberIncome = numberIncome;
        this.numberOutcome = numberOutcome;
    }

    public static Leftover LeftoverFromJSON(JSONObject js, HttpClient ht){

        String productRef = ht.getStringFromJSON(js, "ProductRef");
        String productDescription = ht.getStringFromJSON(js, "ProductDescription");
        String containerRef = ht.getStringFromJSON(js, "ContainerRef");
        String containerDescription = ht.getStringFromJSON(js, "ContainerDescription");
        String cellType = ht.getStringFromJSON(js, "CellType");
        String cellRef = ht.getStringFromJSON(js, "CellRef");
        String cellDescription = ht.getStringFromJSON(js, "CellDescription");
        Double number = ht.getDoubleFromJSON(js, "Number");
        Double numberIncome = ht.getDoubleFromJSON(js, "NumberIncome");
        Double numberOutcome = ht.getDoubleFromJSON(js, "NumberOutcome");

        return new Leftover(productRef, productDescription, containerRef, containerDescription,
                cellType, cellRef, cellDescription, number, numberIncome, numberOutcome);
    }


}
