package com.campusbike.service;

import com.campusbike.model.Station;
import com.campusbike.repository.StationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class StationService {

    @Autowired
    private StationRepository stationRepository;

    public Station createStation(Station station) {
        if (stationRepository.findByName(station.getName()).isPresent()) {
            throw new RuntimeException("Station with this name already exists");
        }
        return stationRepository.save(station);
    }

    public Optional<Station> getStationById(Integer stationId) {
        return stationRepository.findById(stationId);
    }

    public List<Station> getAllStations() {
        return stationRepository.findAll();
    }

    public Station updateStation(Integer stationId, Station stationDetails) {
        Optional<Station> station = stationRepository.findById(stationId);
        if (station.isPresent()) {
            Station existingStation = station.get();
            if (stationDetails.getName() != null) {
                existingStation.setName(stationDetails.getName());
            }
            if (stationDetails.getLocation() != null) {
                existingStation.setLocation(stationDetails.getLocation());
            }
            if (stationDetails.getCapacity() != null) {
                existingStation.setCapacity(stationDetails.getCapacity());
            }
            return stationRepository.save(existingStation);
        }
        throw new RuntimeException("Station not found");
    }

    public void deleteStation(Integer stationId) {
        stationRepository.deleteById(stationId);
    }
}

