package com.tum.ase.exception.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN, reason = "Fail to update authentication credential")
public class FailUpdateCredentialException extends RuntimeException{

    public FailUpdateCredentialException(String message) {
        super(message);
    }
}
