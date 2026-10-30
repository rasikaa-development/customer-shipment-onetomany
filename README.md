# Q2 – Customer Shipment Management System (One-to-Many / Many-to-One)

Spring Boot 3.5 + Spring Data JPA + MySQL. A **Customer** can have many **Shipments**, and each
Shipment belongs to exactly one Customer.

## Mapping

```
customers                       shipments
+---------------+               +-------------+
| customer_id   | PK  1 ----- * | shipment_id | PK
| customer_name |               | source      |
| email UNIQUE  |               | destination |
+---------------+               | status      | CREATED / IN_TRANSIT / DELIVERED / CANCELLED
                                | customer_id | FK, NOT NULL
                                +-------------+
```

| Side | Code | Meaning |
|------|------|---------|
| `Customer` – **One-to-Many** (inverse) | `@OneToMany(mappedBy = "customer", cascade = ALL, orphanRemoval = true)`<br>`List<Shipment> shipments` | No column. `mappedBy` points to the FK owner, so no join table is created. |
| `Shipment` – **Many-to-One** (owning) | `@ManyToOne(fetch = LAZY, optional = false)`<br>`@JoinColumn(name = "customer_id", nullable = false)` | Holds the FK. `NOT NULL` means every shipment has exactly one customer. |

- **Keeping both sides in sync:** always use `customer.addShipment(s)` and `customer.removeShipment(s)`. These set both the list and the FK.
- **Cascade:** `POST /customers` can create the customer and its shipments in one request. Deleting a customer deletes all of its shipments.
- **Deleting a shipment:** `orphanRemoval = true`, so removing a shipment from the customer's list deletes its row.
- **No N+1:** `@EntityGraph` fetches customers with their shipments, and shipments with their customer, in **one** SQL query each. This was checked in the SQL log.

## Project structure

```
src/main/java/com/example/shipment
├── entity/       Customer (@OneToMany), Shipment (@ManyToOne), ShipmentStatus
├── repository/   CustomerRepository, ShipmentRepository
├── dto/          request / response records (with validation)
├── service/      CustomerService, ShipmentService
├── controller/   CustomerController (/customers), ShipmentController (/shipments)
└── exception/    404 / 409 / 400 handling (RFC 7807 problem+json)
```

## Run

Needs Java 17+, Maven, and MySQL on `localhost:3306`. The `shipmentdb` database is created automatically.

```bash
export DB_USERNAME=root DB_PASSWORD=yourPassword   # defaults: root / <empty>
mvn spring-boot:run          # app on http://localhost:8080
mvn test                     # automated API tests (in-memory H2)
```

## REST API

| Method | Endpoint | Description | Success |
|--------|----------|-------------|---------|
| POST | `/customers` | Create a customer, optionally with `shipments` | 201 |
| GET | `/customers` | All customers with their shipments | 200 |
| GET | `/customers/{id}` | One customer with its shipments | 200 |
| PUT | `/customers/{id}` | Update customer name / email | 200 |
| DELETE | `/customers/{id}` | Delete customer **and its shipments** (cascade) | 204 |
| POST | `/customers/{id}/shipments` | Add a shipment to a customer | 201 |
| GET | `/customers/{id}/shipments` | All shipments of one customer | 200 |
| GET | `/shipments` | All shipments, each with its customer | 200 |
| GET | `/shipments/{id}` | One shipment with its customer | 200 |
| PUT | `/shipments/{id}` | Update source / destination / status | 200 |
| DELETE | `/shipments/{id}` | Delete one shipment (customer is kept) | 204 |

Errors: `400` validation or bad `status` value, `404` unknown id, `409` duplicate email.
`status` is optional. It defaults to `CREATED` on create and stays unchanged on update if you leave it out.

### Sample request – `POST /customers`

```json
{
  "customerName": "Anita Desai",
  "email": "anita@example.com",
  "shipments": [
    { "source": "Chennai", "destination": "Bengaluru", "status": "IN_TRANSIT" },
    { "source": "Chennai", "destination": "Hyderabad" }
  ]
}
```

Response `201 Created`:

```json
{
  "customerId": 1,
  "customerName": "Anita Desai",
  "email": "anita@example.com",
  "shipments": [
    { "shipmentId": 1, "source": "Chennai", "destination": "Bengaluru", "status": "IN_TRANSIT" },
    { "shipmentId": 2, "source": "Chennai", "destination": "Hyderabad", "status": "CREATED" }
  ]
}
```

`GET /shipments/1` (Many-to-One view):

```json
{
  "shipmentId": 1,
  "source": "Chennai",
  "destination": "Bengaluru",
  "status": "IN_TRANSIT",
  "customer": { "customerId": 1, "customerName": "Anita Desai", "email": "anita@example.com" }
}
```

## Postman

Import `postman/Customer-Shipment-OneToMany.postman_collection.json` and run it top to bottom, or use
**Run collection**. It has 16 requests with test assertions, and the ids are saved automatically.
