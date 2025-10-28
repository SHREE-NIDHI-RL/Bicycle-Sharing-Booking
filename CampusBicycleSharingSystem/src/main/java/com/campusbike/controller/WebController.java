package com.campusbike.controller;

import com.campusbike.model.User;
import com.campusbike.model.Bicycle;
import com.campusbike.model.Station;
import com.campusbike.model.Booking;
import com.campusbike.service.UserService;
import com.campusbike.service.BicycleService;
import com.campusbike.service.StationService;
import com.campusbike.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

@Controller
public class WebController {

    @Autowired
    private UserService userService;

    @Autowired
    private BicycleService bicycleService;

    @Autowired
    private StationService stationService;

    @Autowired
    private BookingService bookingService;

    @GetMapping("/")
    public String home(Model model) {
        List<Station> stations = stationService.getAllStations();
        List<Bicycle> availableBikes = bicycleService.getAvailableBicycles();
        model.addAttribute("stations", stations);
        model.addAttribute("availableBikes", availableBikes);
        return "index";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, Model model) {
        try {
            userService.registerUser(user);
            model.addAttribute("message", "Registration successful! Please login.");
            return "redirect:/login";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password, 
                       HttpSession session, Model model) {
        // Direct admin login bypass
        if ("admin1@campus.edu".equals(email) && "admin123".equals(password)) {
            session.setAttribute("userId", 999);
            session.setAttribute("userName", "Admin One");
            session.setAttribute("userRole", "Admin");
            return "redirect:/admin-dashboard";
        }
        if ("admin2@campus.edu".equals(email) && "admin456".equals(password)) {
            session.setAttribute("userId", 998);
            session.setAttribute("userName", "Admin Two");
            session.setAttribute("userRole", "Admin");
            return "redirect:/admin-dashboard";
        }
        
        try {
            Optional<User> user = userService.authenticateUser(email, password);
            if (user.isPresent()) {
                session.setAttribute("userId", user.get().getUserId());
                session.setAttribute("userName", user.get().getName());
                session.setAttribute("userRole", user.get().getRole());
                
                if ("Admin".equals(user.get().getRole())) {
                    return "redirect:/admin-dashboard";
                } else {
                    return "redirect:/user-dashboard";
                }
            }
        } catch (Exception e) {
            model.addAttribute("error", "Login failed: " + e.getMessage());
            return "login";
        }
        model.addAttribute("error", "Invalid email or password");
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/user-dashboard")
    public String userDashboard(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        List<Bicycle> availableBikes = bicycleService.getAvailableBicycles();
        List<Station> stations = stationService.getAllStations();
        List<Booking> userBookings = bookingService.getUserBookings(userId);

        model.addAttribute("availableBikes", availableBikes);
        model.addAttribute("stations", stations);
        model.addAttribute("userBookings", userBookings);
        model.addAttribute("userName", session.getAttribute("userName"));

        return "user-dashboard";
    }

    @GetMapping("/admin-dashboard")
    public String adminDashboard(HttpSession session, Model model) {
        String userRole = (String) session.getAttribute("userRole");
        if (!"Admin".equals(userRole)) {
            return "redirect:/login";
        }

        List<Bicycle> bicycles = bicycleService.getAllBicycles();
        List<Station> stations = stationService.getAllStations();
        List<Booking> activeBookings = bookingService.getActiveBookings();
        List<User> users = userService.getAllUsers();
        // Filter out specific old admin users
        users = users.stream()
                .filter(user -> !user.getName().toLowerCase().contains("shree") && 
                               !user.getName().toLowerCase().contains("nidhi") && 
                               !user.getName().toLowerCase().contains("rl") &&
                               !"Admin".equals(user.getRole()))
                .collect(java.util.stream.Collectors.toList());

        model.addAttribute("bicycles", bicycles);
        model.addAttribute("stations", stations);
        model.addAttribute("activeBookings", activeBookings);
        model.addAttribute("users", users);
        model.addAttribute("userName", session.getAttribute("userName"));

        return "admin-dashboard";
    }

    @GetMapping("/booking")
    public String bookingPage(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        List<Bicycle> availableBikes = bicycleService.getAvailableBicycles();
        List<Station> stations = stationService.getAllStations();

        model.addAttribute("availableBikes", availableBikes);
        model.addAttribute("stations", stations);
        model.addAttribute("booking", new Booking());

        return "booking";
    }

    @GetMapping("/reports")
    public String reportsPage(HttpSession session, Model model) {
        String userRole = (String) session.getAttribute("userRole");
        if (!"Admin".equals(userRole)) {
            return "redirect:/login";
        }

        List<Booking> allBookings = bookingService.getAllBookings();
        long completedCount = allBookings.stream().filter(b -> "Completed".equals(b.getStatus())).count();
        long activeCount = allBookings.stream().filter(b -> "Active".equals(b.getStatus())).count();

        model.addAttribute("allBookings", allBookings);
        model.addAttribute("completedCount", completedCount);
        model.addAttribute("activeCount", activeCount);
        model.addAttribute("totalBookings", allBookings.size());

        return "reports";
    }

    // Handle Station Form Submission from Admin Dashboard
    @PostMapping("/add-station")
    public String addStation(@ModelAttribute Station station, HttpSession session, Model model) {
        String userRole = (String) session.getAttribute("userRole");
        if (!"Admin".equals(userRole)) {
            return "redirect:/login";
        }

        try {
            stationService.createStation(station);
            return "redirect:/admin-dashboard";
        } catch (Exception e) {
            List<Bicycle> bicycles = bicycleService.getAllBicycles();
            List<Station> stations = stationService.getAllStations();
            List<Booking> activeBookings = bookingService.getActiveBookings();
            List<User> users = userService.getAllUsers();

            model.addAttribute("bicycles", bicycles);
            model.addAttribute("stations", stations);
            model.addAttribute("activeBookings", activeBookings);
            model.addAttribute("users", users);
            model.addAttribute("userName", session.getAttribute("userName"));
            model.addAttribute("error", "Error adding station: " + e.getMessage());

            return "admin-dashboard";
        }
    }

    // Handle Bicycle Form Submission from Admin Dashboard
    @PostMapping("/add-bicycle")
    public String addBicycle(@ModelAttribute Bicycle bicycle, HttpSession session, Model model) {
        String userRole = (String) session.getAttribute("userRole");
        if (!"Admin".equals(userRole)) {
            return "redirect:/login";
        }

        try {
            bicycleService.addBicycle(bicycle);
            return "redirect:/admin-dashboard";
        } catch (Exception e) {
            List<Bicycle> bicycles = bicycleService.getAllBicycles();
            List<Station> stations = stationService.getAllStations();
            List<Booking> activeBookings = bookingService.getActiveBookings();
            List<User> users = userService.getAllUsers();

            model.addAttribute("bicycles", bicycles);
            model.addAttribute("stations", stations);
            model.addAttribute("activeBookings", activeBookings);
            model.addAttribute("users", users);
            model.addAttribute("userName", session.getAttribute("userName"));
            model.addAttribute("error", "Error adding bicycle: " + e.getMessage());

            return "admin-dashboard";
        }
    }

    // Handle Booking Form Submission
    @PostMapping("/create-booking")
    public String createBooking(@RequestParam Integer bikeId, 
                               @RequestParam Integer stationId,
                               HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        try {
            // Get the user, bicycle, and station objects
            Optional<User> user = userService.getUserById(userId);
            Optional<Bicycle> bicycle = bicycleService.getBicycleById(bikeId);
            Optional<Station> station = stationService.getStationById(stationId);

            if (user.isEmpty() || bicycle.isEmpty() || station.isEmpty()) {
                model.addAttribute("error", "Invalid selection. Please try again.");
                return "redirect:/booking?error=invalid";
            }

            // Create booking object
            Booking booking = new Booking();
            booking.setUser(user.get());
            booking.setBicycle(bicycle.get());
            booking.setStation(station.get());

            // Create the booking
            bookingService.createBooking(booking);
            
            return "redirect:/user-dashboard?success=booking";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/booking?error=" + e.getMessage();
        }
    }

    // Debug endpoint to check users (remove in production)
    @GetMapping("/debug-users")
    @ResponseBody
    public String debugUsers() {
        List<User> users = userService.getAllUsers();
        StringBuilder sb = new StringBuilder();
        sb.append("<h2>Debug: Users in Database</h2>");
        for (User user : users) {
            sb.append("<p>Email: ").append(user.getEmail())
              .append(", Name: ").append(user.getName())
              .append(", Role: ").append(user.getRole())
              .append(", Password: ").append(user.getPassword() != null ? "SET" : "NULL")
              .append("</p>");
        }
        return sb.toString();
    }

    // Endpoint to set default passwords for existing users
    @GetMapping("/fix-passwords")
    @ResponseBody
    public String fixPasswords() {
        List<User> users = userService.getAllUsers();
        int fixed = 0;
        for (User user : users) {
            if (user.getPassword() == null || user.getPassword().isEmpty()) {
                userService.setDefaultPasswordForUser(user.getEmail(), "password123");
                fixed++;
            }
        }
        return "<h2>Password Fix Complete</h2><p>Fixed " + fixed + " users with default password 'password123'</p><p><a href='/debug-users'>Check Users</a></p>";
    }

    // Mark bicycle as available
    @PostMapping("/mark-bike-available")
    public String markBikeAvailable(@RequestParam Integer bikeId, HttpSession session) {
        String userRole = (String) session.getAttribute("userRole");
        if (!"Admin".equals(userRole)) {
            return "redirect:/login";
        }

        try {
            bookingService.markBicycleAvailable(bikeId);
        } catch (Exception e) {
            // Handle error silently and continue
        }
        return "redirect:/admin-dashboard";
    }

    // Test admin creation
    @GetMapping("/test-admin")
    @ResponseBody
    public String testAdmin() {
        try {
            Optional<User> admin1 = userService.authenticateUser("admin1@campus.edu", "admin123");
            Optional<User> admin2 = userService.authenticateUser("admin2@campus.edu", "admin456");
            
            return "<h2>Admin Test Results</h2>" +
                   "<p>Admin1 login: " + (admin1.isPresent() ? "SUCCESS - " + admin1.get().getName() : "FAILED") + "</p>" +
                   "<p>Admin2 login: " + (admin2.isPresent() ? "SUCCESS - " + admin2.get().getName() : "FAILED") + "</p>" +
                   "<p><a href='/login'>Go to Login</a></p>";
        } catch (Exception e) {
            return "<h2>Error:</h2><p>" + e.getMessage() + "</p>";
        }
    }
}

