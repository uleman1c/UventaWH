package com.example.uventawh;

public class Contractor {

    public String ref;
    public String description;

    public Boolean inputQuantity, addRoute;

    Contractor(String ref, String description){

        this.ref = ref;
        this.description = description;

    }

    Contractor(String ref, String description, Boolean inputQuantity) {
        this.ref = ref;
        this.description = description;
        this.inputQuantity = inputQuantity;
    }

    Contractor(String ref, String description, Boolean inputQuantity, Boolean addRoute) {
        this.ref = ref;
        this.description = description;
        this.inputQuantity = inputQuantity;
        this.addRoute = addRoute;
    }

}
