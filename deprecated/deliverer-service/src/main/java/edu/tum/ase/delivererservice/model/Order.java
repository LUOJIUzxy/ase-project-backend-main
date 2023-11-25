package edu.tum.ase.delivererservice.model;

public class Order {
	private String orderId;
	private int orderStatus;
	private String timeStamp;
	private Customer customer;
	private Deliverer deliverer;
	//private QR qr;

	public Order() {}

	public Order(String orderId, Customer customer, Deliverer deliverer) {
		this.orderId = orderId;
		orderStatus = 0;
		this.customer = customer;
		this.deliverer = deliverer;
		//qr = new QR(customer, deliverer, box);
	}

	public String getId() {
		return orderId;
	}

	public int getStatus() {
		return orderStatus;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public Customer getCustomer() {
		return customer;
	}

	public Deliverer getDeliverer() {
		return deliverer;
	}
	/* 
	public QR getQR() {
		return qr;
	}
	*/
	public void setID(String newID) {
		this.orderId = newID;
	}

	public void setStatus(int newStatus) {
		this.orderStatus = newStatus;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public void setCustomer(Customer newCustomer) {
		this.customer = newCustomer;
	}

	public void setDeliverer(Deliverer newDeliverer) {
		this.deliverer = newDeliverer;
	}
	/* 
	public void gernerateNewQR() {
		qr = new QR(customer, deliverer, box);
	}*/
}
