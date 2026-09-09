package com.goomez.CosmosX.model;

public class ResourceFound {
    private String resource;
    private int quantity;

    public ResourceFound() {}

    public ResourceFound(String resource, int quantity) {
        this.resource = resource;
        this.quantity = quantity;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}