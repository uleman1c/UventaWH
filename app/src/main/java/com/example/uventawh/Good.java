package com.example.uventawh;

public class Good {

    public String ref, description;
    public Double quantity;

    Good(String ref, String description){

        this.ref = ref;
        this.description = description;
        this.quantity = 0.;

    }

    public Good(String ref, String description, Double quantity) {
        this.ref = ref;
        this.description = description;
        this.quantity = quantity;
    }
}
