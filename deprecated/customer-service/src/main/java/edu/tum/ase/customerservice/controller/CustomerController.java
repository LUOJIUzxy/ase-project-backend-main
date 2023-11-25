package edu.tum.ase.customerservice.controller;

import edu.tum.ase.customerservice.constant.UserRole;
import edu.tum.ase.customerservice.model.Customer;
import edu.tum.ase.customerservice.model.Order;
import edu.tum.ase.customerservice.service.CustomerService;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;

import java.util.List;

@RestController
@RequestMapping(path = "", produces = {MediaType.APPLICATION_JSON_VALUE})
public class CustomerController {
	
	private final static String BOX_URL = "http://box-service:3001";

	@Autowired
	CustomerService customerService;

	@GetMapping("/customers")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')")
    public List<Customer> getAllCustomers() {
		return customerService.getAllCustomers();
    }

    @GetMapping("/customer/{id}")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')" +
	"|| hasAuthority('" + UserRole.CUSTOMER + "')")
    public Customer getCustomerById(@PathVariable String id) throws CustomerNotFoundException {
        Customer customer =  customerService.getCustomerById(id);
		if (customer == null) {
			throw new CustomerNotFoundException(); 
		}
		else {
			return customer; 
		}
    }
	
	@GetMapping("/customer/{id}/orders")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')" +
	"|| hasAuthority('" + UserRole.CUSTOMER + "')")
    public List<Order> getOrdersByCustomer(@PathVariable String id) {
		ResponseEntity<List<Order>> response = new RestTemplate().exchange(BOX_URL + "/customer/" + id + "/orders", HttpMethod.GET, null, new ParameterizedTypeReference<List<Order>>() {});
		List<Order> orders =  response.getBody();
		return orders;
    }
	
	@GetMapping("/order/{id}")
    @PreAuthorize("hasAuthority('" + UserRole.CUSTOMER + "')")
    public Order getOrderById(@PathVariable String id) {
        Order order=  new RestTemplate().getForObject(BOX_URL + "/order/" + id, Order.class);
		return order;
    }
 	
    @PostMapping("/customer")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')")
    public Customer createCustomer(@RequestBody Customer newCustomer) {
		customerService.saveCustomer(newCustomer);
		return newCustomer;
    }

    @PutMapping("/customer/{id}")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')")
    public Customer updateCustomer(@PathVariable String id, @RequestBody Customer updatedCustomer) throws CustomerNotFoundException {
        customerService.updateCustomer(id, updatedCustomer);
		return updatedCustomer;
    }

	@PutMapping("/customers/{ids}")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')")
    //@PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')")
    public List<Customer> updateCustomers(@PathVariable String[] ids, @RequestBody List<Customer> updatedCustomers) throws CustomerNotFoundException {
        customerService.updateCustomers(ids, updatedCustomers);
		return updatedCustomers;
    }

    @DeleteMapping("/customer/{id}")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')")
    public Customer deleteCustomer(@PathVariable String id) throws CustomerNotFoundException {
        return customerService.deleteCustomer(id);
    }

	@DeleteMapping("/customers/{ids}")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')")
    public List<Customer> deleteCustomer(@PathVariable String[] ids) throws CustomerNotFoundException {
        return customerService.deleteCustomers(ids);
    }
}
