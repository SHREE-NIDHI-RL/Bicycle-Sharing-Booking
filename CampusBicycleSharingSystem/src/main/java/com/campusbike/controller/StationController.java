package com.campusbike.controller;

import com.campusbike.model.Station;
import com.campusbike.service.StationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/stations")
public class StationController {

    @Autowired
    private StationService stationService;

    @PostMapping
    public ResponseEntity<Station> createStation(@RequestBody Station station) {
        try {
            Station newStation = stationService.createStation(station);
            return new ResponseEntity<>(newStation, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<List<Station>> getAllStations() {
        List<Station> stations = stationService.getAllStations();
        return new ResponseEntity<>(stations, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Station> getStationById(@PathVariable Integer id) {
        Optional<Station> station = stationService.getStationById(id);
        return station.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Station> updateStation(@PathVariable Integer id, @RequestBody Station stationDetails) {
        try {
            Station updatedStation = stationService.updateStation(id, stationDetails);
            return new ResponseEntity<>(updatedStation, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStation(@PathVariable Integer id) {
        stationService.deleteStation(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

