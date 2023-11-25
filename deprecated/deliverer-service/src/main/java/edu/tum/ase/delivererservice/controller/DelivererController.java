package edu.tum.ase.delivererservice.controller;

import edu.tum.ase.delivererservice.constant.UserRole;
import edu.tum.ase.delivererservice.model.Deliverer;
import edu.tum.ase.delivererservice.model.Order;
import edu.tum.ase.delivererservice.service.DelivererService;

import org.springframework.http.HttpEntity;
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
public class DelivererController {
	private static RestTemplate restTemplate = new RestTemplate();
	private final static String BOX_URL = "http://box-service:3001";

	@Autowired
	DelivererService delivererService;

	@GetMapping("/deliverers")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')")
    public List<Deliverer> getAllDeliverers() {
		return delivererService.getAllDeliverers();
    }

    @GetMapping("/deliverer/{id}")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')" +
	"|| hasAuthority('" + UserRole.DELIVERER + "')")
    public Deliverer getDelivererById(@PathVariable String id) {
        Deliverer deliverer =  delivererService.getDelivererById(id);
		if (deliverer == null) {
			throw new DelivererNotFoundException(); 
		}
		else {
			return deliverer; 
		}
    }
	 
	@GetMapping("/deliverer/{id}/orders")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')" +
	"|| hasAuthority('" + UserRole.DELIVERER + "')")
    public List<Order> getOrderByDeliverer(@PathVariable String id) {
		ResponseEntity<List<Order>> response = restTemplate.exchange(BOX_URL + "/deliverer/" + id + "/orders", HttpMethod.GET, null, new ParameterizedTypeReference<List<Order>>() {});
		List<Order> orders = response.getBody();
		return orders;
    }

	@GetMapping("/order/{id}")
    @PreAuthorize("hasAuthority('" + UserRole.DELIVERER + "')")
    public Order getOrderById(@PathVariable String id) {
        Order order=  restTemplate.getForObject(BOX_URL + "/order/" + id, Order.class);
		return order;
    }
	
    @PostMapping("/deliverer")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')")
    public Deliverer createDeliverer(@RequestBody Deliverer newDeliverer) {
		delivererService.saveDeliverer(newDeliverer);
		return newDeliverer;
    }

    @PutMapping("/deliverer/{id}")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')")
    public Deliverer updateDeliverer(@PathVariable String id, @RequestBody Deliverer updatedDeliverer) throws DelivererNotFoundException {
        delivererService.updateDeliverer(id, updatedDeliverer);
		return updatedDeliverer;
    }

	@PutMapping("/deliverers/{ids}")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')")
    //@PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')")
    public List<Deliverer> updateDeliverers(@PathVariable String[] ids, @RequestBody List<Deliverer> updatedDeliverers) throws DelivererNotFoundException {
        delivererService.updateDeliverers(ids, updatedDeliverers);
		return updatedDeliverers;
    }

	@PutMapping("/box/{boxId}/order/{orderId}")
    @PreAuthorize("hasAuthority('" + UserRole.DELIVERER + "')")
    public void updateOrder(@PathVariable String boxId, @PathVariable String orderId, @RequestBody Order updatedOrder) throws OrderNotFoundException {
        HttpEntity<Order> requestUpdate = new HttpEntity<>(updatedOrder);
		restTemplate.exchange(BOX_URL + "/box/" + boxId + "/order/" + orderId, HttpMethod.PUT, requestUpdate, Void.class);
    }

	@PutMapping("/box/{boxId}/orders/{orderIds}")
    @PreAuthorize("hasAuthority('" + UserRole.DELIVERER + "')")
    public void updateOrders(@PathVariable String boxId, @PathVariable String[] orderIds, @RequestBody List<Order> updatedOrders) throws OrderNotFoundException {
        //log.info(orderIds.toString());
		String ids = String.join(",", orderIds);
		HttpEntity<List<Order>> requestUpdate = new HttpEntity<>(updatedOrders);
		restTemplate.exchange(BOX_URL + "/box/" + boxId + "/orders/" + ids, HttpMethod.PUT, requestUpdate, Void.class);
    }

    @DeleteMapping("/deliverer/{id}")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')")
    public Deliverer deleteDeliverer(@PathVariable String id) throws DelivererNotFoundException {
        return delivererService.deleteDeliverer(id);
	}

	@DeleteMapping("/deliverers/{ids}")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')")
    public List<Deliverer> deleteDeliverer(@PathVariable String[] ids) throws DelivererNotFoundException {
        return delivererService.deleteDeliverers(ids);
	}
}
