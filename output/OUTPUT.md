# Q2 - Customer Shipment (One-to-Many / Many-to-One) — Run output

| | |
|---|---|
| Base URL | `http://localhost:8080` |
| Collection | `postman/Customer-Shipment-OneToMany.postman_collection.json` |
| Date | 2026-10-30 |
| Requests | 16 |
| Test results | **27 passed, 0 failed** |

Real output from running this project against MySQL: every request of the Postman collection, with what was sent and what came back.

---

## API requests and responses

### 1. Create Customer with Shipments

`POST /customers` → **201 Created**

**Request body**

```json
{
  "customerName": "Anita Desai",
  "email": "anita.1793335383@example.com",
  "shipments": [
    { "source": "Chennai", "destination": "Bengaluru", "status": "IN_TRANSIT" },
    { "source": "Chennai", "destination": "Hyderabad" }
  ]
}
```

**Response** — headers: `location: http://localhost:8080/customers/1`

```json
{
  "customerId": 1,
  "customerName": "Anita Desai",
  "email": "anita.1793335383@example.com",
  "shipments": [
    {
      "shipmentId": 1,
      "source": "Chennai",
      "destination": "Bengaluru",
      "status": "IN_TRANSIT"
    },
    {
      "shipmentId": 2,
      "source": "Chennai",
      "destination": "Hyderabad",
      "status": "CREATED"
    }
  ]
}
```

**Tests**

- ✅ 201 Created
- ✅ both shipments saved (cascade)
- ✅ status defaults to CREATED

### 2. Get All Customers

`GET /customers` → **200 OK**

**Response**

```json
[
  {
    "customerId": 1,
    "customerName": "Anita Desai",
    "email": "anita.1793335383@example.com",
    "shipments": [
      {
        "shipmentId": 1,
        "source": "Chennai",
        "destination": "Bengaluru",
        "status": "IN_TRANSIT"
      },
      {
        "shipmentId": 2,
        "source": "Chennai",
        "destination": "Hyderabad",
        "status": "CREATED"
      }
    ]
  }
]
```

**Tests**

- ✅ 200 OK
- ✅ returns a list

### 3. Get Customer by ID (with shipments)

`GET /customers/1` → **200 OK**

**Response**

```json
{
  "customerId": 1,
  "customerName": "Anita Desai",
  "email": "anita.1793335383@example.com",
  "shipments": [
    {
      "shipmentId": 1,
      "source": "Chennai",
      "destination": "Bengaluru",
      "status": "IN_TRANSIT"
    },
    {
      "shipmentId": 2,
      "source": "Chennai",
      "destination": "Hyderabad",
      "status": "CREATED"
    }
  ]
}
```

**Tests**

- ✅ 200 OK
- ✅ customer has 2 shipments

### 4. Update Customer

`PUT /customers/1` → **200 OK**

**Request body**

```json
{
  "customerName": "Anita Desai Iyer",
  "email": "anita.1793335383@example.com"
}
```

**Response**

```json
{
  "customerId": 1,
  "customerName": "Anita Desai Iyer",
  "email": "anita.1793335383@example.com",
  "shipments": [
    {
      "shipmentId": 1,
      "source": "Chennai",
      "destination": "Bengaluru",
      "status": "IN_TRANSIT"
    },
    {
      "shipmentId": 2,
      "source": "Chennai",
      "destination": "Hyderabad",
      "status": "CREATED"
    }
  ]
}
```

**Tests**

- ✅ 200 OK
- ✅ name updated

### 5. Add Shipment to Customer

`POST /customers/1/shipments` → **201 Created**

**Request body**

```json
{
  "source": "Chennai",
  "destination": "Kochi",
  "status": "CREATED"
}
```

**Response** — headers: `location: http://localhost:8080/shipments/3`

```json
{
  "shipmentId": 3,
  "source": "Chennai",
  "destination": "Kochi",
  "status": "CREATED",
  "customer": {
    "customerId": 1,
    "customerName": "Anita Desai Iyer",
    "email": "anita.1793335383@example.com"
  }
}
```

**Tests**

- ✅ 201 Created
- ✅ shipment belongs to the customer

### 6. Get Shipments of Customer

`GET /customers/1/shipments` → **200 OK**

**Response**

```json
[
  {
    "shipmentId": 1,
    "source": "Chennai",
    "destination": "Bengaluru",
    "status": "IN_TRANSIT",
    "customer": {
      "customerId": 1,
      "customerName": "Anita Desai Iyer",
      "email": "anita.1793335383@example.com"
    }
  },
  {
    "shipmentId": 2,
    "source": "Chennai",
    "destination": "Hyderabad",
    "status": "CREATED",
    "customer": {
      "customerId": 1,
      "customerName": "Anita Desai Iyer",
      "email": "anita.1793335383@example.com"
    }
  },
  {
    "shipmentId": 3,
    "source": "Chennai",
    "destination": "Kochi",
    "status": "CREATED",
    "customer": {
      "customerId": 1,
      "customerName": "Anita Desai Iyer",
      "email": "anita.1793335383@example.com"
    }
  }
]
```

**Tests**

- ✅ 200 OK
- ✅ customer now has 3 shipments

### 7. Get All Shipments

`GET /shipments` → **200 OK**

**Response**

```json
[
  {
    "shipmentId": 1,
    "source": "Chennai",
    "destination": "Bengaluru",
    "status": "IN_TRANSIT",
    "customer": {
      "customerId": 1,
      "customerName": "Anita Desai Iyer",
      "email": "anita.1793335383@example.com"
    }
  },
  {
    "shipmentId": 2,
    "source": "Chennai",
    "destination": "Hyderabad",
    "status": "CREATED",
    "customer": {
      "customerId": 1,
      "customerName": "Anita Desai Iyer",
      "email": "anita.1793335383@example.com"
    }
  },
  {
    "shipmentId": 3,
    "source": "Chennai",
    "destination": "Kochi",
    "status": "CREATED",
    "customer": {
      "customerId": 1,
      "customerName": "Anita Desai Iyer",
      "email": "anita.1793335383@example.com"
    }
  }
]
```

**Tests**

- ✅ 200 OK
- ✅ returns a list

### 8. Get Shipment by ID (with its customer)

`GET /shipments/3` → **200 OK**

**Response**

```json
{
  "shipmentId": 3,
  "source": "Chennai",
  "destination": "Kochi",
  "status": "CREATED",
  "customer": {
    "customerId": 1,
    "customerName": "Anita Desai Iyer",
    "email": "anita.1793335383@example.com"
  }
}
```

**Tests**

- ✅ 200 OK
- ✅ shipment shows its one customer

### 9. Update Shipment Status

`PUT /shipments/3` → **200 OK**

**Request body**

```json
{
  "source": "Chennai",
  "destination": "Kochi",
  "status": "DELIVERED"
}
```

**Response**

```json
{
  "shipmentId": 3,
  "source": "Chennai",
  "destination": "Kochi",
  "status": "DELIVERED",
  "customer": {
    "customerId": 1,
    "customerName": "Anita Desai Iyer",
    "email": "anita.1793335383@example.com"
  }
}
```

**Tests**

- ✅ 200 OK
- ✅ status is DELIVERED

### 10. Delete Shipment

`DELETE /shipments/3` → **204 No Content**

**Response**

_(empty body)_

**Tests**

- ✅ 204 No Content

### 11. Get Customer after Shipment Delete

`GET /customers/1` → **200 OK**

**Response**

```json
{
  "customerId": 1,
  "customerName": "Anita Desai Iyer",
  "email": "anita.1793335383@example.com",
  "shipments": [
    {
      "shipmentId": 1,
      "source": "Chennai",
      "destination": "Bengaluru",
      "status": "IN_TRANSIT"
    },
    {
      "shipmentId": 2,
      "source": "Chennai",
      "destination": "Hyderabad",
      "status": "CREATED"
    }
  ]
}
```

**Tests**

- ✅ 200 OK
- ✅ customer still exists with 2 shipments

### 12. Duplicate Email (409)

`POST /customers` → **409 Conflict**

**Request body**

```json
{
  "customerName": "Someone Else",
  "email": "anita.1793335383@example.com"
}
```

**Response**

```json
{
  "type": "about:blank",
  "title": "Duplicate Resource",
  "status": 409,
  "detail": "Customer with email 'anita.1793335383@example.com' already exists",
  "instance": "/customers"
}
```

**Tests**

- ✅ 409 Conflict

### 13. Invalid Shipment Status (400)

`POST /customers/1/shipments` → **400 Bad Request**

**Request body**

```json
{
  "source": "Chennai",
  "destination": "Madurai",
  "status": "LOST"
}
```

**Response**

```json
{
  "type": "about:blank",
  "title": "Malformed Request",
  "status": 400,
  "detail": "Invalid value 'LOST'. Allowed values: [CREATED, IN_TRANSIT, DELIVERED, CANCELLED]",
  "instance": "/customers/1/shipments"
}
```

**Tests**

- ✅ 400 Bad Request

### 14. Delete Customer (cascades to shipments)

`DELETE /customers/1` → **204 No Content**

**Response**

_(empty body)_

**Tests**

- ✅ 204 No Content

### 15. Get Deleted Customer (404)

`GET /customers/1` → **404 Not Found**

**Response**

```json
{
  "type": "about:blank",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Customer not found with id 1",
  "instance": "/customers/1"
}
```

**Tests**

- ✅ 404 Not Found

### 16. Get Shipment of Deleted Customer (404 - removed by cascade)

`GET /shipments/1` → **404 Not Found**

**Response**

```json
{
  "type": "about:blank",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Shipment not found with id 1",
  "instance": "/shipments/1"
}
```

**Tests**

- ✅ 404 Not Found

## MySQL tables

```text
Sample API calls: POST /customers (x2), POST /customers/2/shipments

mysql> SHOW CREATE TABLE customers;
CREATE TABLE `customers` (
  `customer_id` bigint NOT NULL AUTO_INCREMENT,
  `customer_name` varchar(100) NOT NULL,
  `email` varchar(150) NOT NULL,
  PRIMARY KEY (`customer_id`),
  UNIQUE KEY `UKrfbvkrffamfql7cjmen8v976v` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci

mysql> SHOW CREATE TABLE shipments;
CREATE TABLE `shipments` (
  `shipment_id` bigint NOT NULL AUTO_INCREMENT,
  `destination` varchar(100) NOT NULL,
  `source` varchar(100) NOT NULL,
  `status` enum('CANCELLED','CREATED','DELIVERED','IN_TRANSIT') NOT NULL,
  `customer_id` bigint NOT NULL,
  PRIMARY KEY (`shipment_id`),
  KEY `FK4riardtm02dseyjfo3vjrg3o0` (`customer_id`),
  CONSTRAINT `FK4riardtm02dseyjfo3vjrg3o0` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci

mysql> SELECT * FROM customers;
+-------------+---------------+--------------------+
| customer_id | customer_name | email              |
+-------------+---------------+--------------------+
|           1 | Anita Desai   | anita@example.com  |
|           2 | Vikram Rao    | vikram@example.com |
+-------------+---------------+--------------------+

mysql> SELECT * FROM shipments;
+-------------+-------------+---------+------------+-------------+
| shipment_id | destination | source  | status     | customer_id |
+-------------+-------------+---------+------------+-------------+
|           1 | Bengaluru   | Chennai | IN_TRANSIT |           1 |
|           2 | Hyderabad   | Chennai | CREATED    |           1 |
|           3 | Pune        | Mumbai  | CREATED    |           2 |
+-------------+-------------+---------+------------+-------------+

mysql> SELECT c.customer_id, c.customer_name, COUNT(s.shipment_id) AS shipments FROM customers c LEFT JOIN shipments s ON s.customer_id = c.customer_id GROUP BY c.customer_id, c.customer_name;
+-------------+---------------+-----------+
| customer_id | customer_name | shipments |
+-------------+---------------+-----------+
|           1 | Anita Desai   |         2 |
|           2 | Vikram Rao    |         1 |
+-------------+---------------+-----------+

mysql> SELECT s.shipment_id, s.source, s.destination, s.status, c.customer_name FROM shipments s JOIN customers c ON c.customer_id = s.customer_id ORDER BY s.shipment_id;
+-------------+---------+-------------+------------+---------------+
| shipment_id | source  | destination | status     | customer_name |
+-------------+---------+-------------+------------+---------------+
|           1 | Chennai | Bengaluru   | IN_TRANSIT | Anita Desai   |
|           2 | Chennai | Hyderabad   | CREATED    | Anita Desai   |
|           3 | Mumbai  | Pune        | CREATED    | Vikram Rao    |
+-------------+---------+-------------+------------+---------------+

After DELETE /customers/1: the customer AND all of its shipments are removed (cascade)

mysql> SELECT * FROM customers;
+-------------+---------------+--------------------+
| customer_id | customer_name | email              |
+-------------+---------------+--------------------+
|           2 | Vikram Rao    | vikram@example.com |
+-------------+---------------+--------------------+

mysql> SELECT * FROM shipments;
+-------------+-------------+--------+---------+-------------+
| shipment_id | destination | source | status  | customer_id |
+-------------+-------------+--------+---------+-------------+
|           3 | Pune        | Mumbai | CREATED |           2 |
+-------------+-------------+--------+---------+-------------+

```
