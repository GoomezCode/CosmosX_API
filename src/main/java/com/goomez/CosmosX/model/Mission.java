package com.goomez.CosmosX.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Mission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long spacecraftId;
    private long planetId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "mission_astronauts", joinColumns = @JoinColumn(name = "mission_id"))
    private List<Long> astronauts;

    private String status;
    private int fuelConsumed;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "mission_resources_found", joinColumns = @JoinColumn(name = "mission_id"))
    private List<ResourceFound> resourcesFound = new ArrayList<>();

    private String completedAt;

    public Mission() {}

    public Mission(long id, long spacecraftId, long planetId, List<Long> astronauts, String status) {
        this.id = id;
        this.spacecraftId = spacecraftId;
        this.planetId = planetId;
        this.astronauts = new ArrayList<>(astronauts);
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
        this.astronauts = astronauts == null ? null : new ArrayList<>(astronauts);
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
        this.resourcesFound = resourcesFound == null ? new ArrayList<>() : new ArrayList<>(resourcesFound);
    }

    public String getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(String completedAt) {
        this.completedAt = completedAt;
    }
}