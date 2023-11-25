package edu.tum.ase.dispatcherservice.service;

import edu.tum.ase.dispatcherservice.model.Dispatcher;
import edu.tum.ase.dispatcherservice.repo.DispatcherCollection;
import edu.tum.ase.dispatcherservice.controller.DispatcherNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DispatcherService {
	@Autowired
	private DispatcherCollection  dispatcherCollection;

	public List<Dispatcher> getAllDispatchers() {
		return dispatcherCollection.findAll();
	} 

	public Dispatcher getDispatcherById(String id) {
		return dispatcherCollection.findDispatcherById(id);
	}

	public void saveDispatcher(Dispatcher dispatcher) {
		dispatcherCollection.save(dispatcher);
	}

	public void updateDispatcher(String id, Dispatcher updatedDispatcher) {
		Dispatcher d = getDispatcherById(id);
		if (d == null) {
			throw new DispatcherNotFoundException();
		}
		dispatcherCollection.delete(d);
		dispatcherCollection.save(updatedDispatcher);
	}

	public void updateDispatchers(String[] ids, List<Dispatcher> dispatchers) {
		for (int i = 0; i < ids.length; i++) {
			updateDispatcher(ids[i], dispatchers.get(i));
		}
	}

	public Dispatcher deleteDispatcher(String id) {
		Dispatcher deletedDispatcher = getDispatcherById(id);
		if (deletedDispatcher == null) {
			throw new DispatcherNotFoundException();
		}
		dispatcherCollection.delete(deletedDispatcher);
		return deletedDispatcher;
	}

	public List<Dispatcher> deleteDispatchers(String[] ids) {
		List<Dispatcher> deletedDispatchers = new ArrayList<>();
		for (int i = 0; i < ids.length; i++) {
			deletedDispatchers.add(deleteDispatcher(ids[i]));
		}
		return deletedDispatchers;
	}
}
