package com.campusbike.controller;

import com.campusbike.model.Booking;
import com.campusbike.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping
    public ResponseEntity<Booking> createBooking(@RequestBody Booking booking) {
        try {
            Booking newBooking = bookingService.createBooking(booking);
            return new ResponseEntity<>(newBooking, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<List<Booking>> getAllBookings() {
        List<Booking> bookings = bookingService.getAllBookings();
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBookingById(@PathVariable Integer id) {
        Optional<Booking> booking = bookingService.getBookingById(id);
        return booking.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Booking>> getUserBookings(@PathVariable Integer userId) {
        List<Booking> bookings = bookingService.getUserBookings(userId);
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }

    @GetMapping("/bike/{bikeId}")
    public ResponseEntity<List<Booking>> getBicycleBookings(@PathVariable Integer bikeId) {
        List<Booking> bookings = bookingService.getBicycleBookings(bikeId);
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }

    @GetMapping("/active")
    public ResponseEntity<List<Booking>> getActiveBookings() {
        List<Booking> bookings = bookingService.getActiveBookings();
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }

    @GetMapping("/station/{stationId}")
    public ResponseEntity<List<Booking>> getStationBookings(@PathVariable Integer stationId) {
        List<Booking> bookings = bookingService.getStationBookings(stationId);
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<Booking> completeBooking(@PathVariable Integer id) {
        try {
            Booking completedBooking = bookingService.completeBooking(id);
            return new ResponseEntity<>(completedBooking, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Booking> cancelBooking(@PathVariable Integer id) {
        try {
            Booking cancelledBooking = bookingService.cancelBooking(id);
            return new ResponseEntity<>(cancelledBooking, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
}

