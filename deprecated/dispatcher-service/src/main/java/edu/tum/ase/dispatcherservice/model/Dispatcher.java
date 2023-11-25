package edu.tum.ase.dispatcherservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.mongodb.lang.NonNull;

@Document("Dispatchers")
public class Dispatcher {
	@Id
	private String email;

	@Indexed(unique = true)
    @NonNull
	private String name;

	//private String token;

	public Dispatcher() {};

	public Dispatcher(String name, String email) {
		this.name = name;
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}
	/* 
	public String getToken() {
		return token;
	}
	*/
	public void setName(String newName) {
		this.name = newName;
	}

	public void setEmail(String newEmail) {
		this.email = newEmail;
	}
	/* 
	public void setToken(String token) {
		this.token = token;
	}
	*/
}