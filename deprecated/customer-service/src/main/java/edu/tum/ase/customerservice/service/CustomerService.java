package edu.tum.ase.customerservice.service;

import edu.tum.ase.customerservice.model.Customer;
import edu.tum.ase.customerservice.repo.CustomerCollection;
import edu.tum.ase.customerservice.controller.CustomerNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerService {
	@Autowired
	private CustomerCollection  customerCollection;

	public List<Customer> getAllCustomers() {
		return customerCollection.findAll();
	} 

	public Customer getCustomerById(String email) {
		return customerCollection.findCustomerById(email);
	}

	public void saveCustomer(Customer customer) {
		customerCollection.save(customer);
	}

	public void updateCustomer(String id, Customer updatedCustomer) {
		Customer c = getCustomerById(id);
		if (c == null) {
			throw new CustomerNotFoundException();
		}
		customerCollection.delete(c);
		customerCollection.save(updatedCustomer);
	}

	public void updateCustomers(String[] ids, List<Customer> updatedCustomers) {
		for (int i = 0; i < ids.length; i++) {
			updateCustomer(ids[i], updatedCustomers.get(i));
		}
	}

	public Customer deleteCustomer(String email) {
		Customer deletedCustomer = getCustomerById(email);
		if (deletedCustomer == null) {
			throw new CustomerNotFoundException();
		}
		customerCollection.delete(deletedCustomer);
		return deletedCustomer;
	}

	public List<Customer> deleteCustomers(String[] ids) {
		List<Customer> deletedCustomers = new ArrayList<>();
		for (int i = 0; i < ids.length; i++) {
			deletedCustomers.add(deleteCustomer(ids[i]));
		}
		return deletedCustomers;
	}
}
