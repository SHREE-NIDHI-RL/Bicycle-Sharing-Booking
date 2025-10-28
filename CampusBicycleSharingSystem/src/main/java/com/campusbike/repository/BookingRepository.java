package com.campusbike.repository;

import com.campusbike.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findByUserUserId(Integer userId);
    List<Booking> findByBicycleBikeId(Integer bikeId);
    List<Booking> findByStatus(String status);
    List<Booking> findByStationStationId(Integer stationId);
}

