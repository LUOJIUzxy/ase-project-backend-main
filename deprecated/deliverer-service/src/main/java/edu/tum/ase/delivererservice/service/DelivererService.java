package edu.tum.ase.delivererservice.service;

import edu.tum.ase.delivererservice.model.Deliverer;
import edu.tum.ase.delivererservice.repo.DelivererCollection;
import edu.tum.ase.delivererservice.controller.DelivererNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DelivererService {
	@Autowired
	private DelivererCollection  delivererCollection;

	public List<Deliverer> getAllDeliverers() {
		return delivererCollection.findAll();
	} 

	public Deliverer getDelivererById(String email) {
		return delivererCollection.findDelivererById(email);
	}

	public void saveDeliverer(Deliverer deliverer) {
		delivererCollection.save(deliverer);
	}

	public void updateDeliverer(String id, Deliverer updatedDeliverer) {
		Deliverer d = getDelivererById(id);
		if (d == null) {
			throw new DelivererNotFoundException();
		}
		delivererCollection.delete(d);
		delivererCollection.save(updatedDeliverer);
	}

	public void updateDeliverers(String[] ids, List<Deliverer> deliverers) {
		for (int i = 0; i < ids.length; i++) {
			updateDeliverer(ids[i], deliverers.get(i));
		}
	}

	public Deliverer deleteDeliverer(String email) {
		Deliverer deletedDeliverer = getDelivererById(email);
		if (deletedDeliverer == null) {
			throw new DelivererNotFoundException();
		}
		delivererCollection.delete(deletedDeliverer);
		return deletedDeliverer;
	}

	public List<Deliverer> deleteDeliverers(String[] ids) {
		List<Deliverer> deletedDeliverers = new ArrayList<>();
		for (int i = 0; i < ids.length; i++) {
			deletedDeliverers.add(deleteDeliverer(ids[i]));
		}
		return deletedDeliverers;
	}
}
