package com.goomez.CosmosX.model;

import java.util.ArrayList;
import java.util.List;

public class Mission {
    private long id;
    private long spacecraftId;
    private long planetId;
    private List<Long> astronauts;
    private String status;
    private int fuelConsumed;
    private List<ResourceFound> resourcesFound = new ArrayList<>();
    private String completedAt;

    public Mission() {}

    public Mission(long id, long spacecraftId, long planetId, List<Long> astronauts, String status) {
        this.id = id;
        this.spacecraftId = spacecraftId;
        this.planetId = planetId;
        this.astronauts = astronauts;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSpacecraftId() {
        return spacecraftId;
    }

    public void setSpacecraftId(long spacecraftId) {
        this.spacecraftId = spacecraftId;
    }

    public long getPlanetId() {
        return planetId;
    }

    public void setPlanetId(long planetId) {
        this.planetId = planetId;
    }

    public List<Long> getAstronauts() {
        return astronauts;
    }

    public void setAstronauts(List<Long> astronauts) {
        this.astronauts = astronauts;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getFuelConsumed() {
        return fuelConsumed;
    }

    public void setFuelConsumed(int fuelConsumed) {
        this.fuelConsumed = fuelConsumed;
    }

    public List<ResourceFound> getResourcesFound() {
        return resourcesFound;
    }

    public void setResourcesFound(List<ResourceFound> resourcesFound) {
        this.resourcesFound = resourcesFound;
    }

    public String getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(String completedAt) {
        this.completedAt = completedAt;
    }
}
