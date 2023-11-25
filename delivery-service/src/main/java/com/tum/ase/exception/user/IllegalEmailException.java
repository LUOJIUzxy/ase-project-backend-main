package com.tum.ase.exception.user;

public class IllegalEmailException extends RuntimeException{

    public IllegalEmailException(String message) {
        super(message);
    }
}
