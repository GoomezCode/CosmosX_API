package com.goomez.CosmosX.Model;

public class spacecraftModel {
    private long id;
    private String name;
    private int fuel;
    private int capacity;
    private String status;

    public spacecraftModel(){}

    public spacecraftModel(long id, String name, int fuel, int capacity, String status) {
        this.id = id;
        this.name = name;
        this.fuel = fuel;
        this.capacity = capacity;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFuel() {
        return fuel;
    }

    public void setFuel(int fuel) {
        this.fuel = fuel;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
