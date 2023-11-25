package edu.tum.ase.boxservice.service;

import edu.tum.ase.boxservice.model.Box;
import edu.tum.ase.boxservice.model.Order;
//import edu.tum.ase.boxservice.model.Customer;
//import edu.tum.ase.boxservice.model.Deliverer;
import edu.tum.ase.boxservice.repo.BoxCollection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class BoxService {
	@Autowired
	private BoxCollection  boxCollection;


	private static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
	/* 
	//order status values
	private static final int FREE = 0;
	private static final int OCCUPIED = 1;

	//box status values
	private static final int CREATED = 0;
	private static final int DELIVERING = 1;
	private static final int IN_BOX = 2;
	private static final int DELIVERED = 3;
	*/
    public List<Box> getAllBoxes(){
        return boxCollection.findAll();
    }

    public Box getBoxById(String boxID) {
        return boxCollection.findBoxById(boxID);
    }

	public List<Order> getAllOrders(){
		List<Order> orders = new ArrayList<>();
		List<Box> boxes = boxCollection.findAll();
		for (Box b : boxes) {
			orders.addAll(b.getOrders());
		}
		return orders;
    }

	public List<Order> getOrdersByBox(String boxID) {
		Box box = boxCollection.findBoxById(boxID);
		return box.getOrders();
	}	

	public Order getOrderById(String id) {
		List<Box> boxes = boxCollection.findAll();
		for (Box b : boxes) {
			for (Order o : b.getOrders()) {
				if (o.getId().equals(id)) {
					return o;
				}
			}
		}
		return null;
	}
	
	public List<Order> getCustomerOrders(String customerEmail) {
		List<Order> orders = new ArrayList<>();
		List<Box> boxes = boxCollection.findAll();
		for (Box b : boxes) {
			for (Order o : b.getOrders()) {
				if (o.getCustomer().getEmail().equals(customerEmail)) {
					orders.add(o);
				}
			}
		}
		return orders;
	}
	
	public List<Order> getDelivererOrders(String delivererEmail) {
		List<Order> orders = new ArrayList<>();
		List<Box> boxes = boxCollection.findAll();
		for (Box b : boxes) {
			for (Order o : b.getOrders()) {
				if (o.getDeliverer().getEmail().equals(delivererEmail)) {
					orders.add(o);
				}
			}
		}
		return orders;
	}

    public Box saveBox(Box box){
		boxCollection.save(box);
		return box;
    }

	public void addNewOrder(String boxID, Order newOrder){
		String timeStamp = dateFormatter.format(LocalDateTime.now());
		newOrder.setTimeStamp(timeStamp);
		Box b = getBoxById(boxID);
		boxCollection.delete(b);
		b.addOrder(newOrder);
		boxCollection.save(b);
    }

	public void updateBox(String id, Box updatedBox) {
		Box b = getBoxById(id);
		boxCollection.delete(b);
		boxCollection.save(updatedBox);
	}

	public void updateBoxes(String[] ids, List<Box> boxes) {
		for (int i = 0; i < ids.length; i++) {
			updateBox(ids[i], boxes.get(i));
		}
	}
	
	public void updateOrder(String boxID, String orderID, Order updatedOrder) {
		Box b = getBoxById(boxID);
		b.updateOrder(orderID, updatedOrder);
		updateBox(boxID, b);
	}

	public void updateOrders(String boxId, String[] orderIds, List<Order> orders) {
		for (int i = 0; i < orderIds.length; i++) {
			updateOrder(boxId, orderIds[i], orders.get(i));
		}
	}

	public Box deleteBox(String id) {
		Box deletedBox = boxCollection.findBoxById(id);
		boxCollection.delete(deletedBox);
		return deletedBox;
	}

	public List<Box> deleteBoxes(String[] ids) {
		List<Box> deletedBoxes = new ArrayList<>();
		for (int i = 0; i < ids.length; i++) {
			deletedBoxes.add(deleteBox(ids[i]));
		}
		return deletedBoxes;
	}

	public Order deleteOrder(String boxID, String orderID) {
		Box b = getBoxById(boxID);
		Order deletedOrder = b.removerOrder(orderID);
		updateBox(boxID, b);
		return deletedOrder;
	}

	public List<Order> deleteOrders(String boxId, String[] orderIds) {
		List<Order> deletedOrders = new ArrayList<>();
		for (int i = 0; i < orderIds.length; i++) {
			deletedOrders.add(deleteOrder(boxId, orderIds[i]));
		}
		return deletedOrders;
	}
}
