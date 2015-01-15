package com.cgz.capa.exceptions;

/**
 * Created by czarek on 06/01/15.
 */
public class ServiceException extends Exception {

    public ServiceException(Throwable cause) {
        super(cause);
    }

    public ServiceException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public ServiceException(String message) {
        super(message);
    }
}
