package edu.tum.ase.boxservice.model;

import com.mongodb.lang.NonNull;

import edu.tum.ase.boxservice.controller.OrderNotFoundException;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.ArrayList;

@Document("Boxes")
public class Box {
	@Id
	private String id;
	
	@Indexed(unique = true) 
	@NonNull
	private String name;
	@NonNull
	private String addr;
	@NonNull
	private String token;
	@NonNull
	private int boxStatus;
	
	private List<Order> orders;

	public Box(String id, String name, String addr, String token) {
		this.id = id;
		this.name = name;
		this.addr = addr;
		this.token = token;
		this.boxStatus = 0;
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
		return boxStatus;
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
		this.boxStatus = newStatus;
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
