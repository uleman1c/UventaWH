package com.example.uventawh;

public class Invent extends Adapterable {

    String ref, date, contractor, contractorRef;

    public Invent(String ref, String date, String contractor, String contractorRef) {
        this.ref = ref;
        this.date = date;
        this.contractor = contractor;
        this.contractorRef = contractorRef;
    }

    @Override
    public String getField(String key) {
        String result = "";

        if (key.equals("date")){
            result = StrDateTime.strToDate(date) + " " + StrDateTime.strToTime(date);
        }
        if (key.equals("contractor")){
            result = contractor;
        }

        return result;
    }
}
