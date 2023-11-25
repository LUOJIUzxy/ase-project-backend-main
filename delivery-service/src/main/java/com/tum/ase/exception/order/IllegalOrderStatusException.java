package com.tum.ase.exception.order;

public class IllegalOrderStatusException extends RuntimeException {

    public IllegalOrderStatusException(String message) {
        super(message);
    }

}
