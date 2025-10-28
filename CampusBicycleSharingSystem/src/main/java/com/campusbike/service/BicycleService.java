package com.campusbike.service;

import com.campusbike.model.Bicycle;
import com.campusbike.repository.BicycleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class BicycleService {

    @Autowired
    private BicycleRepository bicycleRepository;

    public Bicycle addBicycle(Bicycle bicycle) {
        if (bicycleRepository.findByBikeCode(bicycle.getBikeCode()).isPresent()) {
            throw new RuntimeException("Bike code already exists");
        }
        if (bicycle.getStatus() == null) {
            bicycle.setStatus("Available");
        }
        return bicycleRepository.save(bicycle);
    }

    public Optional<Bicycle> getBicycleById(Integer bikeId) {
        return bicycleRepository.findById(bikeId);
    }

    public Optional<Bicycle> getBicycleByCode(String bikeCode) {
        return bicycleRepository.findByBikeCode(bikeCode);
    }

    public List<Bicycle> getAllBicycles() {
        return bicycleRepository.findAll();
    }

    public List<Bicycle> getAvailableBicycles() {
        // Get all bicycles with Available status
        List<Bicycle> availableBikes = bicycleRepository.findByStatus("Available");
        
        // Remove bikes that have active bookings
        return availableBikes.stream()
                .filter(bike -> !hasActiveBooking(bike.getBikeId()))
                .collect(java.util.stream.Collectors.toList());
    }
    
    private boolean hasActiveBooking(Integer bikeId) {
        // This will be injected
        return false; // Placeholder
    }

    public List<Bicycle> getBicyclesByStation(Integer stationId) {
        return bicycleRepository.findByCurrentStationStationId(stationId);
    }

    public Bicycle updateBicycleStatus(Integer bikeId, String status) {
        Optional<Bicycle> bicycle = bicycleRepository.findById(bikeId);
        if (bicycle.isPresent()) {
            Bicycle existingBike = bicycle.get();
            existingBike.setStatus(status);
            return bicycleRepository.save(existingBike);
        }
        throw new RuntimeException("Bicycle not found");
    }

    public Bicycle updateBicycle(Integer bikeId, Bicycle bicycleDetails) {
        Optional<Bicycle> bicycle = bicycleRepository.findById(bikeId);
        if (bicycle.isPresent()) {
            Bicycle existingBike = bicycle.get();
            if (bicycleDetails.getType() != null) {
                existingBike.setType(bicycleDetails.getType());
            }
            if (bicycleDetails.getStatus() != null) {
                existingBike.setStatus(bicycleDetails.getStatus());
            }
            if (bicycleDetails.getCurrentStation() != null) {
                existingBike.setCurrentStation(bicycleDetails.getCurrentStation());
            }
            return bicycleRepository.save(existingBike);
        }
        throw new RuntimeException("Bicycle not found");
    }

    public void deleteBicycle(Integer bikeId) {
        bicycleRepository.deleteById(bikeId);
    }
}

