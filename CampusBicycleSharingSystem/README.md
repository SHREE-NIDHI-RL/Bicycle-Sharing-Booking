# Campus Bicycle Sharing System

A complete full-stack Spring Boot web application for managing bicycle sharing on a college campus.

## 🎯 Features

- **User Management**: Register, login, and manage user profiles
- **Bicycle Management**: Add, view, and manage bicycles with different types (Standard/Electric)
- **Station Management**: Create and manage docking stations across campus
- **Booking System**: Book bicycles with availability checking and double-booking prevention
- **Usage Tracking**: Track ride history and generate usage reports
- **Role-Based Access**: Separate dashboards for Users and Admins
- **Responsive UI**: Clean, modern interface using Thymeleaf + HTML + CSS

## 🛠️ Technology Stack

- **Backend**: Spring Boot 3.1.5
- **Database**: MySQL 8.0
- **ORM**: Hibernate/JPA
- **Frontend**: Thymeleaf, HTML5, CSS3
- **Build Tool**: Maven
- **Java Version**: 17

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+
- Git

## 🚀 Setup Instructions

### 1. Clone the Repository
```bash
git clone <repository-url>
cd CampusBicycleSharingSystem
```

### 2. Create MySQL Database
```sql
CREATE DATABASE campus_bike;
USE campus_bike;
```

### 3. Configure Database Connection
Edit `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/campus_bike
spring.datasource.username=root
spring.datasource.password=your_password
```

### 4. Build the Project
```bash
mvn clean install
```

### 5. Run the Application
```bash
cd CampusBicycleSharingSystem
mvn spring-boot:run
```

The application will start at `http://localhost:8080`

## 📁 Project Structure

```
CampusBicycleSharingSystem/
├── src/main/java/com/campusbike/
│   ├── controller/
│   │   ├── WebController.java
│   │   ├── UserController.java
│   │   ├── BicycleController.java
│   │   ├── StationController.java
│   │   ├── BookingController.java
│   │   └── ReportController.java
│   ├── service/
│   │   ├── UserService.java
│   │   ├── BicycleService.java
│   │   ├── StationService.java
│   │   └── BookingService.java
│   ├── repository/
│   │   ├── UserRepository.java
│   │   ├── BicycleRepository.java
│   │   ├── StationRepository.java
│   │   └── BookingRepository.java
│   ├── model/
│   │   ├── User.java
│   │   ├── Bicycle.java
│   │   ├── Station.java
│   │   └── Booking.java
│   └── CampusBicycleSharingApplication.java
├── src/main/resources/
│   ├── templates/
│   │   ├── index.html
│   │   ├── register.html
│   │   ├── login.html
│   │   ├── user-dashboard.html
│   │   ├── admin-dashboard.html
│   │   ├── booking.html
│   │   └── reports.html
│   └── application.properties
└── pom.xml
```

## 🔐 User Roles

### User Role
- View available bicycles
- Book bicycles from stations
- View booking history
- Track ride duration

### Admin Role
- Manage bicycles (add, update, delete)
- Manage stations
- View all bookings
- Generate usage reports
- Manage user accounts

## 🌐 API Endpoints

### Users
- `POST /api/users` - Register new user
- `GET /api/users` - Get all users
- `GET /api/users/{id}` - Get user by ID
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user

### Bicycles
- `POST /api/bicycles` - Add new bicycle
- `GET /api/bicycles` - Get all bicycles
- `GET /api/bicycles/{id}` - Get bicycle by ID
- `GET /api/bicycles/available` - Get available bicycles
- `PUT /api/bicycles/{id}` - Update bicycle
- `DELETE /api/bicycles/{id}` - Delete bicycle

### Stations
- `POST /api/stations` - Create station
- `GET /api/stations` - Get all stations
- `GET /api/stations/{id}` - Get station by ID
- `PUT /api/stations/{id}` - Update station
- `DELETE /api/stations/{id}` - Delete station

### Bookings
- `POST /api/bookings` - Create booking
- `GET /api/bookings` - Get all bookings
- `GET /api/bookings/{id}` - Get booking by ID
- `GET /api/bookings/user/{userId}` - Get user bookings
- `GET /api/bookings/bike/{bikeId}` - Get bike bookings
- `GET /api/bookings/active` - Get active bookings
- `PUT /api/bookings/{id}/complete` - Complete booking
- `PUT /api/bookings/{id}/cancel` - Cancel booking

### Reports
- `GET /api/reports/usage` - Get usage statistics
- `GET /api/reports/activebookings` - Get active bookings

## 🎨 Frontend Pages

1. **Home Page** (`/`) - Welcome page with station overview
2. **Register** (`/register`) - User registration form
3. **Login** (`/login`) - User login page
4. **User Dashboard** (`/user-dashboard`) - View bikes and booking history
5. **Admin Dashboard** (`/admin-dashboard`) - Manage system resources
6. **Booking** (`/booking`) - Book a bicycle
7. **Reports** (`/reports`) - View usage statistics

## 🔄 Database Schema

### Users Table
- user_id (PK, Auto Increment)
- name (VARCHAR 50)
- email (VARCHAR 100, Unique)
- contact (VARCHAR 20)
- role (VARCHAR 20)
- join_date (TIMESTAMP)

### Bicycles Table
- bike_id (PK, Auto Increment)
- bike_code (VARCHAR 20, Unique)
- type (VARCHAR 20)
- status (VARCHAR 20)
- current_station (FK)
- added_at (TIMESTAMP)

### Stations Table
- station_id (PK, Auto Increment)
- name (VARCHAR 50)
- location (VARCHAR 100)
- capacity (INT)

### Bookings Table
- booking_id (PK, Auto Increment)
- user_id (FK)
- bike_id (FK)
- station_id (FK)
- start_time (TIMESTAMP)
- end_time (TIMESTAMP)
- status (VARCHAR 20)
- duration_min (INT)

## ✨ Key Features

- **Double-Booking Prevention**: System checks if a bike is already booked
- **Auto Status Update**: Bicycle status updates automatically when booked/returned
- **Duration Calculation**: Automatically calculates ride duration
- **Session Management**: User sessions for authentication
- **Responsive Design**: Works on desktop and mobile devices
- **Error Handling**: Comprehensive error messages and validation

## 🧪 Testing the Application

### Sample Test Data

1. **Create a Station**
   - POST to `/api/stations`
   - Body: `{"name": "Main Gate", "location": "Campus Main Entrance", "capacity": 20}`

2. **Add a Bicycle**
   - POST to `/api/bicycles`
   - Body: `{"bikeCode": "BIKE001", "type": "Standard", "status": "Available", "currentStation": {"stationId": 1}}`

3. **Register a User**
   - POST to `/api/users`
   - Body: `{"name": "John Doe", "email": "john@example.com", "contact": "9876543210", "role": "User"}`

4. **Create a Booking**
   - POST to `/api/bookings`
   - Body: `{"user": {"userId": 1}, "bicycle": {"bikeId": 1}, "station": {"stationId": 1}}`

## 📝 Notes

- Default database password is set to "root" in application.properties
- Change this to a secure password in production
- The application uses Hibernate's `ddl-auto=update` to auto-create tables
- All timestamps are stored in UTC

## 🐛 Troubleshooting

### Database Connection Error
- Ensure MySQL is running
- Check database credentials in application.properties
- Verify database exists: `CREATE DATABASE campus_bike;`

### Port Already in Use
- Change port in application.properties: `server.port=8081`

### Thymeleaf Template Not Found
- Ensure templates are in `src/main/resources/templates/`
- Check template file names match controller mappings

## 📄 License

This project is open source and available under the MIT License.

## 👨‍💻 Author

Campus Bicycle Sharing System - A Spring Boot Learning Project

---

