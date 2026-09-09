package com.goomez.CosmosX.repository;

import com.goomez.CosmosX.model.Astronaut;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AstronautRepository extends JpaRepository<Astronaut, Long> {
}