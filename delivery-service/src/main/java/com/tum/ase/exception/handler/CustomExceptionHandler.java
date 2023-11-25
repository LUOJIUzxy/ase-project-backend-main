package com.tum.ase.exception.handler;

import com.tum.ase.constant.ErrorCode;
import com.tum.ase.exception.box.AddressIllegalException;
import com.tum.ase.exception.box.IllegalBoxNameException;
import com.tum.ase.exception.box.HasBeenAssignedException;
import com.tum.ase.exception.box.IllegalRfidException;
import com.tum.ase.exception.order.IllegalOrderStatusException;
import com.tum.ase.exception.order.IllegalPickupException;
import com.tum.ase.exception.order.IllegalQrCodeException;
import com.tum.ase.exception.order.IllegalTrackCodeException;
import com.tum.ase.exception.user.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(IllegalUsernameException.class)
    public String handleUsernameIllegalException(IllegalUsernameException ex, HttpServletResponse response) {
        try {
            response.sendError(ErrorCode.USERNAME_ILLEGAL);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ex.getMessage();
    }

    @ExceptionHandler(IllegalEmailException.class)
    public String handleEmailIllegalException(IllegalEmailException ex, HttpServletResponse response) {
        try {
            response.sendError(ErrorCode.EMAIL_ILLEGAL);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ex.getMessage();
    }

    @ExceptionHandler(IllegalPasswordException.class)
    public String handlePasswordIllegalException(IllegalPasswordException ex, HttpServletResponse response) {
        try {
            response.sendError(ErrorCode.PASSWORD_ILLEGAL);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ex.getMessage();
    }

    @ExceptionHandler(IllegalTokenException.class)
    public String handleTokenIllegalException(IllegalTokenException ex, HttpServletResponse response) {
        try {
            response.sendError(ErrorCode.TOKEN_ILLEGAL);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ex.getMessage();
    }

    @ExceptionHandler(IllegalRoleException.class)
    public String handleRoleIllegalException(IllegalRoleException ex, HttpServletResponse response) {
        try {
            response.sendError(ErrorCode.ROLE_ILLEGAL);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ex.getMessage();
    }

    @ExceptionHandler(IllegalBoxNameException.class)
    public void handleBoxNameIllegalException(IllegalBoxNameException ex, HttpServletResponse response) throws IOException {
        response.sendError(ErrorCode.BOX_NAME_ILLEGAL);
    }

    @ExceptionHandler(IllegalRfidException.class)
    public String handleRfidIllegalException(IllegalRfidException ex, HttpServletResponse response) {
        try {
            response.sendError(ErrorCode.TOKEN_ILLEGAL);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ex.getMessage();
    }

    @ExceptionHandler(AddressIllegalException.class)
    public String handleAddressIllegalException(AddressIllegalException ex, HttpServletResponse response) {
        try {
            response.sendError(ErrorCode.ADDRESS_ILLEGAL);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ex.getMessage();
    }

    @ExceptionHandler(IllegalQrCodeException.class)
    public String handleQrCodeIllegalException(IllegalQrCodeException ex, HttpServletResponse response) {
        try {
            response.sendError(ErrorCode.QR_ILLEGAL);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ex.getMessage();
    }

    @ExceptionHandler(IllegalTrackCodeException.class)
    public String handleTrackCodeIllegalException(IllegalTrackCodeException ex, HttpServletResponse response) {
        try {
            response.sendError(ErrorCode.TRACK_CODE_ILLEGAL);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ex.getMessage();
    }

    @ExceptionHandler(HasBeenAssignedException.class)
    public String handleHasBeenAssignedException(HasBeenAssignedException ex, HttpServletResponse response) {
        try {
            response.sendError(ErrorCode.HAS_BEEN_ASSIGNED);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ex.getMessage();
    }

    @ExceptionHandler(IllegalPickupException.class)
    public String handleIllegalPickupException(IllegalPickupException ex, HttpServletResponse response) {
        try {
            response.sendError(ErrorCode.ILLEGAL_PICKUP);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ex.getMessage();
    }

    @ExceptionHandler(IllegalOrderStatusException.class)
    public String handleIllegalOrderStatusException(IllegalOrderStatusException ex, HttpServletResponse response) {
        try {
            response.sendError(ErrorCode.ILLEGAL_ORDER_STATUS);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ex.getMessage();
    }
}
