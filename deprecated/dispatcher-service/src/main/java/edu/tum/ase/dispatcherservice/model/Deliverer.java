package edu.tum.ase.dispatcherservice.model;

public class Deliverer {
	private String name;
	private String email;
	private String rfid;
	private String token;

	public Deliverer() {}

	public Deliverer(String name, String email, String rfid) {
		this.name = name;
		this.email = email;
		this.rfid = rfid;
	}

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}

	public String getRFID() {
		return rfid;
	}

	public String getToken() {
		return token;
	}

	public void setName(String newName) {
		this.name = newName;
	}

	public void setEmail(String newEmail) {
		this.email = newEmail;
	}

	public void setRFID(String newRFID) {
		this.rfid = newRFID;
	}

	public void setToken(String newToken) {
		this.token = newToken;
	}
}
