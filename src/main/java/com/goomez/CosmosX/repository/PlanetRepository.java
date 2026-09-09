package com.goomez.CosmosX.repository;

import com.goomez.CosmosX.model.Planet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanetRepository extends JpaRepository<Planet, Long> {
}