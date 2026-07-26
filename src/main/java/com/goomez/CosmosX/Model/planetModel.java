package com.goomez.CosmosX.Model;

import java.util.List;

public class planetModel {
    private long id;
    private String name;
    private int distance;
    private int dangerLevel;
    private List<String> resources;

    public planetModel(){}

    public planetModel(long id, String name, int distance, int dangerLevel, List<String> resources) {
        this.id = id;
        this.name = name;
        this.distance = distance;
        this.dangerLevel = dangerLevel;
        this.resources = resources;
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

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getDangerLevel() {
        return dangerLevel;
    }

    public void setDangerLevel(int dangerLevel) {
        this.dangerLevel = dangerLevel;
    }

    public List<String> getResources() {
        return resources;
    }

    public void setResources(List<String> resources) {
        this.resources = resources;
    }
}
