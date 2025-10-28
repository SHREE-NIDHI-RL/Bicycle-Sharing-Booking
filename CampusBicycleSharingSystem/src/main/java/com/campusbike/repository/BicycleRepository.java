package com.campusbike.repository;

import com.campusbike.model.Bicycle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BicycleRepository extends JpaRepository<Bicycle, Integer> {
    Optional<Bicycle> findByBikeCode(String bikeCode);
    List<Bicycle> findByStatus(String status);
    List<Bicycle> findByCurrentStationStationId(Integer stationId);
}

