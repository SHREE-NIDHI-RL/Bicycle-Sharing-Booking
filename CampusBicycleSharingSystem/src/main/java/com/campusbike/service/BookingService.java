package com.campusbike.service;

import com.campusbike.model.Bicycle;
import com.campusbike.model.Booking;
import com.campusbike.repository.BookingRepository;
import com.campusbike.repository.BicycleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BicycleRepository bicycleRepository;

    public Booking createBooking(Booking booking) {
        // Check if user already has an active booking
        if (hasActiveBooking(booking.getUser().getUserId())) {
            throw new RuntimeException("You already have an active booking. Please complete it first.");
        }
        
        // Check if bike exists and is available
        Optional<Bicycle> bicycle = bicycleRepository.findById(booking.getBicycle().getBikeId());
        if (bicycle.isEmpty()) {
            throw new RuntimeException("Bicycle not found");
        }
        
        Bicycle bike = bicycle.get();
        if (!"Available".equals(bike.getStatus())) {
            throw new RuntimeException("Bicycle is not available for booking");
        }

        // Check for active bookings on this specific bike
        List<Booking> activeBookings = bookingRepository.findByStatus("Active");
        for (Booking b : activeBookings) {
            if (b.getBicycle().getBikeId().equals(booking.getBicycle().getBikeId())) {
                throw new RuntimeException("Bicycle is already booked");
            }
        }

        // Update bicycle status to Booked
        bike.setStatus("Booked");
        bicycleRepository.save(bike);

        // Set booking status and save
        booking.setStatus("Active");
        return bookingRepository.save(booking);
    }

    public Optional<Booking> getBookingById(Integer bookingId) {
        return bookingRepository.findById(bookingId);
    }

    public List<Booking> getUserBookings(Integer userId) {
        return bookingRepository.findByUserUserId(userId);
    }

    public List<Booking> getBicycleBookings(Integer bikeId) {
        return bookingRepository.findByBicycleBikeId(bikeId);
    }

    public List<Booking> getActiveBookings() {
        return bookingRepository.findByStatus("Active");
    }

    public List<Booking> getStationBookings(Integer stationId) {
        return bookingRepository.findByStationStationId(stationId);
    }

    public Booking completeBooking(Integer bookingId) {
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (booking.isPresent()) {
            Booking existingBooking = booking.get();
            existingBooking.completeBooking();

            // Update bicycle status back to available
            Bicycle bike = existingBooking.getBicycle();
            bike.setStatus("Available");
            bicycleRepository.save(bike);

            return bookingRepository.save(existingBooking);
        }
        throw new RuntimeException("Booking not found");
    }

    public Booking cancelBooking(Integer bookingId) {
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (booking.isPresent()) {
            Booking existingBooking = booking.get();
            existingBooking.setStatus("Cancelled");

            // Update bicycle status back to available
            Bicycle bike = existingBooking.getBicycle();
            bike.setStatus("Available");
            bicycleRepository.save(bike);

            return bookingRepository.save(existingBooking);
        }
        throw new RuntimeException("Booking not found");
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }
    
    // Method to mark bicycle as available and complete any active bookings
    public void markBicycleAvailable(Integer bikeId) {
        // Find any active booking for this bike and complete it
        List<Booking> activeBookings = bookingRepository.findByStatus("Active");
        for (Booking booking : activeBookings) {
            if (booking.getBicycle().getBikeId().equals(bikeId)) {
                booking.completeBooking();
                bookingRepository.save(booking);
                break;
            }
        }
        
        // Update bicycle status to Available
        Optional<Bicycle> bicycle = bicycleRepository.findById(bikeId);
        if (bicycle.isPresent()) {
            Bicycle bike = bicycle.get();
            bike.setStatus("Available");
            bicycleRepository.save(bike);
        }
    }
    
    // Check if user has any active bookings
    public boolean hasActiveBooking(Integer userId) {
        List<Booking> userBookings = bookingRepository.findByUserUserId(userId);
        return userBookings.stream().anyMatch(booking -> "Active".equals(booking.getStatus()));
    }
}

