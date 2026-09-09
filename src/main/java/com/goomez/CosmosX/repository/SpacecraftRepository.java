package com.goomez.CosmosX.repository;

import com.goomez.CosmosX.model.Spacecraft;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpacecraftRepository extends JpaRepository<Spacecraft, Long> {
}