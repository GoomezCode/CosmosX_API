package com.goomez.CosmosX.repository;

import com.goomez.CosmosX.model.Mission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MissionRepository extends JpaRepository<Mission, Long> {
}