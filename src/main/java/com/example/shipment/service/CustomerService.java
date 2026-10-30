package com.example.shipment.service;

import com.example.shipment.dto.CustomerRequest;
import com.example.shipment.dto.CustomerResponse;
import com.example.shipment.entity.Customer;
import com.example.shipment.exception.DuplicateResourceException;
import com.example.shipment.exception.ResourceNotFoundException;
import com.example.shipment.repository.CustomerRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public CustomerResponse createCustomer(CustomerRequest request) {
        if (customerRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Customer with email '" + request.email() + "' already exists");
        }
        Customer customer = new Customer(request.customerName(), request.email());
        if (request.shipments() != null) {
            request.shipments().forEach(shipment -> customer.addShipment(shipment.toEntity()));
        }
        // cascade = ALL: inserts the customer, then one shipments row per shipment (with its customer_id)
        return CustomerResponse.from(customerRepository.save(customer));
    }

    @Transactional(readOnly = true)
    public List<CustomerResponse> getAllCustomers() {
        return customerRepository.findAll().stream().map(CustomerResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public CustomerResponse getCustomer(Long customerId) {
        return CustomerResponse.from(findCustomer(customerId));
    }

    /** Updates customer details only; shipments are managed through the shipment endpoints. */
    public CustomerResponse updateCustomer(Long customerId, CustomerRequest request) {
        Customer customer = findCustomer(customerId);
        if (customerRepository.existsByEmailAndCustomerIdNot(request.email(), customerId)) {
            throw new DuplicateResourceException("Customer with email '" + request.email() + "' already exists");
        }
        customer.setCustomerName(request.customerName());
        customer.setEmail(request.email());
        return CustomerResponse.from(customer); // dirty checking issues the UPDATE on commit
    }

    public void deleteCustomer(Long customerId) {
        // cascade = ALL: all shipments of the customer are deleted first, then the customer
        customerRepository.delete(findCustomer(customerId));
    }

    private Customer findCustomer(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id " + customerId));
    }
}
