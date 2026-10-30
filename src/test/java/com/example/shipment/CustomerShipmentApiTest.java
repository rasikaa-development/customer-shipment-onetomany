package com.example.shipment;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@AutoConfigureMockMvc
class CustomerShipmentApiTest {

    @Autowired MockMvc mvc;
    @Autowired JdbcTemplate jdbc;

    private ResultActions postJson(String url, String json) throws Exception {
        return mvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(json));
    }

    private ResultActions putJson(String url, String json) throws Exception {
        return mvc.perform(put(url).contentType(MediaType.APPLICATION_JSON).content(json));
    }

    private int createCustomer(String name, String email) throws Exception {
        MvcResult result = postJson("/customers", "{\"customerName\":\"%s\",\"email\":\"%s\"}".formatted(name, email))
                .andExpect(status().isCreated()).andReturn();
        return readInt(result, "$.customerId");
    }

    private static int readInt(MvcResult result, String path) throws Exception {
        return JsonPath.read(result.getResponse().getContentAsString(), path);
    }

    private int count(String sql, Object... args) {
        return jdbc.queryForObject(sql, Integer.class, args);
    }

    @Test
    void customerIsCreatedWithShipmentsAndDeletedWithThem() throws Exception {
        MvcResult created = postJson("/customers", """
                {"customerName":"Anita Desai","email":"anita@example.com","shipments":[
                  {"source":"Chennai","destination":"Bengaluru","status":"IN_TRANSIT"},
                  {"source":"Chennai","destination":"Hyderabad"}]}
                """)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.shipments", hasSize(2)))
                .andExpect(jsonPath("$.shipments[0].shipmentId").exists())
                .andExpect(jsonPath("$.shipments[0].status").value("IN_TRANSIT"))
                .andExpect(jsonPath("$.shipments[1].status").value("CREATED")) // default status
                .andReturn();
        int customerId = readInt(created, "$.customerId");
        assertEquals("http://localhost/customers/" + customerId, created.getResponse().getHeader("Location"));
        assertEquals(2, count("select count(*) from shipments where customer_id = ?", customerId));

        mvc.perform(get("/customers")).andExpect(status().isOk())
                .andExpect(jsonPath("$[*].email", hasItem("anita@example.com")));
        mvc.perform(get("/customers/" + customerId)).andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName").value("Anita Desai"))
                .andExpect(jsonPath("$.shipments[*].destination", hasItem("Hyderabad")));

        // PUT changes customer details only - shipments are untouched
        putJson("/customers/" + customerId, "{\"customerName\":\"Anita D\",\"email\":\"anita.d@example.com\"}")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("anita.d@example.com"))
                .andExpect(jsonPath("$.shipments", hasSize(2)));

        // DELETE cascades to all of the customer's shipments
        mvc.perform(delete("/customers/" + customerId)).andExpect(status().isNoContent());
        mvc.perform(get("/customers/" + customerId)).andExpect(status().isNotFound());
        assertEquals(0, count("select count(*) from shipments where customer_id = ?", customerId));
    }

    @Test
    void shipmentLifecycleForExistingCustomer() throws Exception {
        int customerId = createCustomer("Vikram Rao", "vikram@example.com");

        MvcResult created = postJson("/customers/" + customerId + "/shipments",
                "{\"source\":\"Mumbai\",\"destination\":\"Pune\"}")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.customer.customerId").value(customerId))
                .andReturn();
        int shipmentId = readInt(created, "$.shipmentId");
        assertEquals("http://localhost/shipments/" + shipmentId, created.getResponse().getHeader("Location"));
        postJson("/customers/" + customerId + "/shipments",
                "{\"source\":\"Mumbai\",\"destination\":\"Goa\",\"status\":\"DELIVERED\"}")
                .andExpect(status().isCreated());

        // one customer -> many shipments
        mvc.perform(get("/customers/" + customerId + "/shipments")).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].customer.customerId", everyItem(is(customerId))));
        mvc.perform(get("/customers/" + customerId)).andExpect(status().isOk())
                .andExpect(jsonPath("$.shipments", hasSize(2)));

        // each shipment -> exactly one customer
        mvc.perform(get("/shipments/" + shipmentId)).andExpect(status().isOk())
                .andExpect(jsonPath("$.customer.email").value("vikram@example.com"));
        mvc.perform(get("/shipments")).andExpect(status().isOk())
                .andExpect(jsonPath("$[*].shipmentId", hasItem(shipmentId)));

        putJson("/shipments/" + shipmentId, "{\"source\":\"Mumbai\",\"destination\":\"Nashik\",\"status\":\"IN_TRANSIT\"}")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.destination").value("Nashik"))
                .andExpect(jsonPath("$.status").value("IN_TRANSIT"));
        putJson("/shipments/" + shipmentId, "{\"source\":\"Mumbai\",\"destination\":\"Nashik\"}")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_TRANSIT")); // status omitted -> unchanged

        // deleting a shipment keeps the customer
        mvc.perform(delete("/shipments/" + shipmentId)).andExpect(status().isNoContent());
        mvc.perform(get("/shipments/" + shipmentId)).andExpect(status().isNotFound());
        mvc.perform(get("/customers/" + customerId)).andExpect(status().isOk())
                .andExpect(jsonPath("$.shipments", hasSize(1)))
                .andExpect(jsonPath("$.shipments[0].destination").value("Goa"));
        assertEquals(1, count("select count(*) from shipments where customer_id = ?", customerId));
    }

    @Test
    void invalidRequestsReturn400() throws Exception {
        postJson("/customers", "{\"customerName\":\"\",\"email\":\"not-an-email\"}")
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Content-Type", "application/problem+json"))
                .andExpect(jsonPath("$.errors.customerName").exists())
                .andExpect(jsonPath("$.errors.email").exists());

        postJson("/customers", """
                {"customerName":"Bad Shipment","email":"bad.shipment@example.com",
                 "shipments":[{"source":"","destination":"Delhi"}]}
                """)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors['shipments[0].source']").exists());

        int customerId = createCustomer("Status Check", "status.check@example.com");
        postJson("/customers/" + customerId + "/shipments", "{\"source\":\"A\",\"destination\":\"B\",\"status\":\"LOST\"}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail", containsString("Allowed values: [CREATED, IN_TRANSIT, DELIVERED, CANCELLED]")));
    }

    @Test
    void customerEmailMustBeUnique() throws Exception {
        createCustomer("Ravi", "ravi@example.com");
        postJson("/customers", "{\"customerName\":\"Ravi Again\",\"email\":\"ravi@example.com\"}")
                .andExpect(status().isConflict());

        int meena = createCustomer("Meena", "meena@example.com");
        putJson("/customers/" + meena, "{\"customerName\":\"Meena\",\"email\":\"ravi@example.com\"}")
                .andExpect(status().isConflict());
        putJson("/customers/" + meena, "{\"customerName\":\"Meena K\",\"email\":\"meena@example.com\"}")
                .andExpect(status().isOk());
    }

    @Test
    void unknownIdsReturn404() throws Exception {
        String shipment = "{\"source\":\"A\",\"destination\":\"B\"}";
        mvc.perform(get("/customers/999999")).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Customer not found with id 999999"));
        putJson("/customers/999999", "{\"customerName\":\"X\",\"email\":\"x@example.com\"}").andExpect(status().isNotFound());
        mvc.perform(delete("/customers/999999")).andExpect(status().isNotFound());
        postJson("/customers/999999/shipments", shipment).andExpect(status().isNotFound());
        mvc.perform(get("/customers/999999/shipments")).andExpect(status().isNotFound());
        mvc.perform(get("/shipments/999999")).andExpect(status().isNotFound());
        putJson("/shipments/999999", shipment).andExpect(status().isNotFound());
        mvc.perform(delete("/shipments/999999")).andExpect(status().isNotFound());
    }

    @Test
    void databaseRequiresAnExistingCustomerForEveryShipment() {
        // NOT NULL customer_id
        assertThrows(DataIntegrityViolationException.class, () -> jdbc.update(
                "insert into shipments (source, destination, status) values ('A', 'B', 'CREATED')"));
        // FOREIGN KEY customer_id -> customers
        assertThrows(DataIntegrityViolationException.class, () -> jdbc.update(
                "insert into shipments (source, destination, status, customer_id) values ('A', 'B', 'CREATED', 999999)"));
    }
}
