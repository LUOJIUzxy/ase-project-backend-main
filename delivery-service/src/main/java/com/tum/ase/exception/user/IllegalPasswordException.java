package com.tum.ase.exception.user;

public class IllegalPasswordException extends RuntimeException{

    public IllegalPasswordException(String message) {
        super(message);
    }
}
