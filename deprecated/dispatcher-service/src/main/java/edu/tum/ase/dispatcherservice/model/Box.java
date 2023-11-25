package edu.tum.ase.dispatcherservice.model;

import edu.tum.ase.dispatcherservice.controller.OrderNotFoundException;

import java.util.List;
import java.util.ArrayList;

public class Box {
	private String id;
	private String name;
	private String addr;
	private String token;
	private int status;
	
	private List<Order> orders;

	public Box() {}
	
	public Box(String id, String name, String addr, String token) {
		this.id = id;
		this.name = name;
		this.addr = addr;
		this.token = token;
		this.status = 0;
		this.orders = new ArrayList<>();
	}
	
	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getAddr() {
		return addr;
	}

	public String getToken() {
		return token;
	}

	public int getStatus() {
		return status;
	}

	public void setID(String newID) {
		this.id = newID;
	}

	public void setName(String newName) {
		this.name = newName;
	}

	public void setAddr(String newAddr) {
		this.addr = newAddr;
	}

	public void setToken(String newToken) {
		this.token = newToken;
	} 

	public void setStatus(int newStatus) {
		this.status = newStatus;
	}

	public List<Order> getOrders() {
		return orders;
	}

	public Order getOrder(String orderID) {
		for (Order o : orders) {
			if (o.getId().equals(orderID)) {
				return o;
			}
		}
		throw new OrderNotFoundException();
	}
	
	public void addOrder(Order order) {
		orders.add(order);
	}

	public void updateOrder(String orderID, Order order) {
		for (Order o : orders) {
			if (o.getId().equals(orderID)) {
				orders.remove(o);
				orders.add(order);
				return;
			}
		}
		throw new OrderNotFoundException();
	}

	public Order removerOrder(String orderID) {
		for (Order o : orders) {
			if (o.getId().equals(orderID)) {
				Order deletedOrder = o;
				orders.remove(o);
				return deletedOrder;
			}
		}
		throw new OrderNotFoundException();
	}
 }
