package com.goomez.CosmosX.model;

import java.util.List;

public class Mission {
    private long id;
    private long planetId;
    private List<Long> astronauts;
    private String status;

    public Mission() {}

    public Mission(long id, long planetId, List<Long> astronauts, String status) {
        this.id = id;
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
}
