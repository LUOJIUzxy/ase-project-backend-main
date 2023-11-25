package edu.tum.ase.customerservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import com.mongodb.lang.NonNull;


@Document("Customers")
public class Customer {
	@Id
	private String email;

	@Indexed(unique = true)
    @NonNull
	private String name;
	
	private String rfid;
	private String token;

	public Customer() {}

	public Customer(String name, String email, String rfid) {
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

	public void setToken(String token) {
		this.token = token;
	}
}
