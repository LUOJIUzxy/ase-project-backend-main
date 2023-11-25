package com.tum.ase.exception.notfound;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Dispatcher not found")
public class DispatcherNotFoundException extends RuntimeException {
}
