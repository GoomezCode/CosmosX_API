package com.goomez.CosmosX.model;

public class Astronaut {
    private long id;
    private String name;
    private String rank;
    private int experience;

    public Astronaut() {}

    public Astronaut(long id, String name, String rank, int experience) {
        this.id = id;
        this.name = name;
        this.rank = rank;
        this.experience = experience;
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

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }
}
