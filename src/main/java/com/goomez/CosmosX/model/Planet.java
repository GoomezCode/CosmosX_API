package com.goomez.CosmosX.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;

import java.util.List;

@Entity
public class Planet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private int distance;
    private int dangerLevel;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "planet_resources", joinColumns = @JoinColumn(name = "planet_id"))
    private List<String> resources;

    private String discoveredAt;

    public Planet() {}

    public Planet(long id, String name, int distance, int dangerLevel, List<String> resources) {
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

    public String getDiscoveredAt() {
        return discoveredAt;
    }

    public void setDiscoveredAt(String discoveredAt) {
        this.discoveredAt = discoveredAt;
    }
}