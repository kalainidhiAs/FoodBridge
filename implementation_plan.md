# FoodBridge Implementation Plan

This plan details the steps to generate and implement the "FoodBridge" project based on your requirements. It will use a Spring Boot backend with an H2 database and an Angular frontend.

## Proposed Changes

### Phase 1: Project Setup

1. **Spring Boot Backend Generation**
   Generate a new Spring Boot application (`foodbridge-backend`) with the following dependencies:
   - Spring Web
   - Spring Data JPA
   - H2 Database
   - Spring Security (for JWT authentication)
   - Spring Boot Validation
   - Lombok
2. **Angular Frontend Setup**
   Generate a new Angular project (`foodbridge-frontend`) in the same workspace.

### Phase 2: Domain Modeling & Database Schema (H2)

Create the JPA Entities and Relationships:
- `User` (id, name, mobileNumber, password, role, address, status)
- `NgoProfile` (linked to `User`, registeredAddress, contactPersonName, contactMobileNumber, ngoPanNumber, ngoDarpanUniqueId, registrationCertificateUrl)
- `HomemakerProfile` (linked to `User`, cuisinesOffered, experience, specialization, rating, idProofUrl, kitchenImagesUrls)
- `FoodListing` (id, homemaker_id, foodName, description, quantity, price, isVeg, isFree, startTime, endTime, imagesUrls, isActive)
- `Order` (id, customer_id, string status, totalAmount, orderTime)
- `OrderItem` (linked to `Order` and `FoodListing`, quantity)
- `Donation` (id, ngo_id, foodListing_id, collectionTime, status)
- `Earning` (homemaker platform contribution details)

### Phase 3: APIs and Authentication (Backend)

- Implement Global Exception Handling.
- Implement JWT-based Security Config with Custom UserDetailsService.
- Implement Controllers:
  - `AuthController`: User registration, NGO registration, Homemaker registration, login.
  - `AdminController`: View pending verification, approve/reject NGO and Homemakers.
  - `FoodListingController`: Create, update, view food listings.
  - `OrderController`: Place orders, track status, homemaker order management.

### Phase 4: Frontend Development (Angular)

- Implement Service layer for calling REST APIs with JWT Interceptors.
- Implement Routing with AuthGuards (AdminGuard, HomemakerGuard, CustomerGuard, NgoGuard).
- UI Components:
  - Login/Registration Forms.
  - Dashboards for each role (Admin, Homemaker, Customer, NGO).
  - Food listing feed with filters.
  - Cart and Order placement functionality.

---

## Open Questions

> [!WARNING]
> Please confirm the following before I proceed:
> 1. You currently have a folder named `food_bride`. Should I create the new backend in a folder named `foodbridge-backend` alongside it, or overwrite/update the existing code?
> 2. Are you ready for me to run the command to generate the Spring Boot project now?
> 3. Should the Angular project be generated inside the same `d:\Project` directory?

## Verification Plan

### Automated Tests
- Test endpoints utilizing standard HTTP clients (e.g. Postman or `curl`).
- Verify H2 Database connectivity and schema creation.

### Manual Verification
- Walk through user registration, login, and profile verifications from the generated Angular frontend.
- Simulate placing an order and marking a free food listing as collected.
