# ShareIt

ShareIt is a sharing service application that allows users to share items with others and borrow items temporarily. The service implements a simple "sharing economy" concept, enabling users to lend and borrow items without the need for purchase or hiring professionals.

---

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Entities](#entities)
- [Project Structure](#project-structure)
- [DTOs and Mappers](#dtos-and-mappers)
- [Controllers](#controllers)
- [Services](#services)
- [Repositories](#repositories)
- [Validation](#validation)
- [Testing](#testing)
- [Usage](#usage)
- [License](#license)

---

## Overview

Imagine you bought some paintings but need a drill to hang them. Instead of buying or hiring a professional, you could borrow the drill from a friend. ShareIt provides a platform for sharing items like tools, gadgets, or rare equipment with other users. Users can:

- Share their items and make them available for rent.
- Search for items available for borrowing.
- Make booking requests for items on specific dates.
- Leave comments after using an item.
- Create item requests if a needed item is not available.

---

## Features

- **User Management**: Create, read, update, delete users.
- **Item Management**: Add items, edit item details, check availability, view items.
- **Booking System**: Book items for specific dates, owner approval/rejection.
- **Item Requests**: Submit requests for items that are not available in the system.
- **Comments**: Leave feedback for borrowed items.
- **Search**: Search for available items by text in name or description.

---

## Entities

The main entities in the system:

1. **User**
   - Represents a user of the system.
   - Can own items, make bookings, leave comments, and create item requests.

2. **Item**
   - Represents an item that can be shared.
   - Contains `name`, `description`, `availability`, and owner information.

3. **Booking**
   - Represents a booking of an item for a specific period.
   - Contains `start` and `end` dates, booking `status`, and booker information.

4. **ItemRequest**
   - Represents a request for an item that is not currently available.
   - Contains description of the requested item and requester information.

5. **Comment**
   - Feedback left by a user after borrowing an item.

---

## Project Structure

The project follows a **feature-based package layout**:

src/main/java/ru/practicum/shareit/
├── booking/
│ ├── controller/
│ ├── dto/
│ ├── service/
│ ├── mapper/
│ ├── model/
│ └── repository/
├── item/
│ ├── controller/
│ ├── dto/
│ ├── service/
│ ├── mapper/
│ ├── model/
│ └── repository/
├── request/
│ ├── controller/
│ ├── dto/
│ ├── service/
│ ├── mapper/
│ ├── model/
│ └── repository/
├── user/
│ ├── controller/
│ ├── dto/
│ ├── service/
│ ├── mapper/
│ ├── model/
│ └── repository/
└── common/
└── HeaderConstants.java

markdown
Copy code

- Each feature (user, item, booking, request) has its **controller**, **service**, **repository**, **dto**, and **mapper** classes.
- `HeaderConstants` holds common HTTP header names like `X-Sharer-User-Id`.

---

## DTOs and Mappers

- **DTOs (Data Transfer Objects)** are used for REST API communication.
- **Mappers** convert between model entities and DTOs.
- Example:
  - `ItemMapper` converts `Item` ↔ `ItemDto`.
  - `BookingMapper` converts `Booking` ↔ `BookingDto`.

---

## Controllers

All controllers use Spring's `@RestController` and handle requests via REST endpoints:

### UserController
- `POST /users` – create a user
- `GET /users/{id}` – get user by ID
- `GET /users` – list all users
- `PATCH /users/{id}` – update user
- `DELETE /users/{id}` – delete user

### ItemController
- `POST /items` – add new item
- `PATCH /items/{itemId}` – edit item
- `GET /items/{itemId}` – get item details
- `GET /items` – list all items of the owner
- `GET /items/search?text=` – search for items
- `POST /items/{itemId}/comment` – add comment

### BookingController
- `POST /bookings` – create booking
- `PATCH /bookings/{bookingId}?approved=` – approve/reject booking
- `GET /bookings/{bookingId}` – get booking by ID

### ItemRequestController
- `POST /requests` – create item request
- `GET /requests` – list requests by requester
- `GET /requests/all` – list all requests with pagination
- `GET /requests/{requestId}` – get request by ID

---

## Services

- Implement business logic for each feature.
- Handle booking status, item availability, ownership checks.
- Perform validations primarily through DTO annotations (avoid duplicate checks in services).

---

## Repositories

- Currently in-memory storage is used:
  - `InMemoryUserRepository`
  - `InMemoryItemRepository`
  - `InMemoryBookingRepository`
  - `InMemoryItemRequestRepository`
  - `InMemoryCommentRepository`

---

## Validation

- Bean Validation (`jakarta.validation`) is used in DTOs:
  - `@NotNull`, `@NotBlank`, `@Positive`, etc.
- Removed unnecessary custom validators querying repositories.
- Validations in controllers replace duplicate service checks.

---

## Testing

- API can be tested via the provided **Postman collection**.
- Supports CRUD operations for users, items, bookings, requests, and comments.

---

## Usage

1. Clone the repository:

```bash
git clone https://github.com/acoent/java-shareit.git
cd java-shareit
Build and run the project:

bash
Copy code
./mvnw clean install
./mvnw spring-boot:run
Use the API endpoints described in Controllers to interact with the service.
```
## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
