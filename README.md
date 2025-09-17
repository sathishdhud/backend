# Hotel Management System Backend API

A comprehensive Spring Boot backend application for hotel management operations including reservations, check-ins, advances, post transactions, and billing.

## Features

### Core Operations
- **Reservations Management**: Create, update, search reservations with multi-room support
- **Check-in Management**: Process walk-ins and reservation-based check-ins
- **Advance Payments**: Handle advances for reservations, in-house guests, and checkout guests
- **Post Transactions**: Manage guest expenses (room service, restaurant, laundry, etc.)
- **Billing System**: Generate bills, split bills, and manage checkout process

### Special Operations
- **Audit Date Change**: Automated posting of room charges and taxes for all in-house guests
- **Shift Management**: Track and update shift balances
- **Room Status Management**: Real-time room status tracking with color-coded display
- **User Rights Management**: Role-based access control for different user types

### Business Rules
- Automatic number generation with accounting year format (e.g., 1/25-26, 2/25-26 - sequential)
- Referential integrity validation for all foreign key relationships
- Room availability checking before check-in
- Automatic room status updates during check-in/checkout
- Support for both GST-inclusive and exclusive rates

## Technology Stack

- **Framework**: Spring Boot 3.2.0
- **Database**: MySQL with JPA/Hibernate
- **Security**: Spring Security with Basic Authentication
- **Documentation**: OpenAPI 3 with Swagger UI
- **Build Tool**: Maven
- **Java Version**: 17

## Quick Start

### Prerequisites
- Java 17 or higher
- MySQL 8.0 or higher
- Maven 3.6 or higher

### Setup Instructions

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd hotel-management
   ```

2. **Database Setup**
   ```sql
   CREATE DATABASE hotel_management;
   CREATE USER 'hotel_user'@'localhost' IDENTIFIED BY 'hotel_password';
   GRANT ALL PRIVILEGES ON hotel_management.* TO 'hotel_user'@'localhost';
   FLUSH PRIVILEGES;
   ```

3. **Configure Database Connection**
   
   Update `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/hotel_management?createDatabaseIfNotExist=true
   spring.datasource.username=hotel_user
   spring.datasource.password=hotel_password
   ```

4. **Build and Run**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

5. **Access the Application**
   - API Base URL: http://localhost:8080/api
   - Swagger UI: http://localhost:8080/swagger-ui.html
   - API Docs: http://localhost:8080/v3/api-docs

## Authentication

The API uses Basic Authentication with the following default users:

| Username | Password | Role | Permissions |
|----------|----------|------|-------------|
| admin | admin123 | ADMIN | Full access to all operations |
| manager | manager123 | MANAGER | Management operations |
| reception | reception123 | RECEPTION | Front desk operations |

## API Documentation

### Base URL
```
http://localhost:8080/api
```

### Main Endpoints

#### 1. Reservations (`/api/reservations`)
- `POST /` - Create new reservation
- `GET /{reservationNo}` - Get reservation details
- `PUT /{reservationNo}` - Update reservation
- `GET /search?searchTerm={term}` - Search reservations
- `GET /arrivals/{date}` - Expected arrivals
- `GET /departures/{date}` - Expected departures
- `GET /pending-checkins` - Pending check-ins
- `GET /check-availability` - Check room availability for dates
- `GET /available-rooms` - Get available rooms for room type and dates

#### 2. Check-ins (`/api/checkins`)
- `POST /` - Process check-in (walk-in or reservation)
- `GET /{folioNo}` - Get check-in details
- `PUT /{folioNo}` - Update check-in details
- `GET /room/{roomId}` - Get check-in by room
- `GET /search?searchTerm={term}` - Search check-ins
- `GET /inhouse` - Get in-house guests
- `GET /checkouts/{date}` - Expected checkouts

#### 3. Advances (`/api/advances`)
- `POST /reservation` - Create advance for reservation
- `POST /inhouse` - Create advance for in-house guest
- `POST /checkout` - Create advance for checkout guest
- `GET /{advanceId}` - Get advance by ID
- `PUT /{advanceId}` - Update advance
- `DELETE /{advanceId}` - Delete advance
- `GET /reservation/{reservationNo}` - Get advances by reservation
- `GET /folio/{folioNo}` - Get advances by folio
- `GET /bill/{billNo}` - Get advances by bill

#### 4. Post Transactions (`/api/transactions`)
- `POST /inhouse` - Create transaction for in-house guest
- `POST /checkout` - Create transaction for checkout guest
- `GET /{transactionId}` - Get transaction by ID
- `PUT /{transactionId}` - Update transaction
- `DELETE /{transactionId}` - Delete transaction
- `GET /folio/{folioNo}` - Get transactions by folio
- `GET /bill/{billNo}` - Get transactions by bill
- `GET /room/{roomId}` - Get transactions by room

#### 5. Bills (`/api/bills`)
- `POST /generate/{folioNo}` - Generate bill for checkout
- `GET /{billNo}` - Get bill details
- `GET /folio/{folioNo}` - Get bill by folio
- `GET /search?searchTerm={term}` - Search bills
- `POST /{billNo}/split` - Split bill

#### 6. Rooms (`/api/rooms`)
- `GET /` - Get all rooms with status
- `GET /status/{status}` - Get rooms by status
- `GET /available` - Get available rooms
- `GET /occupied` - Get occupied rooms
- `GET /floor/{floor}` - Get rooms by floor
- `GET /occupancy-stats` - Get occupancy statistics
- `PUT /{roomId}` - Update room details
- `PUT /{roomId}/status/{status}` - Update room status
- `POST /{roomId}/checkout` - Process room checkout
- `GET /{roomId}/availability` - Check room availability for dates
- `POST /status/automatic-update` - Trigger automatic room status update
- `POST /status/overdue-check` - Check for overdue checkouts

#### 7. Room Types (`/api/room-types`)
- `POST /` - Create room type
- `GET /` - Get all room types
- `GET /{typeId}` - Get room type by ID
- `PUT /{typeId}` - Update room type
- `DELETE /{typeId}` - Delete room type

#### 8. User Management (`/api/users`)
- `POST /login` - User login
- `POST /logout` - User logout
- `POST /` - Create user
- `GET /` - Get all users
- `GET /{userId}` - Get user by ID
- `PUT /{userId}` - Update user profile
- `PUT /{userId}/rights` - Update user rights
- `DELETE /{userId}` - Delete user

#### 9. Operations (`/api/operations`)
- `POST /audit-date-change` - Process audit date change
- `POST /shift-change` - Process shift change

### Room Status Codes
- **VR**: Vacant Ready (Green - Available for check-in)
- **OD**: Occupied Dirty (Red - Guest checked in)
- **OI**: Occupied Inspected (Yellow - Guest checked in, room cleaned)
- **Blocked**: Room blocked for maintenance (Purple)

### Sample Request/Response

**Create Reservation Request:**
```json
{
  "guestName": "John Doe",
  "companyId": "COMP001",
  "planId": "PLAN001",
  "roomTypeId": "DELUXE",
  "arrivalDate": "2024-01-15",
  "departureDate": "2024-01-20",
  "noOfDays": 5,
  "noOfPersons": 2,
  "noOfRooms": 1,
  "mobileNumber": "+1234567890",
  "emailId": "john.doe@email.com",
  "rate": 5000.00,
  "includingGst": "N",
  "remarks": "Corporate booking"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Reservation created successfully",
  "data": {
    "reservationNo": "1/24-25",
    "guestName": "John Doe",
    "companyName": "ABC Corporation",
    "planName": "Corporate Plan",
    "roomTypeName": "Deluxe Room",
    "arrivalDate": "2024-01-15",
    "departureDate": "2024-01-20",
    "noOfDays": 5,
    "noOfPersons": 2,
    "noOfRooms": 1,
    "mobileNumber": "+1234567890",
    "emailId": "john.doe@email.com",
    "rate": 5000.00,
    "includingGst": "N",
    "remarks": "Corporate booking",
    "roomsCheckedIn": 0,
    "createdAt": "2024-01-10T10:30:00",
    "updatedAt": "2024-01-10T10:30:00"
  },
  "timestamp": "2024-01-10T10:30:00"
}
```

## Database Schema

### Core Tables
- **rooms**: Room information and status
- **room_type**: Room type definitions
- **reservations**: Guest reservations
- **checkin**: Check-in records with folio numbers
- **advances**: Advance payments
- **post_transactions**: Guest expenses and charges
- **fobill**: Generated bills for checkout

### Lookup Tables
- **company**: Corporate clients
- **plan_type**: Rate plans
- **bill_settlement_types**: Payment modes
- **hotel_account_head**: Expense categories
- **taxation**: Tax configurations
- **nationality**: Guest nationalities
- **arrival_mode**: Transportation modes
- **resv_source**: Reservation sources
- **ref_mode**: Reference modes

### System Tables
- **shift**: Shift management
- **hotelsoftusers**: System users
- **user_type**: User role definitions
- **user_rights_data**: User permissions

## Business Workflows

### 1. Reservation to Check-in Flow
1. Create reservation with guest details
2. Guest arrives and presents reservation number
3. Process check-in with room assignment
4. Room status automatically updated to "OD"
5. Reservation check-in counter incremented

### 2. Advance Payment Flow
1. **For Reservations**: Create advance with reservation number
2. **For In-house**: Create advance with folio number
3. **For Checkout**: Create advance with bill number and manual date

### 3. Guest Expense Posting
1. **In-house**: Post transactions with folio number (uses audit date)
2. **Checkout**: Post transactions with bill number (manual date entry)

### 4. Checkout and Billing
1. Generate bill from folio number
2. Calculate total charges and advances
3. Present final bill with balance due
4. Process final payments if needed
5. Complete checkout process

### 5. Audit Date Change Process
1. User initiates audit date change with confirmation
2. System finds all in-house guests
3. Automatically posts room charges for each guest
4. Calculates and posts CGST (9%) and SGST (9%)
5. Updates audit date for the system

## Error Handling

The API uses standardized error responses:

```json
{
  "success": false,
  "message": "Error description",
  "data": null,
  "timestamp": "2024-01-10T10:30:00"
}
```

### Common HTTP Status Codes
- **200**: Success
- **201**: Created
- **400**: Bad Request (validation errors)
- **401**: Unauthorized
- **403**: Forbidden
- **404**: Not Found
- **409**: Conflict (duplicate resource)
- **500**: Internal Server Error

## Configuration

### Application Properties

Key configuration properties in `application.properties`:

```properties
# Server Configuration
server.port=8080

# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/hotel_management
spring.datasource.username=root
spring.datasource.password=password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

# Security Configuration
spring.security.user.name=admin
spring.security.user.password=admin123
spring.security.user.roles=ADMIN

# Application Properties
app.hotel.accounting-year-format=dd/yy-yy
app.hotel.default-currency=INR
app.hotel.timezone=Asia/Kolkata
```

## Development Guidelines

### Adding New Features
1. Create entity classes in `com.hotelworks.entity`
2. Add repository interfaces in `com.hotelworks.repository`
3. Implement service classes in `com.hotelworks.service`
4. Create DTOs in `com.hotelworks.dto`
5. Add controllers in `com.hotelworks.controller`
6. Update documentation

### Code Standards
- Follow Spring Boot best practices
- Use proper validation annotations
- Implement proper exception handling
- Write comprehensive API documentation
- Maintain transactional integrity

## Testing

### Running Tests
```bash
mvn test
```

### API Testing with cURL

**Create Reservation:**
```bash
curl -X POST http://localhost:8080/api/reservations \
  -H "Content-Type: application/json" \
  -H "Authorization: Basic YWRtaW46YWRtaW4xMjM=" \
  -d '{
    "guestName": "John Doe",
    "arrivalDate": "2024-01-15",
    "departureDate": "2024-01-20",
    "noOfDays": 5,
    "noOfPersons": 2,
    "noOfRooms": 1,
    "mobileNumber": "+1234567890",
    "rate": 5000.00,
    "includingGst": "N"
  }'
```

**Get Room Status:**
```bash
curl -X GET http://localhost:8080/api/rooms \
  -H "Authorization: Basic YWRtaW46YWRtaW4xMjM="
```

## Deployment

### Production Deployment
1. Update database configuration for production
2. Configure proper security credentials
3. Set up SSL/TLS certificates
4. Configure logging levels
5. Set up monitoring and health checks

### Docker Deployment
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/hotel-management-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## Support and Contributing

### Getting Help
- Check the API documentation at `/swagger-ui.html`
- Review the source code comments
- Check the logs for error details

### Contributing
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Contact

For support and inquiries:
- Email: support@hotelworks.com
- Website: https://hotelworks.com
- Documentation: http://localhost:8080/swagger-ui.html