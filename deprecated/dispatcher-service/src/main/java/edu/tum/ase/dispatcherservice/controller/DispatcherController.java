package edu.tum.ase.dispatcherservice.controller;

import edu.tum.ase.dispatcherservice.constant.UserRole;
import edu.tum.ase.dispatcherservice.model.Box;
import edu.tum.ase.dispatcherservice.model.Customer;
import edu.tum.ase.dispatcherservice.model.Deliverer;
import edu.tum.ase.dispatcherservice.model.Dispatcher;
import edu.tum.ase.dispatcherservice.model.Order;
import edu.tum.ase.dispatcherservice.service.DispatcherService;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import org.slf4j.Logger; 
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;

import java.util.List;

@RestController
@RequestMapping(path = "", produces = {MediaType.APPLICATION_JSON_VALUE})
public class DispatcherController {
	private static Logger log = LoggerFactory.getLogger(DispatcherController.class);
	private static RestTemplate restTemplate = new RestTemplate();
	private final static String BOX_URL = "http://box-service:3001";
	private final static String CUSTOMER_URL = "http://customer-service:3002";
	private final static String DELIVERER_URL = "http://deliverer-service:3003";

	@Autowired
	DispatcherService dispatcherService;

	@GetMapping("/dispatchers")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')")
    public List<Dispatcher> getAllDispacher() {
		return dispatcherService.getAllDispatchers();
    }

    @GetMapping("/dispatcher/{id}")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')")
    public Dispatcher getDispacherById(@PathVariable String id) {
        Dispatcher dispacher =  dispatcherService.getDispatcherById(id);
		if (dispacher == null) {
			throw new DispatcherNotFoundException(); 
		}
		else {
			return dispacher; 
		}
    }
	
	@GetMapping("/boxes")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')")
    public List<Box> getAllBoxes() {
		ResponseEntity<List<Box>> response = restTemplate.exchange(BOX_URL + "/boxes", HttpMethod.GET, null, new ParameterizedTypeReference<List<Box>>() {});
		List<Box> boxes = response.getBody();
		if (boxes == null) {
			throw new BoxNotFoundException();
		}
		return boxes;
    }
	
	@GetMapping("/boxe/{id}")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')")
    public Box getBoxById(@PathVariable String id) throws BoxNotFoundException {
        Box box = restTemplate.getForObject(BOX_URL + "/box/" + id, Box.class);
		return box;
    }
	
	@GetMapping("/orders")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')")
    public List<Order> getAllOrders() {
		ResponseEntity<List<Order>> response = restTemplate.exchange(BOX_URL + "/orders", HttpMethod.GET, null, new ParameterizedTypeReference<List<Order>>() {});
		List<Order> orders =  response.getBody();
		return orders;
    }
	
	@GetMapping("/order/{id}")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')")
    public Order getOrderById(@PathVariable String id) throws OrderNotFoundException {
        Order order=  restTemplate.getForObject(BOX_URL + "/order/" + id, Order.class);
		return order;
    }
	
	@GetMapping("/customers")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')")
    public List<Customer> getAllCustomers() {
        ResponseEntity<List<Customer>> response = restTemplate.exchange(CUSTOMER_URL + "/cutomers", HttpMethod.GET, null, new ParameterizedTypeReference<List<Customer>>() {});
		List<Customer> customers =  response.getBody();
		return customers;
    }
	
	@GetMapping("/customer/{id}")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')")
    public Customer getCustomerById(@PathVariable String id) throws CustomerNotFoundException {
        Customer customer=  restTemplate.getForObject(CUSTOMER_URL + "/customer/" + id, Customer.class);
		return customer;
    }
	
	@GetMapping("/deliverers")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')")
    public List<Deliverer> getAllDeliverers() {
        ResponseEntity<List<Deliverer>> response = restTemplate.exchange(DELIVERER_URL + "/deliverers", HttpMethod.GET, null, new ParameterizedTypeReference<List<Deliverer>>() {});
		List<Deliverer> deliverers =  response.getBody();
		return deliverers;
    }
	
	@GetMapping("/deliverer/{id}")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')")
    public Deliverer getDelivererById(@PathVariable String id) throws DelivererNotFoundException {
        Deliverer deliverer = restTemplate.getForObject(DELIVERER_URL + "/deliverer/" + id, Deliverer.class);
		return deliverer;
    }
	
    @PostMapping("/dispatcher")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')")
    public Dispatcher createDispacher(@RequestBody Dispatcher newDispacher) {
		log.info("req body" + newDispacher);
		dispatcherService.saveDispatcher(newDispacher);
		return newDispacher;
    }
	
	@PostMapping("/box")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')")
    public Box createBox(@RequestBody Box newBox) {
        HttpEntity<Box> request = new HttpEntity<>(newBox);
		Box box = restTemplate.postForObject(BOX_URL + "/box", request, Box.class);
		if (box == null) {

		}
		return box;
    }

	@PostMapping("/box/{id}")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')")
    public Order createOrder(@PathVariable String id, @RequestBody Order newOrder) {
        HttpEntity<Order> request = new HttpEntity<>(newOrder);
		Order order = restTemplate.postForObject(BOX_URL + "/box/" + id, request, Order.class);
		if (order == null) {
			
		}
		return order;
    }
	 
	@PostMapping("/customer")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')")
    public Customer createCustomer(@RequestBody Customer newCustomer) {
        HttpEntity<Customer> request = new HttpEntity<>(newCustomer);
		Customer customer = restTemplate.postForObject(CUSTOMER_URL + "/customer", request, Customer.class);
		if (customer == null) {
			
		}
		return customer;
    }
	
	@PostMapping("/deliverer")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')")
    public Deliverer createDeliverer(@RequestBody Deliverer newDeliverer) {
        HttpEntity<Deliverer> request = new HttpEntity<>(newDeliverer);
		Deliverer deliverer = restTemplate.postForObject(DELIVERER_URL + "/deliverer", request, Deliverer.class);
		if (deliverer == null) {
			
		}
		return deliverer;
    }
	
    @PutMapping("/dispatcher/{id}")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')")
    public Dispatcher updateDispatcher(@PathVariable String id, @RequestBody Dispatcher updatedDispacher) throws DispatcherNotFoundException {
        dispatcherService.updateDispatcher(id, updatedDispacher);
		return updatedDispacher;
    }

	@PutMapping("/dispatchers/{ids}")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')")
    public List<Dispatcher> updateDispatchers(@PathVariable String[] ids, @RequestBody List<Dispatcher> updatedDispachers) throws DispatcherNotFoundException {
        dispatcherService.updateDispatchers(ids, updatedDispachers);
		return updatedDispachers;
    }
	
	@PutMapping("/box/{id}")
    public void updateBox(@PathVariable String id, @RequestBody Box updatedBox) {
		HttpEntity<Box> requestUpdate = new HttpEntity<>(updatedBox);
		restTemplate.exchange(BOX_URL + "/box/" + id, HttpMethod.PUT, requestUpdate, Void.class);
		/*if (box == null) {
			throw new BoxNotFoundExcception();
		}
		return box;*/
    }

	@PutMapping("/boxes/{ids}")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')")
    public void updateBoxes(@PathVariable String[] ids, @RequestBody List<Box> updatedBoxes) throws BoxNotFoundException {
        String boxIds = String.join(",", ids);
		log.info("joined ids");
		log.info(boxIds);
		HttpEntity<List<Box>> requestUpdate = new HttpEntity<>(updatedBoxes);
		restTemplate.exchange(BOX_URL + "/boxes/" + boxIds, HttpMethod.PUT, requestUpdate, Void.class);
    }
	
	@PutMapping("/box/{boxId}/order/{orderId}")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')")
    public void updateOrder(@PathVariable String boxId, @PathVariable String orderId, @RequestBody Order updatedOrder) throws OrderNotFoundException {
        HttpEntity<Order> requestUpdate = new HttpEntity<>(updatedOrder);
		restTemplate.exchange(BOX_URL + "/box/" + boxId + "/order/" + orderId, HttpMethod.PUT, requestUpdate, Void.class);
    }

	@PutMapping("/box/{boxId}/orders/{orderIds}")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')")
    public void updateOrders(@PathVariable String boxId, @PathVariable String[] orderIds, @RequestBody List<Order> updatedOrders) throws OrderNotFoundException {
        log.info(orderIds.toString());
		String ids = String.join(",", orderIds);
		HttpEntity<List<Order>> requestUpdate = new HttpEntity<>(updatedOrders);
		restTemplate.exchange(BOX_URL + "/box/" + boxId + "/orders/" + ids, HttpMethod.PUT, requestUpdate, Void.class);
    }
	
	@PutMapping("/customer/{id}")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')")
    public void updateCustomer(@PathVariable String id, @RequestBody Customer updatedCustomer) throws CustomerNotFoundException {
        HttpEntity<Customer> requestUpdate = new HttpEntity<>(updatedCustomer);
		restTemplate.exchange(CUSTOMER_URL + "/customer/" + id, HttpMethod.PUT, requestUpdate, Void.class);
    }

	@PutMapping("/customers/{ids}")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')")
    public void updateCustomers(@PathVariable String[] ids, @RequestBody List<Customer> updatedCustomers) throws CustomerNotFoundException {
        String customerIds = String.join(",", ids);
		HttpEntity<List<Customer>> requestUpdate = new HttpEntity<>(updatedCustomers);
		restTemplate.exchange(CUSTOMER_URL + "/customers/" + customerIds, HttpMethod.PUT, requestUpdate, Void.class);
    }
	
	@PutMapping("/deliverer/{id}")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')")
    public void updateDeliverer(@PathVariable String id, @RequestBody Deliverer updatedDeliverer) throws DelivererNotFoundException {
        HttpEntity<Deliverer> requestUpdate = new HttpEntity<>(updatedDeliverer);
		restTemplate.exchange(DELIVERER_URL + "/deliverer/" + id, HttpMethod.PUT, requestUpdate, Void.class);
    }

	@PutMapping("/deliverers/{ids}")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')")
    public void updateDeliverers(@PathVariable String[] ids, @RequestBody List<Deliverer> updatedDeliverers) throws DelivererNotFoundException {
        String delivererIds = String.join(",", ids);
		HttpEntity<List<Deliverer>> requestUpdate = new HttpEntity<>(updatedDeliverers);
		restTemplate.exchange(DELIVERER_URL + "/deliverers/" + delivererIds, HttpMethod.PUT, requestUpdate, Void.class);
    }
	
    @DeleteMapping("/dispatcher/{id}")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')")
    public Dispatcher deleteDispacher(@PathVariable String id) throws DispatcherNotFoundException {
        return dispatcherService.deleteDispatcher(id);
    }

	@DeleteMapping("/dispatchers/{ids}")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')")
    public List<Dispatcher> deleteDispachers(@PathVariable String[] ids) throws DispatcherNotFoundException {
        return dispatcherService.deleteDispatchers(ids);
    }
	 
	@DeleteMapping("/box/{id}")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')")
    public void deleteBox(@PathVariable String id) {
        restTemplate.delete(BOX_URL + "/box/" + id);
    }

	@DeleteMapping("/boxes/{ids}")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')")
    public void deleteBoxes(@PathVariable String ids) throws BoxNotFoundException {
        String boxIds = String.join(",", ids);
		restTemplate.delete(BOX_URL + "/boxes/" + boxIds);
    }

	@DeleteMapping("/box/{boxID}/order/{orderID}")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')")
    public void deleteOrder(@PathVariable String boxId, @PathVariable String orderId) throws OrderNotFoundException {
        restTemplate.delete(BOX_URL + "/box/" + boxId + "/order/" + orderId);
    }

	@DeleteMapping("/box/{boxId}/orders/{orderIds}")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')")
    public void deleteOrders(@PathVariable String boxId, @PathVariable String[] orderIds) throws OrderNotFoundException {
        String ids = String.join(",", orderIds);
		restTemplate.delete(BOX_URL + "/box/" + boxId + "/orders/" + ids);
    }

	
	@DeleteMapping("/customer/{id}")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')")
    public void deleteCustomer(@PathVariable String id) throws CustomerNotFoundException {
        restTemplate.delete(CUSTOMER_URL + "/customer/" + id);
    }

	@DeleteMapping("/customers/{ids}")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')")
    public void deleteCustomer(@PathVariable String[] ids) throws CustomerNotFoundException {
        String customerIds = String.join(",", ids);
		restTemplate.delete(CUSTOMER_URL + "/customers/" + customerIds);
    }
	
	@DeleteMapping("/deliverer/{id}")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')")
    public void deleteDeliverer(@PathVariable String id) throws DelivererNotFoundException {
        restTemplate.delete(DELIVERER_URL + "/deliverer/" + id);
    }

	@DeleteMapping("/deliverers/{ids}")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')")
    public void deleteDeliverer(@PathVariable String[] ids) throws DelivererNotFoundException {
		String delivererIds = String.join(",", ids);
		restTemplate.delete(DELIVERER_URL + "/deliverers/" + delivererIds);
	}
	
}
