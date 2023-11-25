package com.tum.ase.exception.notfound;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Box not found")
public class BoxNotFoundException extends RuntimeException {

    public BoxNotFoundException(String message) {
    }

    public BoxNotFoundException() {

    }
}
