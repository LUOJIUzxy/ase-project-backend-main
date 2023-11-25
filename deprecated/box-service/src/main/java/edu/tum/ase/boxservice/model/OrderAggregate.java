package edu.tum.ase.boxservice.model;

import java.util.List;

import org.springframework.data.annotation.Id;

public class OrderAggregate {
	private @Id String userID;
	private List<Order> orders;
	
	public OrderAggregate(String userID, List<Order> orders) {
		this.userID = userID;
		this.orders = orders;
	}

	public List<Order> getOrders() {
		return orders;
	}
}
