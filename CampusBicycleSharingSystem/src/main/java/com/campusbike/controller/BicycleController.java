package com.campusbike.controller;

import com.campusbike.model.Bicycle;
import com.campusbike.service.BicycleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/bicycles")
public class BicycleController {

    @Autowired
    private BicycleService bicycleService;

    @PostMapping
    public ResponseEntity<Bicycle> addBicycle(@RequestBody Bicycle bicycle) {
        try {
            Bicycle newBicycle = bicycleService.addBicycle(bicycle);
            return new ResponseEntity<>(newBicycle, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<List<Bicycle>> getAllBicycles() {
        List<Bicycle> bicycles = bicycleService.getAllBicycles();
        return new ResponseEntity<>(bicycles, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Bicycle> getBicycleById(@PathVariable Integer id) {
        Optional<Bicycle> bicycle = bicycleService.getBicycleById(id);
        return bicycle.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }

    @GetMapping("/available")
    public ResponseEntity<List<Bicycle>> getAvailableBicycles() {
        List<Bicycle> bicycles = bicycleService.getAvailableBicycles();
        return new ResponseEntity<>(bicycles, HttpStatus.OK);
    }

    @GetMapping("/station/{stationId}")
    public ResponseEntity<List<Bicycle>> getBicyclesByStation(@PathVariable Integer stationId) {
        List<Bicycle> bicycles = bicycleService.getBicyclesByStation(stationId);
        return new ResponseEntity<>(bicycles, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Bicycle> updateBicycle(@PathVariable Integer id, @RequestBody Bicycle bicycleDetails) {
        try {
            Bicycle updatedBicycle = bicycleService.updateBicycle(id, bicycleDetails);
            return new ResponseEntity<>(updatedBicycle, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Bicycle> updateBicycleStatus(@PathVariable Integer id, @RequestParam String status) {
        try {
            Bicycle updatedBicycle = bicycleService.updateBicycleStatus(id, status);
            return new ResponseEntity<>(updatedBicycle, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBicycle(@PathVariable Integer id) {
        bicycleService.deleteBicycle(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

