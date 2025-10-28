package com.campusbike.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "bicycles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bicycle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bike_id")
    private Integer bikeId;

    @Column(name = "bike_code", length = 20, unique = true, nullable = false)
    private String bikeCode;

    @Column(name = "type", length = 20, nullable = false)
    private String type; // "Standard" or "Electric"

    @Column(name = "status", length = 20, nullable = false)
    private String status; // "Available", "Booked", "Maintenance"

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "current_station", referencedColumnName = "station_id")
    private Station currentStation;

    @Column(name = "added_at", nullable = false)
    private LocalDateTime addedAt;

    @OneToMany(mappedBy = "bicycle", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Booking> bookings;

    @PrePersist
    protected void onCreate() {
        addedAt = LocalDateTime.now();
        if (status == null) {
            status = "Available";
        }
    }

    // Getters and Setters
    public Integer getBikeId() {
        return bikeId;
    }

    public void setBikeId(Integer bikeId) {
        this.bikeId = bikeId;
    }

    public String getBikeCode() {
        return bikeCode;
    }

    public void setBikeCode(String bikeCode) {
        this.bikeCode = bikeCode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Station getCurrentStation() {
        return currentStation;
    }

    public void setCurrentStation(Station currentStation) {
        this.currentStation = currentStation;
    }

    public LocalDateTime getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(LocalDateTime addedAt) {
        this.addedAt = addedAt;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }
}

