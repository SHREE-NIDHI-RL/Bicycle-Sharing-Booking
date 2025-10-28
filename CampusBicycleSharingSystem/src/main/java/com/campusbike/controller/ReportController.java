package com.campusbike.controller;

import com.campusbike.model.Booking;
import com.campusbike.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private BookingService bookingService;

    @GetMapping("/activebookings")
    public ResponseEntity<List<Booking>> getActiveBookings() {
        List<Booking> activeBookings = bookingService.getActiveBookings();
        return new ResponseEntity<>(activeBookings, HttpStatus.OK);
    }

    @GetMapping("/usage")
    public ResponseEntity<Map<String, Object>> getUsageReport() {
        List<Booking> allBookings = bookingService.getAllBookings();
        Map<String, Object> report = new HashMap<>();

        // Count completed bookings
        long completedCount = allBookings.stream()
                .filter(b -> "Completed".equals(b.getStatus()))
                .count();

        // Count active bookings
        long activeCount = allBookings.stream()
                .filter(b -> "Active".equals(b.getStatus()))
                .count();

        // Count cancelled bookings
        long cancelledCount = allBookings.stream()
                .filter(b -> "Cancelled".equals(b.getStatus()))
                .count();

        // Calculate total duration
        long totalDuration = allBookings.stream()
                .filter(b -> "Completed".equals(b.getStatus()) && b.getDurationMin() != null)
                .mapToLong(Booking::getDurationMin)
                .sum();

        report.put("totalBookings", allBookings.size());
        report.put("completedBookings", completedCount);
        report.put("activeBookings", activeCount);
        report.put("cancelledBookings", cancelledCount);
        report.put("totalDurationMinutes", totalDuration);
        report.put("averageDurationMinutes", completedCount > 0 ? totalDuration / completedCount : 0);

        return new ResponseEntity<>(report, HttpStatus.OK);
    }

    @GetMapping("/usage-per-station")
    public ResponseEntity<Map<String, Object>> getUsagePerStation() {
        List<Booking> allBookings = bookingService.getAllBookings();
        Map<String, Object> report = new HashMap<>();

        // Group bookings by station
        Map<String, Long> usageByStation = allBookings.stream()
                .filter(b -> b.getStation() != null)
                .collect(Collectors.groupingBy(
                        b -> b.getStation().getName(),
                        Collectors.counting()
                ));

        report.put("usageByStation", usageByStation);
        report.put("totalStations", usageByStation.size());

        return new ResponseEntity<>(report, HttpStatus.OK);
    }

    @GetMapping("/usage-per-day")
    public ResponseEntity<Map<String, Object>> getUsagePerDay() {
        List<Booking> allBookings = bookingService.getAllBookings();
        Map<String, Object> report = new HashMap<>();

        // Group bookings by day
        Map<LocalDate, Long> usageByDay = allBookings.stream()
                .filter(b -> b.getStartTime() != null)
                .collect(Collectors.groupingBy(
                        b -> b.getStartTime().toLocalDate(),
                        Collectors.counting()
                ));

        report.put("usageByDay", usageByDay);
        report.put("totalDays", usageByDay.size());

        return new ResponseEntity<>(report, HttpStatus.OK);
    }
}

