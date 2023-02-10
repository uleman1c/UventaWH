package com.example.uventawh;

public class ScannedShtrihCode {

    public String shtrihCode, date;
    public Boolean added, isPalet;
    public Double quantity;


    public ScannedShtrihCode(String shtrihCode, String date, Boolean added) {
        this.shtrihCode = shtrihCode;
        this.date = date;
        this.added = added;
    }

    public ScannedShtrihCode(String shtrihCode, String date, Boolean added, Double quantity) {
        this.shtrihCode = shtrihCode;
        this.date = date;
        this.added = added;
        this.quantity = quantity;
    }

    public ScannedShtrihCode(String shtrihCode, String date, Boolean added, Boolean isPalet) {
        this.shtrihCode = shtrihCode;
        this.date = date;
        this.added = added;
        this.isPalet = isPalet;
    }

    public ScannedShtrihCode(String shtrihCode, String date, Boolean added, Double quantity, Boolean isPalet) {
        this.shtrihCode = shtrihCode;
        this.date = date;
        this.added = added;
        this.quantity = quantity;
        this.isPalet = isPalet;
    }

    public String getDateStr() {

        String result = "";
        try {
            result = date.substring(6, 8) + "." + date.substring(4, 6) + "." + date.substring(0, 4);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public String getTimeStr() {

        String result = "";
        try {
            result = date.substring(8, 10) + ":" + date.substring(10, 12) + ":" + date.substring(12, 14);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
