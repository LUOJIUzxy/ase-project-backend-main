package com.tum.ase.exception.notfound;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Deliverer not found")
public class DelivererNotFoundException extends RuntimeException {
	
}
